package vip.fanrong.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import vip.fanrong.common.MyHttpResponse;
import vip.fanrong.mapper.MovieResourceMapper;
import vip.fanrong.mapper.ZmzResourceTopMapper;
import vip.fanrong.model.MovieResource;
import vip.fanrong.model.ZmzAccount;
import vip.fanrong.model.ZmzResourceTop;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZmzCrawlerServiceTests {

    @Autowired
    private ZmzCrawlerService zmzCrawlerService;

    @Autowired
    MovieResourceMapper movieResourceMapper;

    @Autowired
    ZmzResourceTopMapper zmzResourceTopMapper;

    @Test
    public void testGetZmzResourceTops() {
        List<ZmzResourceTop> list = zmzCrawlerService.getZmzResourceTops(null);
        System.out.println(list);
    }

    @Test
    public void testGetResourceById() {
        MovieResource movieResource = zmzCrawlerService.getMovieResourceByZmzResourceId(null, "35726");
        System.out.println(movieResource);

        movieResourceMapper.insert(movieResource);

        System.out.println(movieResourceMapper.selectBySourceAndResourceId("zmz", "35726"));
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

    @Test
    public void testGetMovieResourceByZmzResourceId() {
        MovieResource movieResource = zmzCrawlerService.getMovieResourceByZmzResourceId(null, "10840");
        System.out.println(movieResource);
    }

    @Test
    public void testLoadLatestTopMovieResources() {
        int count = zmzCrawlerService.loadLatestTopMovieResources(null);
        System.out.println("Found " + count);
    }

    @Test
    public void testLoadLatestTopTvResources() {
        zmzCrawlerService.loadLatestTopTVResources(null);
    }
}
