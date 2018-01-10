package vip.fanrong.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vip.fanrong.form.LoginForm;
import vip.fanrong.form.UserRegisterForm;
import vip.fanrong.model.User;
import vip.fanrong.service.UserService;

import javax.servlet.http.HttpSession;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String get(@ModelAttribute("user") User user) {
        return "register";
    }

    @PostMapping("/register")
    @ResponseBody
    public LoginForm register(@RequestBody UserRegisterForm form,
                              HttpSession session) {

        LoginForm registerStatus = new LoginForm();
        User user = form.toUser();
        //创建新用户
        user = userService.register(user);

        if (user == null) {
            registerStatus.setStatus("failed");
            registerStatus.setMessage("邮箱已存在！");
            return registerStatus;
        }

        //将用户放进session中
        session.setAttribute("CURRENT_USER", user);

        registerStatus.setStatus("success");
        registerStatus.setMessage("注册成功！");
        registerStatus.setInfo(user.getId().toString());
        return registerStatus;
    }
}
