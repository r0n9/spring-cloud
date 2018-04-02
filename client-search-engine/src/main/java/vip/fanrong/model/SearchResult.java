package vip.fanrong.model;

import java.util.List;

public class SearchResult {
    private String title;
    private String url;
    private String brief;

    private String supplement1;
    private String supplement2;
    private String supplement3;

    private List<SearchResultRelated> relatedList;

    public SearchResult(String title, String url, String brief) {
        this.title = title;
        this.url = url;
        this.brief = brief;
    }

    public SearchResult() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getSupplement1() {
        return supplement1;
    }

    public void setSupplement1(String supplement1) {
        this.supplement1 = supplement1;
    }

    public String getSupplement2() {
        return supplement2;
    }

    public void setSupplement2(String supplement2) {
        this.supplement2 = supplement2;
    }

    public String getSupplement3() {
        return supplement3;
    }

    public void setSupplement3(String supplement3) {
        this.supplement3 = supplement3;
    }

    public List<SearchResultRelated> getRelatedList() {
        return relatedList;
    }

    public void setRelatedList(List<SearchResultRelated> relatedList) {
        this.relatedList = relatedList;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", brief='" + brief + '\'' +
                ", supplement1='" + supplement1 + '\'' +
                ", supplement2='" + supplement2 + '\'' +
                ", supplement3='" + supplement3 + '\'' +
                ", relatedList=" + relatedList +
                '}';
    }
}
