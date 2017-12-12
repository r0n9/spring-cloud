package vip.fanrong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import vip.fanrong.model.ProxyConfig;
import vip.fanrong.service.ProxyCrawlerService;
import vip.fanrong.service.ZmzCrawlerService;

/**
 * Created by Rong on 2017/10/30.
 */
@Configuration
@EnableScheduling
public class ScheduleConfiguration implements SchedulingConfigurer {
    @Autowired
    private ZmzCrawlerService zmzCrawlerService;

    @Autowired
    private ProxyCrawlerService proxyCrawlerService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        // 获取ZMZ热门资源
        scheduledTaskRegistrar.addCronTask(() -> {
            zmzCrawlerService.loadZmzResourceTops(); // 获取热门资源列表、入库
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            zmzCrawlerService.loadLatestTopMovieResources(null); // 获取电影资源信息、入库
        }, "0 0 6,18 * * ?"); // 每天6点和18点

        // ZMZ账号登陆
        scheduledTaskRegistrar.addCronTask(() -> {
            zmzCrawlerService.zmzAccountLoginAll();
        }, "0 50 3 * * ?"); // 每天3:50

        // 获取Gatherproxy SOCKS代理
        scheduledTaskRegistrar.addCronTask(() -> {
            proxyCrawlerService.loadSocksProxyConfigsFromGatherproxy(null, null);
        }, "0 0 * * * ?"); // 每个整点

        // 获取西刺代理
        scheduledTaskRegistrar.addCronTask(() -> {
            ProxyConfig proxy = proxyCrawlerService.getRandomValidatedProxy();
            proxyCrawlerService.loadProxyConfigsFromXicidaili(proxy);
        }, "0 10 * * * ?"); // 10分

        // 清洗验证代理
        scheduledTaskRegistrar.addCronTask(() -> {
            proxyCrawlerService.validateProxy(100);
        }, "0 20 * * * ?"); // 20分
    }

}
