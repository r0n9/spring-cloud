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
        HttpGet request = new HttpGet("http://www.zimuzu.tv/resource/index_json/rid/35726/channel/movie");

        MyHttpResponse response = MyHttpClient.getHttpResponse(request, null, null);

        System.out.println(response.getHtml());


    }
}
