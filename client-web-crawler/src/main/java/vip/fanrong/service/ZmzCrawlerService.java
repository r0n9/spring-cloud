package vip.fanrong.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.fanrong.common.JsonUtil;
import vip.fanrong.common.MyHttpClient;
import vip.fanrong.mapper.ZmzResourceTopMapper;
import vip.fanrong.model.ZmzResourceTop;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Rong on 2017/9/11.
 */
@Service
public class ZmzCrawlerService {

    private static final Log LOGGER = LogFactory.getLog(ZmzCrawlerService.class);
    private static final DateTimeFormatter FORMATTER_SIMPLE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of("GMT+08:00"));

    @Autowired
    private ZmzResourceTopMapper zmzResourceTopMapper;

    public ObjectNode getZmzResourceTopsNode() {
        List<ZmzResourceTop> list = getZmzResourceTops();
        ObjectNode objectNode = JsonUtil.createObjectNode();
        objectNode.put("count", list.size());
        objectNode.putPOJO("resource", list);
        return objectNode;
    }

    public List<ZmzResourceTop> getZmzResourceTops() {
        String html = MyHttpClient.httpGet("http://m.zimuzu.tv/resource/top");
        Date getTime = Calendar.getInstance(Locale.CHINA).getTime();
        List<ZmzResourceTop> list = parseHtml(html, getTime);
        String getTimeStr = ZonedDateTime.ofInstant(getTime.toInstant(), ZoneId.of("GMT+08:00")).format(FORMATTER_SIMPLE);
        LOGGER.info("成功获取最新资源数量为：" + list.size() + " 获取时间：" + getTimeStr);
        return list;
    }

    public int loadZmzResourceTops() {
        List<ZmzResourceTop> list = getZmzResourceTops();
        int loaded = zmzResourceTopMapper.batchInsert(list);
        return loaded;
    }


    private List<ZmzResourceTop> parseHtml(String html, Date getTime) {

        List<ZmzResourceTop> list = new ArrayList<>();
        Document doc = Jsoup.parse(html, "http://m.zimuzu.tv/");

        Element rankingBox1 = doc.getElementById("ranking-box-1"); // 美剧
        Element rankingBox2 = doc.getElementById("ranking-box-2"); // 电影
        Element rankingBox3 = doc.getElementById("ranking-box-3"); // 日剧

        parseElement(list, rankingBox1, getTime);
        parseElement(list, rankingBox2, getTime);
        parseElement(list, rankingBox3, getTime);

        return list;
    }


    private void parseElement(List<ZmzResourceTop> list, Element rankingBox, Date getTime) {
        Elements lis = rankingBox.getElementsByTag("li");
        for (Element li : lis) {
            Element count = li.selectFirst("span.count");
            Element type = li.selectFirst("span.type");
            Element img = li.selectFirst("img[data-src]");
            Element desc = li.selectFirst("p.desc");
            Element descEn = li.getElementsByClass("desc desc-en").get(0);

            ZmzResourceTop data = new ZmzResourceTop();
            data.setGetTime(getTime);
            data.setCount(Integer.valueOf(count.text()));
            data.setType(type.text());
            data.setSrc(desc.getElementsByTag("a").first().attr("abs:href"));
            data.setImgDataSrc(img.attr("data-src"));
            data.setName(desc.getElementsByTag("a").first().text());
            data.setNameEn(descEn.getElementsByTag("a").first().text());
            data.setProcessed(0);

            list.add(data);
        }
    }
}
