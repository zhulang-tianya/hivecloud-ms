package com.hivecloud.common.redis.service;

import java.util.function.Supplier;

/**
 * Redis 降级服务接口
 * 
 * 当 Redis 不可用时，提供降级方案（如使用本地缓存）
 * 避免 Redis 故障导致整个系统不可用
 * 
 * @author HiveCloud Team
 * @date 2026-05-13
 */
public interface RedisFallbackService {

    /**
     * 执行 Redis 操作，失败时使用降级方案
     * 
     * @param redisOperation Redis 操作
     * @param fallback 降级操作
     * @param <T> 返回类型
     * @return 操作结果
     */
    <T> T executeWithFallback(Supplier<T> redisOperation, Supplier<T> fallback);

    /**
     * 执行 Redis 操作，失败时返回默认值
     * 
     * @param redisOperation Redis 操作
     * @param defaultValue 默认值
     * @param <T> 返回类型
     * @return 操作结果或默认值
     */
    <T> T executeWithDefault(Supplier<T> redisOperation, T defaultValue);

    /**
     * 检查 Redis 是否可用（熔断器是否打开）
     * 
     * @return true-可用，false-不可用（熔断器已打开）
     */
    boolean isRedisAvailable();

    /**
     * 获取熔断器状态
     * 
     * @return 状态描述（CLOSED/OPEN/HALF_OPEN）
     */
    String getCircuitBreakerState();
}
