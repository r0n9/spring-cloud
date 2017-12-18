package vip.fanrong.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vip.fanrong.common.JsonUtil;
import vip.fanrong.model.ProxyConfig;
import vip.fanrong.service.ProxyCrawlerService;

@RestController
@RequestMapping(value = "/proxy") // parent folder for request mapping path
@Api(value = "Proxy Crawler API", description = "代理相关接口")
public class ProxyCrawlerController {

    @Autowired
    private ProxyCrawlerService proxyCrawlerService;

    @ApiOperation(value = "Load SOCKS proxy from Gatherproxy", notes = "从Gatherproxy获取最新SOCKS代理")
    @RequestMapping(value = "/gather/load", method = RequestMethod.POST)
    public ObjectNode loadSocksProxyFromGatgerproxy(@RequestParam(required = false) String country) {
        ProxyConfig proxyConfig = proxyCrawlerService.getRandomValidatedProxy("SOCKS");
        int loaded = proxyCrawlerService.loadSocksProxyConfigsFromGatherproxy(proxyConfig, country);
        ObjectNode node = JsonUtil.createObjectNode();
        return node.put("loaded", loaded);
    }

    @ApiOperation(value = "Load proxy from Xicidaili", notes = "从西刺代理获取最新代理")
    @RequestMapping(value = "/xici/load", method = RequestMethod.POST)
    public ObjectNode loadProxyFromXicidaili(@RequestParam(required = false) String country) {
//        ProxyConfig proxyConfig = proxyCrawlerService.getRandomValidatedProxy();
        int loaded = proxyCrawlerService.loadProxyConfigsFromXicidaili(null);
        ObjectNode node = JsonUtil.createObjectNode();
        return node.put("loaded", loaded);
    }

    @ApiOperation(value = "Validate Proxy", notes = "清洗并验证最新获取的代理")
    @RequestMapping(value = "/validate", method = RequestMethod.GET)
    public ObjectNode validateProxy(@RequestParam(required = false, defaultValue = "100") int limit) {
        int validated = proxyCrawlerService.validateProxy(limit);
        ObjectNode node = JsonUtil.createObjectNode();
        return node.put("validated", String.valueOf(validated));


    }

    @ApiOperation(value = "Get random validated proxy", notes = "随机获取代理并验证")
    @RequestMapping(value = "/random", method = RequestMethod.GET)
    public ObjectNode getRandomProxy() {
        ProxyConfig proxyConfig = proxyCrawlerService.getRandomValidatedProxy();
        ObjectNode objectNode = JsonUtil.createObjectNode();
        objectNode.putPOJO("proxy", proxyConfig);
        return objectNode;
    }

}
