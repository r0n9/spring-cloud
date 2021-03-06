package vip.fanrong.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vip.fanrong.common.JsonUtil;
import vip.fanrong.common.MyHttpClient;
import vip.fanrong.common.MyHttpResponse;
import vip.fanrong.model.SearchResult;

import java.util.*;


/**
 * Created by Rong on 2017/12/18.
 */
@Service
public class SearchEngineService {
    private static final Logger LOG = LoggerFactory.getLogger(SearchEngineService.class);

    public ObjectNode searchGoogle(String key, int pageNum, String pageUrl) throws Exception {
        String url = null;
        if (StringUtils.isNotBlank(pageUrl) && !"{}".equals(pageUrl) && !"null".equalsIgnoreCase(pageUrl)) {
            url = pageUrl;
        } else {
            url = "https://www.google.com.hk/search?q=" + key;
        }

        StopWatch watch = new StopWatch();
        watch.start();
        MyHttpResponse myHttpResponse = MyHttpClient.getHttpResponse(new HttpGet(url), null, null);
        String html = myHttpResponse.getHtml();
        watch.stop();
        return toGoogleObjectNode(key, html, pageNum, watch.getTime());
    }

    private ObjectNode toGoogleObjectNode(String key, String html, int pageNum, long time) {
        Map<String, String> pageUrls = new LinkedHashMap<>();
        int totalPageNums = 1;
        List<SearchResult> resultList = new ArrayList<>();

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

        ObjectNode node = JsonUtil.createObjectNode();
        node.put("keyword", key);
        node.put("page_num", pageNum);
        node.put("total_num", totalPageNums);
        node.put("cost_ms", time);
        node.putPOJO("results", resultList);
        node.putPOJO("page_urls", pageUrls);

        return node;
    }

    public ObjectNode searchBaidu(String key, int pageNum, String pageUrl) throws Exception {

        String url = null;
        if (StringUtils.isNotBlank(pageUrl) && !"{}".equals(pageUrl) && !"null".equalsIgnoreCase(pageUrl)) {
            url = pageUrl;
        } else {
            url = "https://www.baidu.com/s?wd=" + key;
        }

        StopWatch watch = new StopWatch();
        watch.start();
        List<SearchResult> resultList = new ArrayList<>();
        Map<String, String> pageUrls = new LinkedHashMap<>();
        int totalPageNums = 1;

        MyHttpResponse myHttpResponse = MyHttpClient.getHttpResponse(new HttpGet(url), null, null);
        String html = myHttpResponse.getHtml();
        watch.stop();

        ObjectNode node = JsonUtil.createObjectNode();
        node.put("keyword", key);
        node.put("page_num", pageNum);
        node.put("total_num", totalPageNums);
        node.put("cost_ms", watch.getTime());
        node.putPOJO("results", resultList);
        node.putPOJO("page_urls", pageUrls);

        return toBaiduObjectNode(key, html, pageNum, watch.getTime());

    }

    private ObjectNode toBaiduObjectNode(String key, String html, int pageNum, long time) {

        List<SearchResult> resultList = new ArrayList<>();
        Map<String, String> pageUrls = new LinkedHashMap<>();
        int totalPageNums = 1;

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

        ObjectNode node = JsonUtil.createObjectNode();
        node.put("keyword", key);
        node.put("page_num", pageNum);
        node.put("total_num", totalPageNums);
        node.put("cost_ms", time);
        node.putPOJO("results", resultList);
        node.putPOJO("page_urls", pageUrls);

        return node;
    }
}
