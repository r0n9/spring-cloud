package vip.fanrong.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Rong on 2017/11/22.
 */
@FeignClient("client-web-crawler")
public interface KdsCrawlerClient {

    @GetMapping("/kds/topics/get")
    ObjectNode getHotTopics(@RequestParam(value = "limit", required = false, defaultValue = "20") int limit);
}
