package vip.fanrong.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vip.fanrong.Constant;
import vip.fanrong.client.KdsCrawlerClient;
import vip.fanrong.model.Blog;
import vip.fanrong.model.User;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Rong on 2018/1/9.
 */
@Controller
public class IndexController {

    @Autowired
    private KdsCrawlerClient kdsCrawlerClient;

    //网站首页
    @GetMapping("/")
    public String showMainPage(@CookieValue(Constant.COOKIE_KEY_NAME) Optional<String> cookieEmail,
                               HttpSession session,
                               Model model,
                               @RequestParam("page") Optional<Integer> page) {
        //自动登录
        if (session.getAttribute("CURRENT_USER") == null
                && cookieEmail != null && cookieEmail.isPresent()) {
            session.setAttribute("CURRENT_USER", new User(cookieEmail.get(), "", ""));
        }

        //获取热门博客
        model.addAttribute("", "");
        model.addAttribute("page", 1);
        List<Blog> blogs = new ArrayList<>();
        Blog blog = new Blog();
        blog.setAuthor(new User());
        blog.setTitle("TEST title");
        blog.setId(1l);
        blog.setContent("contentttt");

        blogs.add(blog);
        blogs.add(blog);
        blogs.add(blog);
        blogs.add(blog);
        blogs.add(blog);
        model.addAttribute("blogs", blogs);


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


        List<Blog> blogs = new ArrayList<>();
        Blog blog = new Blog();
        blog.setAuthor(new User());
        blog.setTitle("TEST title");
        blog.setId(1l);
        blog.setContent("contentttt");

        blogs.add(blog);
        blogs.add(blog);
        blogs.add(blog);
        blogs.add(blog);
        blogs.add(blog);
        model.addAttribute("blogs", blogs);

        return "admin";
    }
}
