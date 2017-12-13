package vip.fanrong.service;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.fanrong.common.MyHttpClient;
import vip.fanrong.common.MyHttpResponse;
import vip.fanrong.mapper.ProxyConfigMapper;
import vip.fanrong.mapper.ProxyConfigValidatedMapper;
import vip.fanrong.model.ProxyConfig;

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

    Integer testProxy(ProxyConfig proxyConfig) {
        int status = MyHttpClient.testProxy(proxyConfig.getHost(), proxyConfig.getPort(), proxyConfig.getType());
        return status;
    }

    public ProxyConfig getRandomValidatedProxy() {
        List<ProxyConfig> list = proxyConfigValidatedMapper.getValidatedProxyConfigsByLimit(50);
        return getRandomValidatedProxy(list);
    }

    private ProxyConfig getRandomValidatedProxy(List<ProxyConfig> list) {
        if (list == null || list.isEmpty()) {
            LOG.warn("No validated proxy!!!!!");
            return null;
        }
        Random rand = new Random(System.currentTimeMillis());
        int index = rand.nextInt(list.size());
        ProxyConfig proxy = list.get(index);
        if (HttpStatus.SC_OK == testProxy(proxy)) {
            return proxy;
        } else {
            list.remove(index);
            proxyConfigValidatedMapper.delete(proxy.getId());
            LOG.info("Proxy removed: " + proxy);
            return getRandomValidatedProxy(list);
        }
    }

    public String getProxyInfo(ProxyConfig proxyConfig) {
        HttpGet request = new HttpGet("http://ip.chinaz.com/getip.aspx");
        MyHttpResponse myHttpResponse = MyHttpClient.getHttpResponse(request, null, null,
                proxyConfig.getHost(), proxyConfig.getPort(), proxyConfig.getType());
        return myHttpResponse.getHtml();
    }

    public Integer validateProxy(int limit) {

        if (isValidateProxyRunning) {
            LOG.warn("Validate Proxy start failed because this method is running.");
            return 0;
        }
        isValidateProxyRunning = true;
        int validated = 0;
        LOG.info("Validate Proxy is running...");
        try {
            List<ProxyConfig> list = proxyConfigMapper.getProxyConfigsByLimit(limit);
            if (list == null || list.isEmpty()) {
                return 0;
            }

            ExecutorService executorService = Executors.newFixedThreadPool(10);
            List<FutureTask<ProxyConfig>> tasks = new ArrayList<>();
            for (ProxyConfig pc : list) {
                if (StringUtils.isNotBlank(pc.getStatus())) {
                    continue;
                }

                FutureTask<ProxyConfig> task = new FutureTask<>(() -> {
                    try {
                        int status = testProxy(pc);
                        pc.setStatus(String.valueOf(status));
                    } catch (Exception e) {
                        LOG.warn("Proxy validate failed: " + pc);
                        pc.setStatus(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
                        return pc;
                    } finally {
                        return pc;
                    }
                });

                executorService.submit(task);
                tasks.add(task);
            }

            for (Future<ProxyConfig> task : tasks) {
                ProxyConfig proxyConfig = null;
                try {
                    proxyConfig = task.get(2, TimeUnit.MINUTES);
                } catch (TimeoutException e) {
                    if (null == proxyConfig) {
                        continue;
                    }
                    proxyConfig.setStatus(String.valueOf(HttpStatus.SC_REQUEST_TIMEOUT));
                } catch (Exception e) {
                    if (null == proxyConfig) {
                        continue;
                    }
                    proxyConfig.setStatus(String.valueOf(HttpStatus.SC_REQUEST_TIMEOUT));
                }

                proxyConfig.setStatusUpdateTime(Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")).getTime());
                proxyConfigMapper.update(proxyConfig);

                if (String.valueOf(HttpStatus.SC_OK).equalsIgnoreCase(proxyConfig.getStatus())) {
                    proxyConfigValidatedMapper.insert(proxyConfig);
                    validated++;
                }
            }

            executorService.shutdown();

        } finally {
            LOG.info("Validate Proxy is end...");
            isValidateProxyRunning = false;
            return validated;
        }
    }


    public List<ProxyConfig> getSocksProxyConfigsFromGatherproxy(ProxyConfig proxyConfig, String byCountry) {
        String url = "http://www.gatherproxy.com/zh/sockslist";
        if (StringUtils.isNotBlank(byCountry)) {
            url += "/country/?c=" + byCountry;
        }
        HttpGet request = new HttpGet(url);

        String html;
        MyHttpResponse response;
        if (proxyConfig == null || proxyConfig.getHost() == null) {
            response = MyHttpClient.getHttpResponse(request, null, null);
        } else {
            response = MyHttpClient.getHttpResponse(request, null, null,
                    proxyConfig.getHost(), proxyConfig.getPort(), proxyConfig.getType());
        }

        List<ProxyConfig> results = new ArrayList<>();
        if (null == response) {
            return results;
        } else {
            html = response.getHtml();
        }

        Document doc = Jsoup.parse(html);
        Elements trs = doc.getElementsByTag("tr");
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
            proxy.setInsertTime(Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")).getTime());
            results.add(proxy);
        }

        return results;

    }

    public int loadSocksProxyConfigsFromGatherproxy(ProxyConfig proxyConfig, String byCountry) {
        List<ProxyConfig> list = getSocksProxyConfigsFromGatherproxy(proxyConfig, byCountry);
        if (list == null || list.isEmpty()) {
            return 0;
        }
        return proxyConfigMapper.batchInsert(list);
    }

    public List<ProxyConfig> getProxyConfigsFromXicidaili(ProxyConfig proxyConfig) {
        String url = "http://www.xicidaili.com";
        HttpGet request = new HttpGet(url);

        String html;
        if (proxyConfig == null || proxyConfig.getHost() == null) {
            html = MyHttpClient.getHttpResponse(request, null, null).getHtml();
        } else {
            html = MyHttpClient.getHttpResponse(request, null, null,
                    proxyConfig.getHost(), proxyConfig.getPort(), proxyConfig.getType()).getHtml();
        }

        Document doc = Jsoup.parse(html);
        Elements trs = doc.getElementsByTag("tr");
        List<ProxyConfig> results = new ArrayList<>();
        for (Element tr : trs) {
            Elements tds = tr.getElementsByTag("td");
            if (tds == null || tds.size() != 8) {
                continue;
            }
            ProxyConfig proxy = new ProxyConfig();

            proxy.setHost(tds.get(1).text());
            proxy.setPort(Integer.parseInt(tds.get(2).text()));
            String type = tds.get(5).text();
            if (StringUtils.containsIgnoreCase(type, "socks")) {
                type = "SOCKS";
            } else if (StringUtils.containsIgnoreCase(type, "http")) {
                type = "HTTP";
            } else {
                LOG.warn("Unknown proxy type: [" + type + "] from " + url);
                continue;
            }
            proxy.setType(type);
            proxy.setLocation(tds.get(3).text());
            proxy.setInsertTime(Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")).getTime());
            results.add(proxy);
        }

        return results;

    }

    public int loadProxyConfigsFromXicidaili(ProxyConfig proxyConfig) {
        List<ProxyConfig> list = getProxyConfigsFromXicidaili(proxyConfig);
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int loaded = proxyConfigMapper.batchInsert(list);
        return loaded;
    }
}
