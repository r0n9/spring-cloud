package vip.fanrong.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vip.fanrong.exception.NotFoundException;
import vip.fanrong.form.BlogCreateForm;
import vip.fanrong.model.Blog;
import vip.fanrong.model.User;
import vip.fanrong.service.BlogService;
import vip.fanrong.service.CommentService;
import vip.fanrong.util.LuceneUtils;
import vip.fanrong.util.TagUtils;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    @PostMapping("/blogs/create")
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
            PageInfo pageInfo = new PageInfo<>(blogService.showBlogs());
            model.addAttribute("blogs", pageInfo);
        }
        return "list";
    }


    // 展示某一篇博文
    @GetMapping("/blogs/{id}")
    public String showBlog(@PathVariable("id") Long id, Model model) throws NotFoundException {
        Blog blog = blogService.getBlogForBrowse(id);
        model.addAttribute("blog", blog);
        return "item";
    }

    // 删除一篇博文
    @DeleteMapping("/blogs/{id}")
    public String deleteBlog(@PathVariable("id") Long id,
                             HttpSession session,
                             final RedirectAttributes redirectAttributes) {
        blogService.deleteBlog(id);
        redirectAttributes.addFlashAttribute("delete", "success");
        return "redirect:/admin/" + ((User) session.getAttribute("CURRENT_USER")).getId();
    }

    //获取编辑某一篇博文的页面
    @GetMapping("/blogs/{id}/edit")
    public String showEditBlog(@PathVariable("id") Long id, Model model) throws NotFoundException {
        Blog blog = blogService.getBlogForEdit(id);
        model.addAttribute("blog", blog);
        model.addAttribute("alltags", TagUtils.toStringTags(blog.getTags()));
        model.addAttribute("edit", 1);
        return "create";
    }

    //提交修改过的博文
    @PostMapping("/blogs/{id}")
    public String updateBlog(@PathVariable("id") Long id,
                             @RequestParam(value = "alltags", required = false) String tags,
                             @ModelAttribute("blog") @Valid BlogCreateForm form,
                             BindingResult result,
                             HttpSession session) {
        if (result.hasErrors()) {
            return "create";
        }
        Blog blog = form.toBlog();
        blogService.updateBlog(id, blog, tags);
        return "redirect:/blogs/" + id;
    }

    @PostMapping("/blogs/search")
    public String search(Model model, @RequestParam("key") String key) {
        List<Blog> blogs = new ArrayList<>();
        if (key == null || key.length() == 0) {

        } else {
            blogs = blogService.search(key, 1, 5);
        }
        model.addAttribute("blogs", blogs);
        return "search";
    }

}
