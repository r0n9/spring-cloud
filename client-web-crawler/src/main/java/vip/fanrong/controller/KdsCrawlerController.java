package vip.fanrong.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vip.fanrong.common.JsonUtil;
import vip.fanrong.model.KdsTopic;
import vip.fanrong.model.ProxyConfig;
import vip.fanrong.service.KdsCrawlerService;
import vip.fanrong.service.ProxyCrawlerService;

import java.util.List;

/**
 * Created by Rong on 2017/7/14.
 */
@RestController
@RequestMapping(value = "/kds")
@Api(value = "KDS Crawler API", description = "KDS相关接口")
public class KdsCrawlerController {

    @Autowired
    private KdsCrawlerService kdsCrawlerService;

    @ApiOperation(value = "Load Popular Tops", notes = "近期热门帖子入库")
    @RequestMapping(value = "/topics/load", method = RequestMethod.POST)
    public ObjectNode loadKdsPopular(@RequestParam(required = false, defaultValue = "20") int limit) {
        ProxyConfig proxyConfig = null;
        int loaded = kdsCrawlerService.loadPopularTopics(proxyConfig, limit);
        ObjectNode node = JsonUtil.createObjectNode();
        node.put("loaded", loaded);
        return node;
    }

    @ApiOperation(value = "Get Popular Tops", notes = "近期热门帖子出库")
    @RequestMapping(value = "/topics/get", method = RequestMethod.GET)
    public ObjectNode getKdsPopular(@RequestParam(required = false, defaultValue = "20") int limit) {
        List<KdsTopic> list = kdsCrawlerService.selectPopularTopics(limit);
        ObjectNode node = JsonUtil.createObjectNode();
        node.put("count", list.size());
        node.putPOJO("topics", list);
        return node;
    }

}
