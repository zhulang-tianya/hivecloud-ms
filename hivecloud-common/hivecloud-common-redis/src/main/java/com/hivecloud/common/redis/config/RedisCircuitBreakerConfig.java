package com.hivecloud.common.redis.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Redis 熔断器配置（基于 Resilience4j）
 * 
 * 当 Redis 服务不可用时，自动触发熔断，避免系统雪崩
 * 支持自动恢复，当 Redis 服务恢复后自动关闭熔断器
 * 
 * 配置说明：
 * - 滑动窗口大小：10 次请求
 * - 失败率阈值：50%（失败率超过 50% 触发熔断）
 * - 慢调用阈值：3 秒（超过 3 秒视为慢调用）
 * - 熔断器打开时间：30 秒（30 秒后尝试半开状态）
 * - 半开状态测试请求数：5 次
 * 
 * @author HiveCloud Team
 * @date 2026-05-13
 */
@Configuration
public class RedisCircuitBreakerConfig {

    /**
     * 配置 Redis 熔断器
     * 
     * @return CircuitBreakerRegistry
     */
    @Bean
    public CircuitBreakerRegistry redisCircuitBreakerRegistry() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                // 滑动窗口大小（请求数量）
                .slidingWindowSize(10)
                // 滑动窗口类型：基于计数
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                // 最小调用次数（达到此次数后才开始计算失败率）
                .minimumNumberOfCalls(5)
                // 失败率阈值（超过 50% 触发熔断）
                .failureRateThreshold(50.0f)
                // 慢调用阈值（超过 3 秒视为慢调用）
                .slowCallDurationThreshold(Duration.ofSeconds(3))
                // 慢调用率阈值（慢调用超过 80% 触发熔断）
                .slowCallRateThreshold(80.0f)
                // 熔断器打开时间（30 秒后进入半开状态）
                .waitDurationInOpenState(Duration.ofSeconds(30))
                // 半开状态最大测试请求数
                .permittedNumberOfCallsInHalfOpenState(5)
                // 自动从打开状态过渡到半开状态
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                // 记录异常（包括连接超时、连接拒绝等）
                .recordExceptions(
                        java.net.ConnectException.class,
                        java.net.SocketTimeoutException.class,
                        org.springframework.data.redis.RedisConnectionFailureException.class
                )
                // 忽略异常（业务异常不触发熔断）
                .ignoreExceptions(
                        IllegalArgumentException.class,
                        java.lang.NullPointerException.class
                )
                .build();

        return CircuitBreakerRegistry.of(circuitBreakerConfig);
    }

    /**
     * 获取 Redis 熔断器实例
     * 
     * @param registry CircuitBreakerRegistry
     * @return Redis 专用的 CircuitBreaker
     */
    @Bean
    public CircuitBreaker redisCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("redisCircuitBreaker");
    }

    /**
     * 配置 Redis 超时限制器
     * 防止 Redis 操作长时间阻塞
     * 
     * @return TimeLimiterRegistry
     */
    @Bean
    public TimeLimiterRegistry redisTimeLimiterRegistry() {
        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                // 超时时间：5 秒
                .timeoutDuration(Duration.ofSeconds(5))
                // 取消未来操作
                .cancelRunningFuture(true)
                .build();

        return TimeLimiterRegistry.of(timeLimiterConfig);
    }

    /**
     * 获取 Redis 超时限制器实例
     * 
     * @param registry TimeLimiterRegistry
     * @return Redis 专用的 TimeLimiter
     */
    @Bean
    public TimeLimiter redisTimeLimiter(TimeLimiterRegistry registry) {
        return registry.timeLimiter("redisTimeLimiter");
    }
}
