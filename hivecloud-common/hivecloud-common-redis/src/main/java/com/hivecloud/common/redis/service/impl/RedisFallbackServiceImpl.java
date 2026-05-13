package com.hivecloud.common.redis.service.impl;

import com.hivecloud.common.redis.service.RedisFallbackService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

/**
 * Redis 降级服务实现
 * 
 * 使用 Resilience4j 熔断器和超时限制器实现 Redis 操作的容错
 * 当 Redis 不可用时，自动执行降级逻辑
 * 
 * @author HiveCloud Team
 * @date 2026-05-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisFallbackServiceImpl implements RedisFallbackService {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Override
    public <T> T executeWithFallback(Supplier<T> redisOperation, Supplier<T> fallback) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("redisCircuitBreaker");

        try {
            // 使用装饰器包装操作，应用熔断器
            Supplier<T> decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, redisOperation);

            return decoratedSupplier.get();

        } catch (CallNotPermittedException e) {
            // 熔断器打开，执行降级逻辑
            log.warn("熔断器已打开，执行降级逻辑");
            return fallback.get();
        } catch (Exception e) {
            // 其他异常，也执行降级逻辑
            log.error("Redis 操作失败，执行降级逻辑：{}", e.getMessage());
            return fallback.get();
        }
    }

    @Override
    public <T> T executeWithDefault(Supplier<T> redisOperation, T defaultValue) {
        return executeWithFallback(redisOperation, () -> {
            log.debug("返回默认值：{}", defaultValue);
            return defaultValue;
        });
    }

    @Override
    public boolean isRedisAvailable() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("redisCircuitBreaker");
        return circuitBreaker.getState() == CircuitBreaker.State.CLOSED ||
               circuitBreaker.getState() == CircuitBreaker.State.HALF_OPEN;
    }

    @Override
    public String getCircuitBreakerState() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("redisCircuitBreaker");
        return circuitBreaker.getState().name();
    }
}
