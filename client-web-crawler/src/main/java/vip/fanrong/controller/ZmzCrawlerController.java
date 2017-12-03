package vip.fanrong.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vip.fanrong.common.JsonUtil;
import vip.fanrong.service.ZmzCrawlerService;

/**
 * Created by Rong on 2017/7/14.
 */
@RestController
@RequestMapping(value = "/crawler/zmz") // parent folder for request mapping path
@Api(value = "Web Crawler API", description = "v1")
public class ZmzCrawlerController {

    @Autowired
    private ZmzCrawlerService zmzCrawlerService;

    @ApiOperation(value = "Resource Tops", notes = "Resource Tops")
    @RequestMapping(value = "/resource/tops", method = RequestMethod.GET)
    public ObjectNode getZmzResourceTops(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        return zmzCrawlerService.getZmzResourceTopsNode();
    }

    @ApiOperation(value = "Load Resource Tops", notes = "Load Resource Tops")
    @RequestMapping(value = "/resource/loadtops", method = RequestMethod.POST)
    public ObjectNode loadZmzResourceTops(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        int loaded = zmzCrawlerService.loadZmzResourceTops();
        ObjectNode node = JsonUtil.createObjectNode();
        return node.put("loaded", loaded);
    }

}
