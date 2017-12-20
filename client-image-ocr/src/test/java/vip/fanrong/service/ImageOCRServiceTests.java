package vip.fanrong.service;

import org.junit.Test;

/**
 * Created by Rong on 2017/12/20.
 */
public class ImageOCRServiceTests {

    @Test
    public void testRecognize() {
        ImageOCRService imageOCRService = new ImageOCRService();

        String result = imageOCRService.recognize("http://119.254.106.121/bdrs_rk/query/createImageAction.do?method=getImg&t=Wed%20Dec%2020%202017%2020:58:03%20GMT+0800%20(%D6%D0%B9%FA%B1%EA%D7%BC%CA%B1%BC%E4)");
        System.out.println(result);

    }
}
