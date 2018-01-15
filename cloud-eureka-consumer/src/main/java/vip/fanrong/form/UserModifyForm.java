package vip.fanrong.form;

import vip.fanrong.model.User;

import javax.validation.constraints.Size;

public class UserModifyForm {

    @Size(min = 3, max = 30)
    private String username;

    @Size(min = 6, max = 30)
    private String password;

    private String description;

    private String status;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User toUser() {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setDescription(description);
        return user;
    }
}
