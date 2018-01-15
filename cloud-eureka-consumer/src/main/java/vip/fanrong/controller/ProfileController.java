package vip.fanrong.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vip.fanrong.form.UserModifyForm;
import vip.fanrong.model.User;
import vip.fanrong.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
public class ProfileController {

    @Value("${file.dir}")
    private String ROOT;

    private final ResourceLoader resourceLoader;
    private final UserService userService;

    @Autowired
    public ProfileController(ResourceLoader resourceLoader, UserService userService) {
        this.resourceLoader = resourceLoader;
        this.userService = userService;
    }

    //用户获取资料页
    @GetMapping("/profile/{id}")
    public String showProfile(Model model, HttpSession session) {
        model.addAttribute("user", (User) session.getAttribute("CURRENT_USER"));
        return "profile";
    }

    //获得头像的方法
    @GetMapping("/avatar/{id}")
    @ResponseBody
    public ResponseEntity<?> getFile(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        if (user.getAvatar() == null) {
            //用户还没有头像
            try {
                //默认头像
                return ResponseEntity.ok(resourceLoader.getResource("classpath:" + Paths.get("/static/img/", "defaultAva.jpg")));
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }
        } else {
            //用户有头像，获取头像文件
            String filename = user.getAvatar();
            return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(ROOT, filename).toString()));
        }
    }

    /*
     * 用户修改资料
     *
     * 表单提交请求时考虑下列三种情况：
     * 用户只上传文件
     * 用户只修改密码
     * 用户只修改用户名邮箱
     */
    @PostMapping("/profile/{id}")
    public String handleFileUpload(@RequestParam(value = "file", required = false) MultipartFile file,
                                   @PathVariable("id") Long id,
                                   @Valid @ModelAttribute("user") UserModifyForm form,
                                   BindingResult result,
                                   HttpSession session,
                                   final RedirectAttributes redirectAttributes) throws IOException {

        // 不符合要求
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "failed");
            return "redirect:/profile/" + id;
        }

        User currentUser = (User) session.getAttribute("CURRENT_USER");

        String name = form.getUsername();
        String password = form.getPassword();
        String description = form.getDescription();

        if (name != null && !name.isEmpty()
                && !name.equals(currentUser.getUsername())) {
            currentUser.setUsername(form.getUsername());
        }
        if (password != null && !password.isEmpty()
                && !password.equals(currentUser.getPassword())) {
            currentUser.setPassword(form.getPassword());
        }
        if (description != null && !description.isEmpty()
                && !description.equals(currentUser.getDescription())) {
            currentUser.setDescription(form.getDescription());
        }

        // 保存图片
        if (file != null && !file.isEmpty()) {
            String filename = currentUser.getId() + ".jpg";
            Files.copy(file.getInputStream(), Paths.get(ROOT, filename), StandardCopyOption.REPLACE_EXISTING);
            currentUser.setAvatar(filename);
        }
        userService.updateUser(currentUser);
        session.setAttribute("CURRENT_USER", currentUser);
        
        redirectAttributes.addFlashAttribute("message", "success");
        return "redirect:/profile/" + id;
    }
}
