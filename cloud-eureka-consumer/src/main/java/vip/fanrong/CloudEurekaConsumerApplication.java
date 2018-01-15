package vip.fanrong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

import java.util.TimeZone;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class CloudEurekaConsumerApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(CloudEurekaConsumerApplication.class, args);
    }
}
