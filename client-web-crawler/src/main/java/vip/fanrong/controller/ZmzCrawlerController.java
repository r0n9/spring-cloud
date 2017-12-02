package vip.fanrong.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vip.fanrong.service.ZmzCrawlerService;

/**
 * Created by Rong on 2017/7/14.
 */
@RestController
@RequestMapping(value = "/v1/kds") // parent folder for request mapping path
@Api(value = "KDS Crawler API", description = "v1")
public class ZmzCrawlerController {

    @Autowired
    private ZmzCrawlerService zmzCrawlerService;


}
