package vip.fanrong.model;

import java.util.Date;

public class ProxyConfig {
    private String host;
    private Integer port;
    private String location;
    private String type;
    private String status;
    private Date statusUpdateTime;
    private Date insertTime;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStatusUpdateTime() {
        return statusUpdateTime;
    }

    public void setStatusUpdateTime(Date statusUpdateTime) {
        this.statusUpdateTime = statusUpdateTime;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public String toString() {
        return "ProxyConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", location='" + location + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", statusUpdateTime=" + statusUpdateTime +
                ", insertTime=" + insertTime +
                '}';
    }
}
