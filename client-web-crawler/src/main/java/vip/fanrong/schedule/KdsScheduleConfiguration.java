package vip.fanrong.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import vip.fanrong.service.KdsCrawlerService;

/**
 * Created by Rong on 2017/10/30.
 */
@Configuration
@EnableScheduling
public class KdsScheduleConfiguration implements SchedulingConfigurer {

    @Autowired
    private KdsCrawlerService kdsCrawlerService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        // KDS热门
        scheduledTaskRegistrar.addCronTask(() -> {
            kdsCrawlerService.loadPopularTopics(null, 20);
        }, "0 25,55 * * * ?"); // 25/55分
    }

}
