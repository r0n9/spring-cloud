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
    @RequestMapping(value = "/google", method = RequestMethod.GET)
    public ObjectNode searchGoogle(@RequestParam(required = true) String key,
                                   @RequestParam(required = false, defaultValue = "1") int pageNum) {

        return searchEngineService.searchGoogle(key, pageNum);
    }

    @ApiOperation(value = "百度搜索", notes = "Baidu搜索")
    @RequestMapping(value = "/baidu", method = RequestMethod.GET)
    public ObjectNode searchBaidu(@RequestParam(required = true) String key,
                                  @RequestParam(required = false, defaultValue = "1") int pageNum) {

        return searchEngineService.searchBaidu(key, pageNum);
    }


}
