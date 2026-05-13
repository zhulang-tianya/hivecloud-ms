package com.hivecloud.common.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Redis 配置验证器
 * 
 * 在应用启动时验证 Redis 配置是否正确，确保：
 * 1. 端口配置正确
 * 2. database 配置正确
 * 
 * 注意：localhost 检查已移除，允许开发环境使用本地 Redis
 * 
 * @author HiveCloud Team
 * @date 2026-05-13
 */
@Slf4j
@Component
public class RedisConfigValidator {

    private final Environment environment;

    public RedisConfigValidator(Environment environment) {
        this.environment = environment;
    }

    /**
     * 应用启动完成后验证 Redis 配置
     * 使用 ApplicationReadyEvent 确保配置已完全加载
     */
    @EventListener(ApplicationReadyEvent.class)
    public void validateRedisConfig() {
        log.info("========================================");
        log.info("开始验证 Redis 配置...");
        
        // 从 Environment 直接读取，确保获取最新配置
        String host = environment.getProperty("spring.redis.host", "localhost");
        int port = Integer.parseInt(environment.getProperty("spring.redis.port", "6379"));
        String password = environment.getProperty("spring.redis.password");
        int database = Integer.parseInt(environment.getProperty("spring.redis.database", "0"));
        
        log.info("从 Environment 读取的 Redis 配置：");
        log.info("  host: {}", host);
        log.info("  port: {}", port);
        log.info("  password: {}", password != null ? "******" : "null");
        log.info("  database: {}", database);
        
        // 检测 localhost（开发环境允许）
        if ("localhost".equals(host) || "127.0.0.1".equals(host)) {
            log.warn("⚠️ 检测到使用 localhost 作为 Redis 地址");
            log.warn("  如果是生产环境，请检查：");
            log.warn("    1. bootstrap.yml 中 Nacos 配置是否正确");
            log.warn("    2. Nacos 服务器地址是否可访问");
            log.warn("    3. hivecloud-common.yaml 中 Redis 配置是否正确");
        }
        
        // 验证 port
        if (port <= 0 || port > 65535) {
            log.error("❌ Redis 端口配置错误：{}", port);
            throw new IllegalStateException("Redis 端口配置错误，应在 1-65535 范围内，当前值：" + port);
        }
        
        // 验证 password（可选，允许无密码）
        if (password == null || password.trim().isEmpty()) {
            log.warn("⚠️ Redis 未配置密码，生产环境建议配置密码");
        }
        
        // 验证 database
        if (database < 0 || database > 15) {
            log.error("❌ Redis database 配置错误：{}", database);
            throw new IllegalStateException("Redis database 配置错误，应在 0-15 范围内，当前值：" + database);
        }
        
        // 验证通过
        log.info("✅ Redis 配置验证通过：");
        log.info("  host: {}", host);
        log.info("  port: {}", port);
        log.info("  password: {}", password != null ? "******" : "null");
        log.info("  database: {}", database);
        log.info("========================================");
    }
}
