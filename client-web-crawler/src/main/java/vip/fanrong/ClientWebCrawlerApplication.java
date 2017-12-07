package vip.fanrong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ClientWebCrawlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientWebCrawlerApplication.class, args);
    }
}