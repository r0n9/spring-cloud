package vip.fanrong.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vip.fanrong.client.KdsCrawlerClient;

/**
 * Created by Rong on 2017/11/22.
 */
@CrossOrigin(maxAge = 3600)
@RestController
public class ConsumerController {
    @Autowired
    KdsCrawlerClient kdsCrawlerClient;

    @GetMapping("/kdshot")
    public ObjectNode getHotTopics(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        return kdsCrawlerClient.getHotTopics(limit);
    }
}
