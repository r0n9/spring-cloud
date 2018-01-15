package vip.fanrong.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import vip.fanrong.model.ProxyConfig;
import vip.fanrong.service.KdsCrawlerService;
import vip.fanrong.service.ProxyCrawlerService;
import vip.fanrong.service.ZmzCrawlerService;

/**
 * Created by Rong on 2017/10/30.
 */
@Configuration
@EnableScheduling
public class ProxyScheduleConfiguration implements SchedulingConfigurer {

    @Autowired
    private ProxyCrawlerService proxyCrawlerService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        // 获取Gatherproxy SOCKS代理
        scheduledTaskRegistrar.addCronTask(() -> {
//            ProxyConfig proxy = proxyCrawlerService.getRandomValidatedProxy("SOCKS");
            proxyCrawlerService.loadSocksProxyConfigsFromGatherproxy(null, null);
        }, "0 5 * * * ?"); //

        // 获取西刺代理
        scheduledTaskRegistrar.addCronTask(() -> {
            proxyCrawlerService.loadProxyConfigsFromXicidaili(null);
        }, "0 10 * * * ?"); // 10分

        // 清洗验证代理
        scheduledTaskRegistrar.addCronTask(() -> {
            proxyCrawlerService.validateProxy(100);
        }, "0 15 * * * ?"); // 15
    }

}
