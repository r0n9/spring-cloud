package vip.fanrong.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import vip.fanrong.model.Document;

import java.util.ArrayList;

/**
 * Created by Rong on 2018/1/18.
 */
@FeignClient("client-search-service")
public interface SearchServiceClient {

    @PostMapping("/index/create")
    ObjectNode createIndex(@RequestParam(value = "key", required = true) String key,
                           @RequestParam(value = "id", required = true) String id,
                           @RequestBody Document doc);

    @PostMapping("/index/delete")
    ObjectNode deleteIndex(@RequestParam(value = "key", required = true) String key,
                           @RequestParam(value = "id", required = true) String id);

    @PostMapping("/index/update")
    ObjectNode updateIndex(@RequestParam(value = "key", required = true) String key,
                           @RequestParam(value = "id", required = true) String id,
                           @RequestBody Document doc);

    @PostMapping("/index/search")
    ObjectNode search(@RequestParam(value = "key", required = true) String key,
                      @RequestParam(value = "keyword", required = true) String keyword,
                      @RequestBody ArrayList<String> fields,
                      @RequestParam(value = "start", required = true) int start,
                      @RequestParam(value = "size", required = true) int size);
}
