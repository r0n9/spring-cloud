package vip.fanrong.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import vip.fanrong.model.Tag;

import java.util.List;

/**
 * Created by Rong on 2018/1/12.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class TagServiceTests {
    @Autowired
    private TagService tagService;

    @Test
    public void testGetTags() {
        List<Tag> tags = tagService.getTags();
        System.out.println(tags);
    }
}
