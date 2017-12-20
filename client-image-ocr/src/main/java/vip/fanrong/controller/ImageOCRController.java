package vip.fanrong.controller;


import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vip.fanrong.common.JsonUtil;
import vip.fanrong.service.ImageOCRService;

@RestController
@RequestMapping(value = "/ocr")
public class ImageOCRController {

    @Autowired
    private ImageOCRService imageOCRService;

    @RequestMapping(value = "/recognizeImageUrl", method = RequestMethod.GET)
    public ObjectNode recognizeImageUrl(@RequestParam(required = true) String imageUrl) {
        String result = imageOCRService.recognize(imageUrl);

        ObjectNode node = JsonUtil.createObjectNode();
        node.put("imageUrl", imageUrl);
        node.put("result", result);

        return node;
    }

}
