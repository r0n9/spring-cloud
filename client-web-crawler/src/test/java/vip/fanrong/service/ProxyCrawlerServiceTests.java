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
    public void testgetSocksProxyConfigsFromGatherproxy() {
        List<ProxyConfig> list = proxyCrawlerService.getSocksProxyConfigsFromGatherproxy(null, "China");
        System.out.println(list.size());
        System.out.println(list.get(0));


        list = proxyCrawlerService.getSocksProxyConfigsFromGatherproxy(list.get(0), null);
        System.out.println(list.size());
        System.out.println(list.get(0));

    }
}
