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
import vip.fanrong.service.BlogService;
import vip.fanrong.service.TagService;
import vip.fanrong.service.UserService;

import javax.servlet.http.HttpSession;
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

    //网站首页
    @GetMapping("/")
    public String showMainPage(@CookieValue(Constant.COOKIE_KEY_NAME) Optional<String> cookieEmail,
                               HttpSession session,
                               Model model,
                               @RequestParam("page") Optional<Integer> page) {
        // 自动登录
        if (session.getAttribute("CURRENT_USER") == null
                && cookieEmail != null && cookieEmail.isPresent()) {
            session.setAttribute("CURRENT_USER", userService.getUserByEmail(cookieEmail.get()));
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
