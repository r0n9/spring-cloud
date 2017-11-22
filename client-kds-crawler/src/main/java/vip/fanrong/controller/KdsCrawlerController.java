package vip.fanrong.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vip.fanrong.service.KdsCrawlerService;

/**
 * Created by Rong on 2017/7/14.
 */
@RestController
@RequestMapping(value = "/v1/kds") // parent folder for request mapping path
@Api(value = "KDS Crawler API", description = "v1")
@CrossOrigin(maxAge = 3600) // 后端接口支持跨域CORS调用
public class KdsCrawlerController {

    @Autowired
    private KdsCrawlerService kdsCrawlerService;

    @ApiOperation(value = "获取近期热门帖", notes = "按照回帖数量排行")
    @RequestMapping(value = "/getHotTopics", method = RequestMethod.GET)
    public ObjectNode getHotTopics(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        return kdsCrawlerService.getHotTopics(limit, null);
    }

    @ApiOperation(value = "获取最新回复帖", notes = "可以指定第几页，默认第一页")
    @RequestMapping(value = "/getReply", method = RequestMethod.GET)
    public ObjectNode getByReplyOrder(@RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo) {
        return kdsCrawlerService.getByReplyOrder(pageNo);
    }

    @ApiOperation(value = "获取最新发布帖", notes = "可以指定第几页，默认第一页")
    @RequestMapping(value = "/getCreate", method = RequestMethod.GET)
    public ObjectNode getByCreateOrder(@RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo) {
        return kdsCrawlerService.getByCreateOrder(pageNo);
    }


}
