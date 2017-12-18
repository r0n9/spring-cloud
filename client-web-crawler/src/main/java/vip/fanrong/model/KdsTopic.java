package vip.fanrong.model;

import java.util.Date;

/**
 * Created by Rong on 2017/12/18.
 */
public class KdsTopic {
    private int id;
    private String title;
    private String link;
    private String imgLink;
    private long replyTo;
    private long userto;

    private Date postTime;

    private Date insertTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public long getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(long replyTo) {
        this.replyTo = replyTo;
    }

    public long getUserto() {
        return userto;
    }

    public void setUserto(long userto) {
        this.userto = userto;
    }

    public Date getPostTime() {
        return postTime;
    }

    public void setPostTime(Date postTime) {
        this.postTime = postTime;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KdsTopic kdsTopic = (KdsTopic) o;

        if (title != null ? !title.equals(kdsTopic.title) : kdsTopic.title != null) return false;
        return link != null ? link.equals(kdsTopic.link) : kdsTopic.link == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (link != null ? link.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "KdsTopic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", imgLink='" + imgLink + '\'' +
                ", replyTo=" + replyTo +
                ", userto=" + userto +
                ", postTime=" + postTime +
                ", insertTime=" + insertTime +
                '}';
    }
}
