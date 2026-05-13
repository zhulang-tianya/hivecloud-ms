package com.hivecloud.system.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Redis 连接配置检查
 * 在应用启动时打印实际使用的 Redis 配置
 */
@Configuration
@Slf4j
public class RedisConnectionConfig {
    
    private final Environment environment;
    
    public RedisConnectionConfig(Environment environment) {
        this.environment = environment;
    }
    
    @PostConstruct
    public void init() {
        // 从 Environment 直接读取，确保获取最新配置
        String host = environment.getProperty("spring.redis.host", "localhost");
        int port = Integer.parseInt(environment.getProperty("spring.redis.port", "6379"));
        String password = environment.getProperty("spring.redis.password");
        int database = Integer.parseInt(environment.getProperty("spring.redis.database", "0"));
        
        log.info("========================================");
        log.info("实际 Redis 配置：");
        log.info("  host: {}", host);
        log.info("  port: {}", port);
        log.info("  password: {}", password != null ? "******" : "null");
        log.info("  database: {}", database);
        log.info("  timeout: {}", environment.getProperty("spring.redis.timeout"));
        log.info("========================================");
        
        // 如果 host 是 localhost，说明配置没有被正确加载
        if ("localhost".equals(host) || "127.0.0.1".equals(host)) {
            log.error("⚠️ 警告：Redis 配置使用了默认值 localhost，可能配置文件没有被正确读取！");
        }
    }
}