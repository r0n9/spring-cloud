package vip.fanrong.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/*
 * PO类最好都实现Serializable接口，这样JVM就能够方便地将PO实例序列化到
 * 硬盘中，或者通过流的方式进行发送，为缓存、集群等功能带来便利。
 * 也方便把PO对象打印为一个字符串
 */
public class Blog implements Serializable {

    private Long id;

    private String title;

    private User author;

    private String content;

    private Date createdTime;

    private Set<Tag> tags;

    public Blog() {
        createdTime = new Date();
    }

    public Blog(String title, String content) {
        this();
        this.title = title;
        this.content = content;
    }

    public Blog(String title, String content, User author) {
        this(title, content);
        this.setAuthor(author);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }


}
