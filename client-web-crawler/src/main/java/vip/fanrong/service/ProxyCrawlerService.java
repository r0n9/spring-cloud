package vip.fanrong.service;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import vip.fanrong.common.MyHttpClient;
import vip.fanrong.model.ProxyConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProxyCrawlerService {

    private static final Pattern GATHERPROXY_PATTERN = Pattern.compile("document.write\\('(.*)'\\)");

    public List<ProxyConfig> getSocksProxyConfigsFromGatherproxy(ProxyConfig proxyConfig, String byCountry) {
        String url = "http://www.gatherproxy.com/zh/sockslist";

        if (StringUtils.isNotBlank(byCountry)) {
            url += "/country/?c=" + byCountry;
        }

        String html = null;
        if (proxyConfig == null) {
            html = MyHttpClient.httpGet(url);
        } else {
            html = MyHttpClient.httpGetWithProxy(url, proxyConfig.getHost(), proxyConfig.getPort(), proxyConfig.getType());
        }

        Document doc = Jsoup.parse(html);
        Elements trs = doc.getElementsByTag("tr");
        List<ProxyConfig> results = new ArrayList<>();
        for (Element tr : trs) {
            Elements tds = tr.getElementsByTag("td");
            if (tds == null || tds.size() != 7) {
                continue;
            }
            ProxyConfig proxy = new ProxyConfig();
            Matcher m1 = GATHERPROXY_PATTERN.matcher(tds.get(1).toString());
            m1.find();
            Matcher m2 = GATHERPROXY_PATTERN.matcher(tds.get(2).toString());
            m2.find();
            proxy.setHost(m1.group(1));
            proxy.setPort(Integer.parseInt(m2.group(1)));
            proxy.setType("SOCKS");
            proxy.setLocation(tds.get(3).getElementsByTag("a").first().text());
            proxy.setInsertTime(new Date());
            results.add(proxy);
        }

        return results;

    }


}
