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
public class ZmzScheduleConfiguration implements SchedulingConfigurer {
    @Autowired
    private ZmzCrawlerService zmzCrawlerService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        // 获取ZMZ热门资源
        scheduledTaskRegistrar.addCronTask(() -> {
            zmzCrawlerService.loadZmzResourceTops(null); // 获取热门资源列表、入库
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            zmzCrawlerService.loadLatestTopMovieResources(null); // 获取最新热门电影资源信息、入库

            zmzCrawlerService.loadLatestTopTVResources(null); // 获取最新热门电视剧资源信息、入库
        }, "0 0 */2 * * ?");

        // ZMZ账号登陆
        scheduledTaskRegistrar.addCronTask(() -> {
            zmzCrawlerService.zmzAccountLoginAll();
        }, "0 50 3,15 * * ?"); // 每天3:50
    }

}
