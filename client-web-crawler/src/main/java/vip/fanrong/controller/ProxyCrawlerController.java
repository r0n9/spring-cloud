package vip.fanrong.controller;


import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.fanrong.service.ProxyCrawlerService;

@RestController
@RequestMapping(value = "/crawler/proxy") // parent folder for request mapping path
@Api(value = "Proxy Crawler API", description = "v1")
public class ProxyCrawlerController {

    @Autowired
    private ProxyCrawlerService proxyCrawlerService;








}
