package com.hivecloud.common.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Redis 连接工厂配置
 * 
 * 注意：Spring Boot 3.x 会自动配置 RedisConnectionFactory，
 * 包括从配置中心（如 Nacos）读取配置。
 * 
 * 本类不再手动创建 LettuceConnectionFactory，避免覆盖自动配置的行为。
 * 如果需要自定义配置，请使用 @ConditionalOnMissingBean 或在启动模块中配置。
 */
@Configuration
@Slf4j
public class RedisConnectionFactoryConfig {
    
    private final Environment environment;
    
    public RedisConnectionFactoryConfig(Environment environment) {
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
        log.info("配置 RedisConnectionFactory:");
        log.info("  host: {}", host);
        log.info("  port: {}", port);
        log.info("  password: {}", password != null ? "******" : "null");
        log.info("  database: {}", database);
        log.info("========================================");
        
        // 如果 host 是 localhost，发出警告
        if ("localhost".equals(host) || "127.0.0.1".equals(host)) {
            log.warn("⚠️ 警告：Redis 配置使用了默认值 localhost，可能配置文件没有被正确读取！");
        }
    }
}