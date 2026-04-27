package com.hivecloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 网关服务启动类
 * Spring Boot 应用入口
 * 使用@SpringBootApplication 自动配置
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 */
@SpringBootApplication
public class GatewayApplication {

    /**
     * 主方法，启动 Spring Boot 应用
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
