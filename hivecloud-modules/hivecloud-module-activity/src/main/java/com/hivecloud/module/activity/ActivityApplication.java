package com.hivecloud.module.activity;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 营销活动模块启动类
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.hivecloud.module.activity.mapper")
public class ActivityApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActivityApplication.class, args);
    }
}
