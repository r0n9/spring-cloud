package vip.fanrong.model;

import java.util.Date;

/**
 * Created by Rong on 2017/11/29.
 */
public class ZmzResourceTop {
    private Date getTime;
    private int count;
    private String type;
    private String src;
    private String imgDataSrc;
    private String name;
    private String nameEn;
    private int processed;
    private Date processTime;

    public Date getGetTime() {
        return getTime;
    }

    public void setGetTime(Date getTime) {
        this.getTime = getTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getImgDataSrc() {
        return imgDataSrc;
    }

    public void setImgDataSrc(String imgDataSrc) {
        this.imgDataSrc = imgDataSrc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public int getProcessed() {
        return processed;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }

    public Date getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
    }
}
