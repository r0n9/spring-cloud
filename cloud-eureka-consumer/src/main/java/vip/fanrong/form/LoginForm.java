package vip.fanrong.form;

/**
 * Created by Rong on 2018/1/9.
 */
public class LoginForm {

    private String status;
    private String message;
    private String info; //用来放重定向的信息

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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
