package vip.fanrong.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vip.fanrong.exception.NotFoundException;
import vip.fanrong.form.BlogCreateForm;
import vip.fanrong.model.Blog;
import vip.fanrong.model.User;
import vip.fanrong.service.BlogService;
import vip.fanrong.service.CommentService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

/**
 * Created by Rong on 2018/1/10.
 */
@Controller
public class BlogController {
    @Autowired
    private BlogService blogService;

    @Autowired
    private CommentService commentService;

    // 获取新建博文的页面
    @GetMapping("blogs/create")
    public String showCreatePage(@ModelAttribute("user") User user,
                                 @ModelAttribute("blog") Blog blog) {
        return "create";
    }

    // 提交新建的博文
    @PostMapping("/blogs")
    public String createBlog(@ModelAttribute("blog") @Valid BlogCreateForm form,
                             BindingResult result,
                             @RequestParam(value = "alltags", required = false) String tags,
                             HttpSession session) {
        if (result.hasErrors()) {
            return "create";
        }
        User user = ((User) session.getAttribute("CURRENT_USER"));
        Blog blog = form.toBlog();
        blog.setAuthor(user);
        blogService.createBlog(blog, tags);
        return "redirect:/blogs/" + blog.getId();
    }


    // 展示所有博文
    @GetMapping("/blogs")
    public String showBlogs(Model model,
                            @RequestParam("tag") Optional<String> tag,
                            @RequestParam("page") Optional<Integer> page) {
        PageHelper.startPage(page.orElse(1), 5);
        if (tag.isPresent()) {
            model.addAttribute("blogs", new PageInfo<>(blogService.getBlogsByTag(tag.get())));
        } else {
            model.addAttribute("blogs", new PageInfo<>(blogService.showBlogs()));
        }
        return "list";
    }


    // 展示某一篇博文
    @GetMapping("/blogs/{id}")
    public String showBlog(@PathVariable("id") Long id, Model model) throws NotFoundException {
        Blog blog = blogService.getBlogForBrowse(id);
        model.addAttribute("blog", blog);
        model.addAttribute("comments", commentService.getCommentsByBlogId(id));
        return "item";
    }
}
