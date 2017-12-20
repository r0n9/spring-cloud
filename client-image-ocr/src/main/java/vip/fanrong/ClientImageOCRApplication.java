package vip.fanrong;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.io.File;
import java.util.TimeZone;

/**
 * Created by Rong on 2017/12/20.
 */

@EnableDiscoveryClient
@SpringBootApplication
public class ClientImageOCRApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        File folder = new File("./../images/");
        folder.mkdirs();
        SpringApplication.run(ClientImageOCRApplication.class, args);
    }
}
