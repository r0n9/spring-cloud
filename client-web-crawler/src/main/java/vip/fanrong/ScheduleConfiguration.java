package vip.fanrong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import vip.fanrong.service.ProxyCrawlerService;
import vip.fanrong.service.ZmzCrawlerService;

/**
 * Created by Rong on 2017/10/30.
 */
@Configuration
@EnableScheduling
public class ScheduleConfiguration implements SchedulingConfigurer {
    @Autowired
    ZmzCrawlerService zmzCrawlerService;

    @Autowired
    ProxyCrawlerService proxyCrawlerService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addCronTask(() -> {
            zmzCrawlerService.loadZmzResourceTops();
        }, "0 0 6,18 * * ?");


        scheduledTaskRegistrar.addCronTask(() -> {
            proxyCrawlerService.loadSocksProxyConfigsFromGatherproxy(null, null);
        }, "0 0 * * * ?");
    }
}
