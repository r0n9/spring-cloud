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
        String body = null;

//        body = MyHttpClient.httpPost("http://m.zimuzu.tv/search", map, "");
        body = MyHttpClient.httpGet("http://m.zimuzu.tv/resource/top");
//        System.out.println(body);

        Document doc = Jsoup.parse(body);

        Element rankingBox1 = doc.getElementById("ranking-box-1");
        Element rankingBox2 = doc.getElementById("ranking-box-2");
        Element rankingBox3 = doc.getElementById("ranking-box-3");

        Elements lis = rankingBox1.getElementsByTag("li");
        for (Element li : lis) {
            Element ob = li.getElementsByClass("desc").get(0).getElementsByTag("a").get(0);
            Element obEn = li.getElementsByClass("desc desc-en").get(0).getElementsByTag("a").get(0);

            System.out.println(ob.text() + " " + ob.attr("href") + " " + obEn.text());
        }

    }
}
