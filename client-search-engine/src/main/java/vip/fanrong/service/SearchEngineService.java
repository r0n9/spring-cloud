package vip.fanrong.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import org.apache.commons.lang.time.StopWatch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vip.fanrong.common.JsonUtil;
import vip.fanrong.model.SearchResult;

import java.util.*;


/**
 * Created by Rong on 2017/12/18.
 */
@Service
public class SearchEngineService {
    private static final Logger LOG = LoggerFactory.getLogger(SearchEngineService.class);


    public ObjectNode searchGoogle(String key, int pageNum) {

        StopWatch watch = new StopWatch();
        watch.start();

        Map<String, String> pageUrls = new LinkedHashMap<>();
        int totalPageNums = 1;
        Launcher launcher = new Launcher();
        List<SearchResult> resultList = new ArrayList<>();
        try (SessionFactory factory = launcher.launch(); Session session = factory.create()) {
            session.navigate("https://www.google.com.hk/")
                    .waitDocumentReady()
                    .installSizzle()
                    .enableNetworkLog()
                    .click("#lst-ib")
                    .sendKeys(key)
                    .sendEnter()
                    .wait(1000);

            String html = session.getContent();

            Document doc = Jsoup.parse(html);
            Elements elements = doc.select("#rso > div > div > div");


            for (Element element : elements) {
                Element titleElmt = element.selectFirst("a");
                Element urlElmt = element.selectFirst("a");
                Element briefElmt = element.selectFirst("span.st");

                if (titleElmt != null && urlElmt != null) {
                    if ("".equals(titleElmt.text())) {
                        continue;
                    }
                    resultList.add(new SearchResult(titleElmt.text(), urlElmt.attr("href"), null == briefElmt ? null : briefElmt.text()));
                }
            }

            Elements navs = doc.select("#nav > tbody > tr > td > a.fl");
            if (navs != null || !navs.isEmpty()) {
                for (Element nav : navs) {
                    pageUrls.put(nav.childNode(1).toString(), "https://www.google.com.hk" + nav.attr("href"));
                    totalPageNums++;
                }
            }

        }

        watch.stop();

        ObjectNode node = JsonUtil.createObjectNode();
        node.put("page_num", pageNum);
        node.put("total_num", totalPageNums);
        node.put("cost_ms", watch.getTime());
        node.putPOJO("results", JsonUtil.objectToJson(resultList));
        node.putPOJO("page_urls", JsonUtil.objectToJson(pageUrls));

        return node;
    }

    public ObjectNode searchBaidu(String key, int pageNum) {

        StopWatch watch = new StopWatch();
        watch.start();

        Map<String, String> pageUrls = new LinkedHashMap<>();
        int totalPageNums = 1;
        Launcher launcher = new Launcher();
        List<SearchResult> resultList = new ArrayList<>();
        try (SessionFactory factory = launcher.launch(); Session session = factory.create()) {
            session.navigate("https://www.baidu.com/s?wd=" + key)
                    .waitDocumentReady()
                    .installSizzle()
                    .enableNetworkLog();

            String html = session.getContent();

            Document doc = Jsoup.parse(html);
            Elements elements = doc.select("#content_left > div");


            for (Element element : elements) {
                Element titleElmt = element.selectFirst("h3 > a");
                Element urlElmt = element.selectFirst("h3 > a");
                Element briefElmt = element.selectFirst("div.c-abstract");

                if (titleElmt != null && urlElmt != null) {
                    if ("".equals(titleElmt.text())) {
                        continue;
                    }
                    resultList.add(new SearchResult(titleElmt.text(), urlElmt.attr("href"), null == briefElmt ? null : briefElmt.text()));
                }
            }

            Elements navs = doc.select("#page > a");
            if (navs != null || !navs.isEmpty()) {
                for (Element nav : navs) {
                    if (nav.selectFirst("span.pc") == null) {
                        continue;
                    }
                    pageUrls.put(nav.selectFirst("span.pc").text(), "https://www.baidu.com" + nav.attr("href"));
                    totalPageNums++;
                }
            }

        }

        watch.stop();

        ObjectNode node = JsonUtil.createObjectNode();
        node.put("page_num", pageNum);
        node.put("total_num", totalPageNums);
        node.put("cost_ms", watch.getTime());
        node.putPOJO("results", JsonUtil.objectToJson(resultList));
        node.putPOJO("page_urls", JsonUtil.objectToJson(pageUrls));

        return node;

    }

}
