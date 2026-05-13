package com.hivecloud.system.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Redis 配置属性类
 * 用于验证 Redis 配置是否被正确加载
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.redis")
@Slf4j
public class RedisConfigProperties {
    
    private String host = "localhost";
    private int port = 6379;
    private String password;
    private int database = 0;
    
    @PostConstruct
    public void init() {
        log.info("========================================");
        log.info("Redis 配置加载结果：");
        log.info("  host: {}", host);
        log.info("  port: {}", port);
        log.info("  password: {}", password != null ? "******" : "null");
        log.info("  database: {}", database);
        log.info("========================================");
    }
}