package vip.fanrong.form;

import vip.fanrong.model.Blog;

import javax.validation.constraints.Size;

public class BlogCreateForm {

    @Size(min = 1, max = 50, message = "文章标题长度须在1-50字之间")
    private String title;

    @Size(min = 1, message = "文章内容不能为空")
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Blog toBlog() {
        Blog blog = new Blog();
        blog.setTitle(this.title);
        blog.setContent(this.content);

        return blog;
    }
}
