package vip.fanrong.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.web.bind.annotation.*;
import vip.fanrong.LuceneUtils;
import vip.fanrong.common.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/index")
public class SearchServiceController {

    @PostMapping(value = "/create")
    public ObjectNode createIndex(@RequestParam(value = "key", required = true) String key,
                                  @RequestParam(value = "id", required = true) String id,
                                  @RequestBody vip.fanrong.model.Document doc) {
        ObjectNode node = JsonUtil.createObjectNode();

        try {
            LuceneUtils.createIndex(key, id, doc);
        } catch (IOException e) {
            e.printStackTrace();
            node.put("status", "failure");
            return node;
        }

        node.put("status", "success");
        return node;
    }

    @PostMapping(value = "/delete")
    public ObjectNode deleteIndex(@RequestParam(value = "key", required = true) String key,
                                  @RequestParam(value = "id", required = true) String id) {
        ObjectNode node = JsonUtil.createObjectNode();

        try {
            LuceneUtils.deleteIndex(key, id);
        } catch (IOException e) {
            e.printStackTrace();
            node.put("status", "failure");
            return node;
        }

        node.put("status", "success");
        return node;
    }

    @PostMapping(value = "/update")
    public ObjectNode updateIndex(@RequestParam(value = "key", required = true) String key,
                                  @RequestParam(value = "id", required = true) String id,
                                  @RequestBody vip.fanrong.model.Document doc) {
        ObjectNode node = JsonUtil.createObjectNode();

        try {
            LuceneUtils.updateIndex(key, id, doc);
        } catch (IOException e) {
            e.printStackTrace();
            node.put("status", "failure");
            return node;
        }

        node.put("status", "success");
        return node;
    }


    @PostMapping(value = "/search")
    public ObjectNode search(@RequestParam(value = "key", required = true) String key,
                             @RequestParam(value = "keyword", required = true) String keyword,
                             @RequestBody ArrayList<String> fields,
                             @RequestParam(value = "start", required = true) int start,
                             @RequestParam(value = "size", required = true) int size) {

        ObjectNode node = JsonUtil.createObjectNode();
        List<vip.fanrong.model.Document> docs;
        try {
            docs = LuceneUtils.search(key, keyword, fields, start, size);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            node.put("status", "failure");
            return node;
        }
        List<ObjectNode> list = new ArrayList<>();
        for (vip.fanrong.model.Document doc : docs) {
            ObjectNode tmp = JsonUtil.createObjectNode();
            doc.getTextFields().forEach(f -> tmp.put(f.getName(), f.getValue()));
            list.add(tmp);
        }
        node.putPOJO("results", list);
        node.putPOJO("count", list.size());
        node.put("status", "success");
        return node;
    }
}
