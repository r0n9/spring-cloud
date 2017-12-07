package vip.fanrong.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import vip.fanrong.model.ProxyConfig;
import vip.fanrong.model.ZmzResourceTop;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProxyCrawlerServiceTests {

    @Autowired
    private ProxyCrawlerService proxyCrawlerService;


    @Test
    public void testGetSocksProxyConfigsFromGatherproxy() {
        List<ProxyConfig> list = proxyCrawlerService.getSocksProxyConfigsFromGatherproxy(null, "China");
        System.out.println(list.size());
        System.out.println(list.get(0));


        list = proxyCrawlerService.getSocksProxyConfigsFromGatherproxy(list.get(0), null);
        System.out.println(list.size());
        System.out.println(list.get(0));

    }

    @Test
    public void testLoadSocksProxyConfigsFromGatherproxy() {
        ProxyConfig proxyConfig = new ProxyConfig();
        proxyConfig.setHost("177.33.208.3");
        proxyConfig.setPort(14181);
        proxyConfig.setType("SOCKS");
        int loaded = proxyCrawlerService.loadSocksProxyConfigsFromGatherproxy(proxyConfig, null);
        System.out.println(loaded);
    }

    @Test
    public void testTestProxy() {
        ProxyConfig proxyConfig = new ProxyConfig();
        proxyConfig.setHost("177.195.60.15");
        proxyConfig.setPort(32510);
        proxyConfig.setType("SOCKS");
        int status = proxyCrawlerService.testProxy(proxyConfig);

        System.out.println(status);
    }

    @Test
    public void testGetProxyFromXicidaili() {
        List<ProxyConfig> list = proxyCrawlerService.getProxyConfigsFromXicidaili(null);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }

    @Test
    public void testGetRandomProxy() {
        ProxyConfig proxy = proxyCrawlerService.getRandomValidatedProxy();
        System.out.println(proxy);
    }
}
