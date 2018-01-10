package vip.fanrong.model;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String description;
    private String avatar;

    private Date insertTime;
    private Long invited;

    public User() {

    }

    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User(String email, String username, String password, String description) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //重写equals方法
    @Override
    public boolean equals(Object user) {
        if (!(user instanceof User)) {
            return false;
        }
        return this.getUsername().equals(((User) user).getUsername());
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public Long getInvited() {
        return invited;
    }

    public void setInvited(Long invited) {
        this.invited = invited;
    }
}
