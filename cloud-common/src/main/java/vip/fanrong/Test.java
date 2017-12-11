package vip.fanrong;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.fanrong.common.MyHttpClient;
import vip.fanrong.common.MyHttpResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rong on 2017/11/29.
 */
public class Test {
    private final static Logger LOG = LoggerFactory.getLogger(Test.class);


    public static void main(String[] args) {
        // zmz 登陆
        String url = "http://www.zimuzu.tv/User/Login/ajaxLogin";

        Map<String, String> params = new HashMap<>();
        params.put("account", "da6528ad-321b");
        params.put("password", "eec746d53b33");
        params.put("remember", "1");
        params.put("url_back", "http://www.zimuzu.tv/user/login");

        MyHttpResponse response = MyHttpClient.getHttpResponse(new HttpPost(url), params, null);

        for (Header header : response.getHeaders()) {
            System.out.println(header);
        }
        System.out.println(response.getHtml());


        // zmz 注册
//        String url = "http://www.zimuzu.tv/user/reg"; // 初始页面
//        String html = MyHttpClient.httpGet(url);
//
//        Document doc = Jsoup.parse(html);
//        Elements inputs = doc.getElementsByTag("input");
//        Element hashInput = null;
//        for (Element element : inputs) {
//            if ("__hash__".equalsIgnoreCase(element.attr("name"))) {
//                hashInput = element;
//                break;
//            }
//        }
//        String hashCode = hashInput.attr("value");
//        System.out.println("hash: " + hashCode);
//
//
//        String regUrl = "http://www.zimuzu.tv/User/Reg/saveReg";
//        Map<String, String> requestBody = new HashMap<>();
//        requestBody.put("email", "123241153@qq.com");
//        requestBody.put("nickname", "nissasad1");
//        requestBody.put("password", "12345678");
//        requestBody.put("repassword", "12345678");
//        requestBody.put("sex", "1");
//        requestBody.put("__hash__", hashCode);
//
//        String result = MyHttpClient.httpPost(regUrl, requestBody, null);
//
//        result = Jsoup.parse(result).getElementById("tipsMsg").getElementsByTag("a").first().text();
//
//        System.out.println(result);

        System.out.println(UUID.randomUUID().toString().substring(0, 13)); // passwrod

        System.out.println(UUID.randomUUID().toString().substring(24)); // passwrod
        System.out.print(System.currentTimeMillis() / 13);
    }
}
