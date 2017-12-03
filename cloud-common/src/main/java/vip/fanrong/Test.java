package vip.fanrong;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import vip.fanrong.common.MyHttpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rong on 2017/11/29.
 */
public class Test {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("keyword", "闪电侠");
        String body = "";

        body = MyHttpClient.httpGet("https://whoer.net/zh");

        Document doc = Jsoup.parse(body);
        Elements elements = doc.select("div.column");

        System.out.println("--------------------");
        System.out.println(elements.first());


        body = MyHttpClient.httpGetWithProxy("https://whoer.net/zh", "62.16.21.21", 9999, "HTTP");

        doc = Jsoup.parse(body);
        elements = doc.select("div.column");

        System.out.println("--------------------");
        System.out.println(elements.first());


    }
}
