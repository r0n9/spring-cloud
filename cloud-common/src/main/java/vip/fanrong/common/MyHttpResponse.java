package vip.fanrong.common;

import org.apache.http.Header;

public class MyHttpResponse {
    private Header[] headers;

    private String html;

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
