package vip.fanrong;

import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.fanrong.common.MyHttpClient;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rong on 2017/11/29.
 */
public class Test {
    private final static Logger LOG = LoggerFactory.getLogger(Test.class);

    public static void testProxcy() {
        Map<String, String> map = new HashMap<>();
        map.put("keyword", "闪电侠");
        String body = "";

        body = MyHttpClient.httpGet("https://whoer.net/zh");

        Document doc = Jsoup.parse(body);
        Elements elements = doc.select("span.cont.overdots");

        LOG.info("--------------------");
        LOG.info(String.valueOf(elements.first()));


        body = MyHttpClient.httpGetWithProxy("https://whoer.net/zh", "62.16.21.21", 9999, "HTTP");

        doc = Jsoup.parse(body);
        elements = doc.select("span.cont.overdots");

        LOG.info("--------------------");
        LOG.info(String.valueOf(elements.first()));

        body = MyHttpClient.httpGetWithProxy("https://whoer.net/zh", "139.162.83.23", 55477, "SOCKS");

        doc = Jsoup.parse(body);
        elements = doc.select("span.cont.overdots");

        LOG.info("--------------------");
        LOG.info(String.valueOf(elements.first()));


    }

    public static void getProxcy() {
        String url = "http://www.gatherproxy.com/zh/sockslist";

        Pattern p = Pattern.compile("document.write\\('(.*)'\\)");


        String html = MyHttpClient.httpGet(url);

//        System.out.println(html);


        Document doc = Jsoup.parse(html);

        Elements trs = doc.getElementsByTag("tr");

        for (Element tr : trs) {
            Elements tds = tr.getElementsByTag("td");
            if (tds == null || tds.size() != 7) {
                continue;
            }

            Matcher m1 = p.matcher(tds.get(1).toString());
            m1.find();
            Matcher m2 = p.matcher(tds.get(2).toString());
            m2.find();

            System.out.println(m1.group(1)
                    + " " + m2.group(1)
                    + " " + tds.get(3).getElementsByTag("a").first().text());

        }

    }

    public static void main(String[] args) {
        getProxcy();
    }
}
