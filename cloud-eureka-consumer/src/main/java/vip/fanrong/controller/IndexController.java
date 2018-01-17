package vip.fanrong.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vip.fanrong.Constant;
import vip.fanrong.client.KdsCrawlerClient;
import vip.fanrong.model.Blog;
import vip.fanrong.model.Tag;
import vip.fanrong.model.User;
import vip.fanrong.service.BlogService;
import vip.fanrong.service.TagService;
import vip.fanrong.service.UserService;
import vip.fanrong.util.HttpUtils;
import vip.fanrong.util.MD5Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Rong on 2018/1/9.
 */
@Controller
public class IndexController {

    @Autowired
    private KdsCrawlerClient kdsCrawlerClient;

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //网站首页
    @GetMapping("/")
    public String showMainPage(@CookieValue(Constant.COOKIE_KEY_NAME_1) Optional<String> cookieUserId,
                               @CookieValue(Constant.COOKIE_KEY_NAME_2) Optional<String> cookieToken1,
                               @CookieValue(Constant.COOKIE_KEY_NAME_3) Optional<String> cookieToken2,
                               HttpSession session,
                               Model model,
                               @RequestParam("page") Optional<Integer> page) {
        // 自动登录, IP必须跟上次登录IP一致 TODO ip区域一致更好，解决动态IP分配问题
        if (session.getAttribute("CURRENT_USER") == null
                && cookieUserId != null && cookieUserId.isPresent()
                && cookieToken1 != null && cookieToken1.isPresent()
                && cookieToken2 != null && cookieToken2.isPresent()) {

            String clientIP = "unknown";
            try {
                clientIP = HttpUtils.getIpAddress(httpServletRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!"unknown".equalsIgnoreCase(clientIP)) {
                String token2 = MD5Utils.getMD5(clientIP + "+SALT"); // 用户登陆IP验证
                if (cookieToken2.get().equalsIgnoreCase(token2)) {
                    try {
                        long id = Long.parseLong(cookieUserId.get());
                        User user = userService.getUserById(id);
                        if (user != null) {
                            String token1 = MD5Utils.getMD5(user.getPassword() + "+SALT"); // 用户密码
                            if (cookieToken1.get().equalsIgnoreCase(token1)) {
                                session.setAttribute("CURRENT_USER", user);
                                session.setAttribute("USER_NAME", user.getUsername());
                                session.setAttribute("TOKEN1", token1);
                                session.setAttribute("TOKEN2", token2);
                                session.setAttribute("IP", clientIP);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // 获取热门博客
        List<Blog> blogs = blogService.getHotBlogs(page.orElse(1));
        model.addAttribute("blogs", blogs);
        if (page.isPresent()) {
            model.addAttribute("page", page.get());
        } else {
            model.addAttribute("page", 1);
        }
        return "index";
    }

    @ResponseBody
    @GetMapping("/kds")
    public ObjectNode getKdsTopics() {
        return kdsCrawlerClient.getHotTopics(10);
    }

    //用户的个人主页
    @GetMapping("/admin/{id}")
    public String showUserPage(@PathVariable("id") long id, Model model) {
        model.addAttribute("blogs", blogService.showUserBlogs(id));
        return "admin";
    }

    @ResponseBody
    @GetMapping("/tags")
    public List<Tag> getTags() {
        return tagService.getTags();
    }

}
