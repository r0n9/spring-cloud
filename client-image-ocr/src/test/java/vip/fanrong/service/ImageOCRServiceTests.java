package vip.fanrong.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Rong on 2017/12/20.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageOCRServiceTests {

    @Autowired
    private ImageOCRService imageOCRService;

    @Test
    public void testRecognize() {
        String result = imageOCRService.recognize("http://119.254.106.121/bdrs_rk/query/createImageAction.do?method=getImg");
        System.out.println(result);
    }

    @Test
    public void testRecognizeKds() {
        String result = imageOCRService.recognizeKds("https://passport.pchome.net/index.php?c=vcode");
        System.out.println(result);
    }
}
