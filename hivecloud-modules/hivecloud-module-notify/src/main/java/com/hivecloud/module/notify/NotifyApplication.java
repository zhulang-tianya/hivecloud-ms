package com.hivecloud.module.notify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 消息通知模块启动类
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NotifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotifyApplication.class, args);
    }
}
