package vip.fanrong;

import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.fanrong.common.MyHttpClient;
import vip.fanrong.common.MyHttpResponse;


/**
 * Created by Rong on 2017/11/29.
 */
public class Test {
    private final static Logger LOG = LoggerFactory.getLogger(Test.class);


    public static void main(String[] args) {
        HttpGet request = new HttpGet("http://www.baidu.com");

        MyHttpResponse response = MyHttpClient.getHttpResponse(request, null, null, "183.30.197.8", 9797, "HTTP");

        System.out.println(response.getHtml());
        System.out.println(response.getStatusLine());


    }
}
