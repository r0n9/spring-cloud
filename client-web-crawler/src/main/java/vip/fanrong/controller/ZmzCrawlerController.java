package vip.fanrong.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vip.fanrong.common.JsonUtil;
import vip.fanrong.model.ProxyConfig;
import vip.fanrong.model.ZmzAccount;
import vip.fanrong.model.ZmzResourceTop;
import vip.fanrong.service.ProxyCrawlerService;
import vip.fanrong.service.ZmzCrawlerService;

import java.util.List;

/**
 * Created by Rong on 2017/7/14.
 */
@RestController
@RequestMapping(value = "/crawler/zmz") // parent folder for request mapping path
@Api(value = "Web Crawler API", description = "v1")
public class ZmzCrawlerController {

    @Autowired
    private ZmzCrawlerService zmzCrawlerService;

    @Autowired
    private ProxyCrawlerService proxyCrawlerService;

    @ApiOperation(value = "Resource Tops", notes = "Resource Tops")
    @RequestMapping(value = "/resource/tops", method = RequestMethod.GET)
    public ObjectNode getZmzResourceTops() {
        List<ZmzResourceTop> list = zmzCrawlerService.getZmzResourceTops();
        ObjectNode objectNode = JsonUtil.createObjectNode();
        objectNode.put("count", list.size());
        objectNode.putPOJO("resource", list);
        return objectNode;
    }

    @ApiOperation(value = "Load Resource Tops", notes = "Load Resource Tops")
    @RequestMapping(value = "/resource/loadtops", method = RequestMethod.POST)
    public ObjectNode loadZmzResourceTops() {
        int loaded = zmzCrawlerService.loadZmzResourceTops();
        ObjectNode node = JsonUtil.createObjectNode();
        return node.put("loaded", loaded);
    }

    @ApiOperation(value = "Register a random account")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ObjectNode registerRandom() {
        ProxyConfig proxyConfig = proxyCrawlerService.getRandomValidatedProxy();
        ZmzAccount account = zmzCrawlerService.registerZmzAccountRandom(proxyConfig);
        ObjectNode node = JsonUtil.createObjectNode();
        node.putPOJO("account", account);
        return node;
    }

}
