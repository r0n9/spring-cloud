package vip.fanrong.form;

import vip.fanrong.model.Comment;
import vip.fanrong.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CommentForm {

    private Long id;
    private String content;
    private User commentor;
    private String createdTime;
    private String status;
    private String message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getCommentor() {
        return commentor;
    }

    public void setCommentor(User commentor) {
        this.commentor = commentor;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Comment toComment() throws ParseException {
        Comment comment = new Comment();
        comment.setCommentor(commentor);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        comment.setCreatedTime(format.parse(createdTime));
        comment.setContent(content);
        return comment;
    }
}
