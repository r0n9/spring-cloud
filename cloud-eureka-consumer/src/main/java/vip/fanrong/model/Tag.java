package vip.fanrong.model;

import java.io.Serializable;

public class Tag implements Serializable {

    private String name;
    private Double blogsNum;

    public Tag() {
        blogsNum = 0d;
    }

    public Tag(String name) {
        this.name = name;
        this.blogsNum = 0d;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tag(String name, double n) {
        this.name = name;
        this.blogsNum = n;
    }

    public double getBlogsNum() {
        return blogsNum;
    }

    public void setBlogsNum(double blogsNum) {
        this.blogsNum = blogsNum;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tag)) {
            return false;
        }
        Tag t = (Tag) o;
        return t.name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.length();
    }
}
