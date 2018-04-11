package vip.fanrong.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vip.fanrong.common.JsonUtil;
import vip.fanrong.service.SearchEngineService;

import javax.naming.directory.SearchResult;
import java.util.List;

/**
 * Created by Rong on 2017/7/14.
 */
@RestController
@RequestMapping(value = "/search")
@Api(value = "Search Engine API", description = "搜索引擎接口")
public class SearchEngineController {

    @Autowired
    private SearchEngineService searchEngineService;


    @ApiOperation(value = "Google搜索", notes = "Google搜索")
    @RequestMapping(value = "/google", method = RequestMethod.POST)
    public ObjectNode searchGoogle(@RequestParam(required = true) String key,
                                   @RequestParam(required = false, defaultValue = "1") int pageNum,
                                   @RequestBody(required = false) String pageUrl) throws Exception {

        return searchEngineService.searchGoogle(key, pageNum, pageUrl);
    }

    @ApiOperation(value = "百度搜索", notes = "Baidu搜索")
    @RequestMapping(value = "/baidu", method = RequestMethod.POST)
    public ObjectNode searchBaidu(@RequestParam(required = true) String key,
                                  @RequestParam(required = false, defaultValue = "1") int pageNum,
                                  @RequestBody(required = false) String pageUrl) throws Exception {

        return searchEngineService.searchBaidu(key, pageNum, pageUrl);
    }


}
