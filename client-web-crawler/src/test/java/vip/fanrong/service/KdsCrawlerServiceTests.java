package vip.fanrong.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import vip.fanrong.model.KdsTopic;

import java.util.List;

/**
 * Created by Rong on 2017/12/18.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class KdsCrawlerServiceTests {
    @Autowired
    private KdsCrawlerService kdsCrawlerService;

    @Test
    public void testGetPopularTopics() {
        int loaded = kdsCrawlerService.loadPopularTopics(null, 20);
        System.out.println(loaded);
    }
}
