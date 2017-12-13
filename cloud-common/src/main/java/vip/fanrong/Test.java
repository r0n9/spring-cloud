package vip.fanrong;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.fanrong.common.JsonUtil;
import vip.fanrong.common.MyHttpClient;
import vip.fanrong.common.MyHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


/**
 * Created by Rong on 2017/11/29.
 */
public class Test {
    private final static Logger LOG = LoggerFactory.getLogger(Test.class);


    public static void main(String[] args) throws IOException, URISyntaxException {
        // "http://www.zimuzu.tv/resource/index_json/rid/11057/channel/tv"

        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpGet getCookie = new HttpGet("http://www.zimuzu.tv/user/login");
        CloseableHttpResponse response1 = httpclient.execute(getCookie);
        System.out.println(StringEscapeUtils.unescapeJava(EntityUtils.toString(response1.getEntity())));
        response1.close();

        System.out.println(cookieStore.getCookies());
        System.out.println("~~~~~~~~~~~");

        String loginUrl = "http://www.zimuzu.tv/User/Login/ajaxLogin";

        HttpUriRequest postLogin = RequestBuilder.post().setUri(new URI(loginUrl))
                .addParameter("account", "8d787d3d-23dd")
                .addParameter("password", "e54711b01d9b")
                .addParameter("remember", "1")
                .addParameter("url_back", "http://www.zimuzu.tv/user/login")
                .build();
        CloseableHttpResponse response2 = httpclient.execute(postLogin);
        System.out.println(StringEscapeUtils.unescapeJava(EntityUtils.toString(response2.getEntity())));
        response2.close();

        System.out.println(cookieStore.getCookies());
        System.out.println("~~~~~~~~~~~");

        String getUserInfoUrl = "http://www.zimuzu.tv/user/login/getCurUserTopInfo";
        HttpUriRequest postExamMark = RequestBuilder.post().setUri(new URI(getUserInfoUrl))
                .build();
        CloseableHttpResponse response = httpclient.execute(postExamMark);
        HttpEntity entity = response.getEntity();
        String json = StringEscapeUtils.unescapeJava(EntityUtils.toString(entity));
        EntityUtils.consume(entity);
        System.out.println(json);

        System.out.println(cookieStore.getCookies());
        System.out.println("~~~~~~~~~~~");
    }

    public static void main1(String[] args) {
        HttpGet request = new HttpGet("http://www.baidu.com");
        MyHttpResponse response = MyHttpClient.getHttpResponse(request, null, null, "61.163.139.161", 80,"HTTP");

        System.out.println(response.getHtml());
    }
}
