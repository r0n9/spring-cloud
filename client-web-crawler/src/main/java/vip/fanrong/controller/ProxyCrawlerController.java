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
@RequestMapping(value = "/crawler/proxy") // parent folder for request mapping path
@Api(value = "Proxy Crawler API", description = "v1")
public class ProxyCrawlerController {

    @Autowired
    private ProxyCrawlerService proxyCrawlerService;

    @ApiOperation(value = "SOCKS proxy from Gatherproxy", notes = "Gatherproxy")
    @RequestMapping(value = "/socks/gatherproxy", method = RequestMethod.POST)
    public ObjectNode getZmzResourceTops(@RequestBody(required = false) ProxyConfig proxyConfig,
                                         @RequestParam(required = false) String country) {
        return proxyCrawlerService.getSocksProxyConfigsNodeFromGatherproxy(proxyConfig, country);
    }

    @ApiOperation(value = "Load SOCKS proxy from Gatherproxy", notes = "Gatherproxy")
    @RequestMapping(value = "/socks/gatherproxy/load", method = RequestMethod.POST)
    public ObjectNode loadZmzResourceTops(@RequestBody(required = false) ProxyConfig proxyConfig,
                                          @RequestParam(required = false) String country) {
        int loaded = proxyCrawlerService.loadSocksProxyConfigsFromGatherproxy(proxyConfig, country);
        ObjectNode node = JsonUtil.createObjectNode();
        return node.put("loaded", loaded);
    }


}
