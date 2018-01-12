package vip.fanrong.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import vip.fanrong.model.Blog;

import java.util.List;

/**
 * Created by Rong on 2018/1/11.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlogServiceTests {
    @Autowired
    private BlogService blogService;

    @Test
    public void testShowBlogs() {
        List<Blog> list = blogService.showBlogs();
        System.out.println(list.size());
        System.out.println(list);
    }
}
