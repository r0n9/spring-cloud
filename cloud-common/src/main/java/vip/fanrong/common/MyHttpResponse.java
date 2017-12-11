package vip.fanrong.common;

import org.apache.http.Header;
import org.apache.http.StatusLine;

public class MyHttpResponse {
    private Header[] headers;
    private StatusLine statusLine;
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

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }
}
