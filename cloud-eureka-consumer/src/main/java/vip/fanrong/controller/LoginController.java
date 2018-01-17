package vip.fanrong.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vip.fanrong.Constant;
import vip.fanrong.form.LoginForm;
import vip.fanrong.form.UserLoginForm;
import vip.fanrong.model.User;
import vip.fanrong.service.CacheService;
import vip.fanrong.service.UserService;
import vip.fanrong.util.HttpUtils;
import vip.fanrong.util.MD5Utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;

/**
 * Created by Rong on 2018/1/9.
 */
@Controller
public class LoginController {

    private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private CacheService cacheService;

    // 用户登录界面
    @GetMapping("/login")
    public String get(@ModelAttribute("user") User user) {
        return "login";
    }

    //用户登录，通过email
    @PostMapping("/login")
    @ResponseBody
    public LoginForm login(@RequestBody UserLoginForm form,
                           @RequestParam("remember-me") Optional<String> rememberMe,
                           @RequestParam("next") Optional<String> next,
                           HttpServletResponse response,
                           HttpSession session) {

        LoginForm loginStatus = new LoginForm();
        String clientIP = "unknown";
        try {
            clientIP = HttpUtils.getIpAddress(httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Client is trying to login from IP=" + clientIP + " email=" + form.getEmail());

        // 登陆
        User user = userService.login(form.getEmail(), form.getPassword());

        if (user == null) {
            // 登陆失败
            loginStatus.setStatus("failed");
            loginStatus.setMessage("登陆失败！");
            return loginStatus;
        } else {
            // 登陆成功
            String token1 = MD5Utils.getMD5(user.getPassword() + "+SALT"); // 用户密码
            String token2 = MD5Utils.getMD5(clientIP + "+SALT"); // 用户登陆IP
            session.setAttribute("CURRENT_USER", user);
            session.setAttribute("USER_NAME", user.getUsername());
            session.setAttribute("TOKEN1", token1);
            session.setAttribute("TOKEN2", token2);
            session.setAttribute("IP", clientIP);

            // 添加cookie，同IP三天内免登陆
            if (rememberMe != null) {
                Cookie cookie1 = new Cookie(Constant.COOKIE_KEY_NAME_1, String.valueOf(user.getId()));
                Cookie cookie2 = new Cookie(Constant.COOKIE_KEY_NAME_2, token1);
                Cookie cookie3 = new Cookie(Constant.COOKIE_KEY_NAME_3, token2);
                cookie1.setMaxAge(60 * 60 * 24 * 3);
                cookie2.setMaxAge(60 * 60 * 24 * 3);
                cookie3.setMaxAge(60 * 60 * 24 * 3);
                response.addCookie(cookie1);
                response.addCookie(cookie2);
                response.addCookie(cookie3);
            }

            // 最后一次登陆地址
            cacheService.setToRedis("lastLogin:" + user.getId(), clientIP);

            loginStatus.setStatus("success");
            loginStatus.setMessage("登陆成功！");
            loginStatus.setInfo(next.isPresent() ? next.get() : "");
            return loginStatus;
        }
    }

    //用户登出
    @GetMapping("/logout")
    public String logout(HttpServletResponse response, HttpSession session) {
        //清空session和cookie
        Cookie cookie = new Cookie(Constant.COOKIE_KEY_NAME_1, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        session.removeAttribute("CURRENT_USER");

        //回到主页
        return "redirect:/";
    }

}
