package vip.fanrong.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.fanrong.common.JsonUtil;
import vip.fanrong.common.MyHttpClient;
import vip.fanrong.mapper.ProxyConfigMapper;
import vip.fanrong.model.ProxyConfig;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProxyCrawlerService {

    private static final Pattern GATHERPROXY_PATTERN = Pattern.compile("document.write\\('(.*)'\\)");

    @Autowired
    private ProxyConfigMapper proxyConfigMapper;

    public Integer testProxy(ProxyConfig proxyConfig) {
        int status = MyHttpClient.testProxy(proxyConfig.getHost(), proxyConfig.getPort(), proxyConfig.getType());
        return status;
    }


    public List<ProxyConfig> getSocksProxyConfigsFromGatherproxy(ProxyConfig proxyConfig, String byCountry) {
        String url = "http://www.gatherproxy.com/zh/sockslist";

        if (StringUtils.isNotBlank(byCountry)) {
            url += "/country/?c=" + byCountry;
        }

        String html = null;
        if (proxyConfig == null || proxyConfig.getHost() == null) {
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
            proxy.setInsertTime(Calendar.getInstance(Locale.CHINA).getTime());
            results.add(proxy);
        }

        return results;

    }

    public int loadSocksProxyConfigsFromGatherproxy(ProxyConfig proxyConfig, String byCountry) {
        List<ProxyConfig> list = getSocksProxyConfigsFromGatherproxy(proxyConfig, byCountry);
        int loaded = proxyConfigMapper.batchInsert(list);
        return loaded;
    }

    public ObjectNode getSocksProxyConfigsNodeFromGatherproxy(ProxyConfig proxyConfig, String byCountry) {
        List<ProxyConfig> list = getSocksProxyConfigsFromGatherproxy(proxyConfig, byCountry);
        ObjectNode objectNode = JsonUtil.createObjectNode();
        objectNode.put("count", list.size());
        objectNode.putPOJO("proxies", list);
        return objectNode;
    }

}
