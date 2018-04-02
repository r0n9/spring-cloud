package vip.fanrong.model;

public class SearchResultRelated {
    private String title;
    private String url;
    private String supplement1;
    private String supplement2;
    private String supplement3;

    public SearchResultRelated(String title, String url, String supplement1, String supplement2, String supplement3) {
        this.title = title;
        this.url = url;
        this.supplement1 = supplement1;
        this.supplement2 = supplement2;
        this.supplement3 = supplement3;
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

    @Override
    public String toString() {
        return "SearchResultRelated{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", supplement1='" + supplement1 + '\'' +
                ", supplement2='" + supplement2 + '\'' +
                ", supplement3='" + supplement3 + '\'' +
                '}';
    }
}
