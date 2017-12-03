package vip.fanrong;

import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.fanrong.common.MyHttpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rong on 2017/11/29.
 */
public class Test {
    private final static Logger LOG = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
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
}
