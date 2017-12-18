package vip.fanrong.service;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.fanrong.common.DateTimeUtil;
import vip.fanrong.common.MyHttpClient;
import vip.fanrong.common.MyHttpResponse;
import vip.fanrong.mapper.KdsTopicMapper;
import vip.fanrong.model.KdsTopic;
import vip.fanrong.model.ProxyConfig;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * Created by Rong on 2017/12/18.
 */
@Service
public class KdsCrawlerService {
    private static final Logger LOG = LoggerFactory.getLogger(KdsCrawlerService.class);

    private String homepageUrl = "https://m.kdslife.com/";

    private Pattern postPattern = Pattern.compile("<li>.+?</li>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // 支持匹配多行，忽略\n

    private Pattern imgPattern = Pattern.compile("<img src=\"(.*?)@");
    private Pattern titlePattern = Pattern.compile("title=\"(.*?)\" target");
    private Pattern linkPattern = Pattern.compile("<a href=\"(.*?\\.html)");

    private Pattern replytoPattern = Pattern.compile("<span class=\"replyto\">(.*?)</span>");
    private Pattern usertoPattern = Pattern.compile("<span class=\"userto\">(.*?)</span>");
    private Pattern asidePattern = Pattern.compile("<aside>(.*?)</aside>");

    @Autowired
    private KdsTopicMapper kdsTopicMapper;

    private MyHttpResponse getMyHttpResponse(ProxyConfig proxy,
                                             HttpRequestBase request,
                                             Map<String, String> map,
                                             List<Cookie> cookie) {
        MyHttpResponse response;
        if (null == proxy) {
            response = MyHttpClient.getHttpResponse(request, map, cookie);
        } else {
            response = MyHttpClient.getHttpResponse(request, map, cookie,
                    proxy.getHost(), proxy.getPort(), proxy.getType());
        }
        return response;
    }

    public int loadPopularTopics(ProxyConfig proxy, int limit) {
        List<KdsTopic> topics = this.getPopularTopics(proxy, limit);
        int loaded = kdsTopicMapper.batchInsert(topics);
        return loaded;
    }

    public List<KdsTopic> getPopularTopics(ProxyConfig proxy, int limit) {
        Set<KdsTopic> set = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            String url = this.getReplyPageUrl(i);
            set.addAll(this.getTopics(proxy, url));
        }
        for (int i = 0; i < 5; i++) {
            String url = this.getCreatePageUrl(i);
            set.addAll(this.getTopics(proxy, url));
        }


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateTimeUtil.getTimeNowGMT8());
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 14); // 2周内

        List<KdsTopic> popularTopics = set.stream()
                .filter((KdsTopic t) -> t.getPostTime().after(calendar.getTime()))
                .sorted(comparing(KdsTopic::getReplyTo).reversed())
                .limit(limit)
                .collect(toList());
        return popularTopics;
    }

    private List<KdsTopic> getTopics(ProxyConfig proxy, String url) {
        String html = getMyHttpResponse(proxy, new HttpGet(url), null, null).getHtml();
        return parseHtml(html);
    }

    private List<KdsTopic> parseHtml(String pageHtml) {

        pageHtml = pageHtml.replaceAll("\\n", " ");
        Date now = DateTimeUtil.getTimeNowGMT8();

        Matcher matcher = postPattern.matcher(pageHtml);
        List<KdsTopic> topics = new ArrayList<>();
        while (matcher.find()) {
            String postStr = matcher.group();

            Matcher imgUrlMat = imgPattern.matcher(postStr);
            String imgUrl = imgUrlMat.find() ? imgUrlMat.group(1).trim() : "";

            Matcher titleMatcher = titlePattern.matcher(postStr);
            String title = titleMatcher.find() ? titleMatcher.group(1).trim() : "";

            Matcher linkMatcher = linkPattern.matcher(postStr);
            String link = linkMatcher.find() ? homepageUrl + linkMatcher.group(1).trim() : "";

            Matcher replytoMatcher = replytoPattern.matcher(postStr);
            String replyto = replytoMatcher.find() ? replytoMatcher.group(1).trim() : "0";

            Matcher usertoMatcher = usertoPattern.matcher(postStr);
            String userto = usertoMatcher.find() ? usertoMatcher.group(1).trim() : "0";

            Matcher asideMatcher = asidePattern.matcher(postStr);
            String aside = asideMatcher.find() ? asideMatcher.group(1).trim() : "";


            if ("".equals(title) || "".equals(link)) {
                // remove AD
                continue;
            }

            KdsTopic topic = new KdsTopic();
            topic.setTitle(title);
            topic.setLink(link);
            topic.setImgLink(imgUrl);
            topic.setPostTime(DateTimeUtil.getDate(aside, "yyyy-MM-dd HH:mm"));
            topic.setReplyTo(Long.parseLong(replyto));
            topic.setUserto(Long.parseLong(userto));
            topic.setInsertTime(now);

            topics.add(topic);
        }
        return topics;
    }

    private String getReplyPageUrl(int pageNo) {
        String urlReply1 = "https://m.kdslife.com/f_15_0_2_";
        String urlReply2 = "_0.html";
        String url = urlReply1 + pageNo + urlReply2;
        return url;
    }

    private String getCreatePageUrl(int pageNo) {
        String urlCreate1 = "https://m.kdslife.com/f_15_0_3_";
        String urlCreate2 = "_0.html";
        String url = urlCreate1 + pageNo + urlCreate2;
        return url;
    }

}
