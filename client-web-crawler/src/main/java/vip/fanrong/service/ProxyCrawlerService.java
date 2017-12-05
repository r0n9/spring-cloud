package vip.fanrong.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.fanrong.common.JsonUtil;
import vip.fanrong.common.MyHttpClient;
import vip.fanrong.mapper.ProxyConfigMapper;
import vip.fanrong.mapper.ProxyConfigValidatedMapper;
import vip.fanrong.model.ProxyConfig;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProxyCrawlerService {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyCrawlerService.class);

    private static final Pattern GATHERPROXY_PATTERN = Pattern.compile("document.write\\('(.*)'\\)");

    @Autowired
    private ProxyConfigMapper proxyConfigMapper;
    @Autowired
    private ProxyConfigValidatedMapper proxyConfigValidatedMapper;

    private static boolean isValidateProxyRunning = false;

    public Integer testProxy(ProxyConfig proxyConfig) {
        int status = MyHttpClient.testProxy(proxyConfig.getHost(), proxyConfig.getPort(), proxyConfig.getType());
        return status;
    }

    public Integer validateProxy(int limit) {

        if (isValidateProxyRunning) {
            LOG.warn("Validate Proxy start failed because this method is running.");
            return 0;
        }
        isValidateProxyRunning = true;
        LOG.info("Validate Proxy is running...");

        List<ProxyConfig> list = proxyConfigMapper.getProxyConfigsByLimit(limit);
        if (list == null || list.isEmpty()) {
            return 0;
        }

        int validated = 0;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (ProxyConfig pc : list) {
            if (StringUtils.isNotBlank(pc.getStatus())) {
                continue;
            }

            FutureTask<Integer> future = new FutureTask<>(() -> testProxy(pc));
            executorService.execute(future);
            int statusCode;
            try {
                statusCode = future.get(120, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                statusCode = HttpStatus.SC_REQUEST_TIMEOUT;
            } catch (Exception e) {
                statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
            }

            pc.setStatus(String.valueOf(statusCode));
            pc.setStatusUpdateTime(Calendar.getInstance(Locale.CHINA).getTime());
            proxyConfigMapper.update(pc);

            if (statusCode == HttpStatus.SC_OK) {
                proxyConfigValidatedMapper.insert(pc);
                validated++;
            }
        }

        executorService.shutdown();

        LOG.info("Validate Proxy is end...");
        isValidateProxyRunning = false;

        return validated;
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
