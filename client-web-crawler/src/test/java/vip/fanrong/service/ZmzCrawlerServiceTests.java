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


    @Test
    public void testService() {
        List<ZmzResourceTop> list = zmzCrawlerService.getZmzResourceTops();
        System.out.println(list);
    }
}
