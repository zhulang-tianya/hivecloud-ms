package com.hivecloud.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 系统管理模块启动类
 * 启用 Nacos 服务发现
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.hivecloud.system.mapper")
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}
