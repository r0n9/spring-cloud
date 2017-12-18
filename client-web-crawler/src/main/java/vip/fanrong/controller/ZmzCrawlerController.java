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
@RequestMapping(value = "/zmz") // parent folder for request mapping path
@Api(value = "Zmz Crawler API", description = "ZMZ相关接口")
public class ZmzCrawlerController {

    @Autowired
    private ZmzCrawlerService zmzCrawlerService;

    @Autowired
    private ProxyCrawlerService proxyCrawlerService;

    @ApiOperation(value = "Load Resource Tops", notes = "Load Resource Tops")
    @RequestMapping(value = "/resource/loadtops", method = RequestMethod.POST)
    public ObjectNode loadZmzResourceTops() {
        ProxyConfig proxyConfig = proxyCrawlerService.getRandomValidatedProxy();
        int tops = zmzCrawlerService.loadZmzResourceTops(proxyConfig); // 获取热门资源列表、入库
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int newMovieResources = zmzCrawlerService.loadLatestTopMovieResources(proxyConfig); // 获取电影资源信息、入库
        int newTvResources = zmzCrawlerService.loadLatestTopTVResources(proxyConfig); // 获取最新热门电视剧资源信息、入库

        ObjectNode node = JsonUtil.createObjectNode();
        node.put("tops", tops);
        node.put("newMovieResources", newMovieResources);
        node.put("newTvResources", newTvResources);
        return node;
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

    @ApiOperation(value = "Login all of the accounts")
    @RequestMapping(value = "/alllogin", method = RequestMethod.POST)
    public ObjectNode loginAll() {
        ProxyConfig proxyConfig = proxyCrawlerService.getRandomValidatedProxy();
        String result = zmzCrawlerService.zmzAccountLoginAll();
        ObjectNode node = JsonUtil.createObjectNode();
        node.put("result", result);
        return node;
    }

}
