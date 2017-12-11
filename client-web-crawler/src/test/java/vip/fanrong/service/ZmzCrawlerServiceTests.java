package vip.fanrong.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import vip.fanrong.model.ZmzResourceTop;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZmzCrawlerServiceTests {

    @Autowired
    private ZmzCrawlerService zmzCrawlerService;

    @Autowired
    ProxyCrawlerService proxyCrawlerService;


    @Test
    public void testGetZmzResourceTops() {
        List<ZmzResourceTop> list = zmzCrawlerService.getZmzResourceTops();
        System.out.println(list);
    }

    @Test
    public void testGetResourceById() {
        zmzCrawlerService.getMovieResourceByZmzResourceId(null, "35726");
    }

    @Test
    public void testRegister() {
        for (int i = 0; i < 10; i++) {
            zmzCrawlerService.registerZmzAccountRandom(null);
        }
    }

    @Test
    public void testLoginAll() {
        zmzCrawlerService.zmzAccountLoginAll();
    }
}
