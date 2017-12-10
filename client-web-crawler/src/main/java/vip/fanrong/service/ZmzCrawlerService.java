package vip.fanrong.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.fanrong.common.MyHttpClient;
import vip.fanrong.mapper.ZmzAccountMapper;
import vip.fanrong.mapper.ZmzResourceTopMapper;
import vip.fanrong.model.MovieResource;
import vip.fanrong.model.ProxyConfig;
import vip.fanrong.model.ZmzAccount;
import vip.fanrong.model.ZmzResourceTop;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rong on 2017/9/11.
 */
@Service
public class ZmzCrawlerService {

    private static final Log LOGGER = LogFactory.getLog(ZmzCrawlerService.class);
    private static final DateTimeFormatter FORMATTER_SIMPLE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of("GMT+08:00"));

    private static final Pattern URL_PATTERN_XIAZAI003 = Pattern.compile("((http://xiazai003\\.com/\\w+))");

    @Autowired
    private ZmzResourceTopMapper zmzResourceTopMapper;

    @Autowired
    private ZmzAccountMapper zmzAccountMapper;

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

    public MovieResource getMovieResourceByZmzResourceId(ProxyConfig proxy, String zmzResourceId) {
        String sourceUrl = "http://www.zimuzu.tv/resource/index_json/rid/" + zmzResourceId + "/channel/movie";

        String html;
        if (null == proxy) {
            html = MyHttpClient.httpGet(sourceUrl);

        } else {
            html = MyHttpClient.httpGetWithProxy(sourceUrl, proxy.getHost(), proxy.getPort(), proxy.getType());
        }

        Matcher matcher = URL_PATTERN_XIAZAI003.matcher(html);

        if (matcher.find()) {
            sourceUrl = matcher.group(1);

            if (null == proxy) {
                html = MyHttpClient.httpGet(sourceUrl);

            } else {
                html = MyHttpClient.httpGetWithProxy(sourceUrl, proxy.getHost(), proxy.getPort(), proxy.getType());
            }

            // TODO
            System.out.println(html);

        }

        return null;


    }

    public ZmzAccount registerZmzAccountRandom(ProxyConfig proxy) {
        String uuid = UUID.randomUUID().toString();
        ZmzAccount zmzAccount = new ZmzAccount();
        zmzAccount.setEmail(String.valueOf(System.currentTimeMillis() / 13) + "@qq.com");
        zmzAccount.setNickname(uuid.substring(0, 13));
        zmzAccount.setPassword(uuid.substring(24));
        zmzAccount.setSex("1");
        return registerZmzAccount(proxy, zmzAccount);
    }

    public ZmzAccount registerZmzAccount(ProxyConfig proxy, ZmzAccount zmzAccount) {

        // zmz 注册
        String url = "http://www.zimuzu.tv/user/reg"; // 初始页面
        String html;
        if (null == proxy) {
            html = MyHttpClient.httpGet(url);
        } else {
            html = MyHttpClient.httpGetWithProxy(url, proxy.getHost(), proxy.getPort(), proxy.getType());
        }
        Document doc = Jsoup.parse(html);
        Elements inputs = doc.getElementsByTag("input");
        Element hashInput = null;
        for (Element element : inputs) {
            if ("__hash__".equalsIgnoreCase(element.attr("name"))) {
                hashInput = element;
                break;
            }
        }
        String hashCode = hashInput.attr("value");
//        System.out.println("hash: " + hashCode);


        String regUrl = "http://www.zimuzu.tv/User/Reg/saveReg";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", zmzAccount.getEmail());
        requestBody.put("nickname", zmzAccount.getNickname());
        requestBody.put("password", zmzAccount.getPassword());
        requestBody.put("repassword", zmzAccount.getPassword());
        requestBody.put("sex", zmzAccount.getSex());
        requestBody.put("__hash__", hashCode);

        String result;
        if (null == proxy) {
            result = MyHttpClient.httpPost(regUrl, requestBody, null);
        } else {
            result = MyHttpClient.httpPostWithProxy(regUrl, requestBody, null, proxy.getHost(), proxy.getPort(), proxy.getType());
        }

        result = Jsoup.parse(result).getElementById("tipsMsg").getElementsByTag("a").first().text();
        LOGGER.info(result);
        boolean isSuccess = StringUtils.contains(result, "注册成功");

        if (isSuccess) {
            zmzAccount.setIsValide(1);
            zmzAccount.setRegisterDate(Calendar.getInstance(Locale.CHINA).getTime());
            zmzAccountMapper.insert(zmzAccount);
        } else {
            return null;
        }

        return zmzAccount;
    }


}
