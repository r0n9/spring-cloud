package vip.fanrong;

import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.fanrong.common.MyHttpClient;
import vip.fanrong.common.MyHttpResponse;

import java.util.Date;
import java.util.TimeZone;


/**
 * Created by Rong on 2017/11/29.
 */
public class Test {
    private final static Logger LOG = LoggerFactory.getLogger(Test.class);


    public static void main(String[] args) {
        HttpGet request = new HttpGet("http://www.baidu.com");

        MyHttpResponse response = MyHttpClient.getHttpResponse(request, null, null, "61.138.104.30", 1080, "SOCKS");

        System.out.println(response.getHtml());
        System.out.println(response.getStatusLine());



    }
}
