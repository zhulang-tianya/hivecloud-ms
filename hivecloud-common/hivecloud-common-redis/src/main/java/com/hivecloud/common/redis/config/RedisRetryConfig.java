package com.hivecloud.common.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * Redis 重试配置
 * 
 * 配置 Spring Retry 模板，用于 Redis 操作的自动重试
 * 当 Redis 连接失败或超时时，自动进行指数退避重试
 * 
 * 重试策略：
 * - 最大重试次数：3 次
 * - 初始间隔：1 秒
 * - 最大间隔：10 秒
 * - 乘数：2.0（每次重试间隔翻倍）
 * 
 * @author HiveCloud Team
 * @date 2026-05-13
 */
@Slf4j
@Configuration
@EnableRetry
public class RedisRetryConfig {

    /**
     * 配置 Redis 重试模板
     * 使用指数退避策略，避免频繁重试加重服务器压力
     * 
     * @return 配置好的 RetryTemplate
     */
    @Bean
    public RetryTemplate redisRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        
        // 配置重试策略：最多重试 3 次
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);
        
        // 配置退避策略：指数退避
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000); // 初始间隔 1 秒
        backOffPolicy.setMaxInterval(10000);    // 最大间隔 10 秒
        backOffPolicy.setMultiplier(2.0);       // 乘数因子 2.0
        retryTemplate.setBackOffPolicy(backOffPolicy);
        
        return retryTemplate;
    }
}
