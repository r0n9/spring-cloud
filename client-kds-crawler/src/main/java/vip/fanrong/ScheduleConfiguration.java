package vip.fanrong;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import vip.fanrong.service.KdsCrawlerService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rong on 2017/10/30.
 */
@Configuration
@EnableScheduling
public class ScheduleConfiguration implements SchedulingConfigurer {
    @Autowired
    KdsCrawlerService kdsCrawlerService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addCronTask(() -> {
            ObjectNode presNode = kdsCrawlerService.getHotTopics(20, null);
            List<KdsCrawlerService.Post> prePosts = new ArrayList<>();
            for (JsonNode node : presNode.get("posts")) {
                prePosts.add(new KdsCrawlerService.Post(node.get("title").asText(),
                        node.get("link").asText(),
                        node.get("imgUrl").asText(),
                        node.get("replyto").asLong(),
                        node.get("userto").asLong(),
                        node.get("aside").asText()));
            }
            kdsCrawlerService.clearHotTopicsCache(20);
            kdsCrawlerService.getHotTopics(20, prePosts);
        }, "0 0/30 * * * ?");
    }
}
