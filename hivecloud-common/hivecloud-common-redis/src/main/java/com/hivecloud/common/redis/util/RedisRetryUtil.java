package com.hivecloud.common.redis.util;

import com.hivecloud.common.redis.config.RedisRetryConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Redis 重试工具类
 * 
 * 提供统一的 Redis 操作重试方法，确保在网络抖动等临时故障时自动重试
 * 使用 Spring Retry 实现，支持指数退避策略
 * 
 * 使用示例：
 * <pre>
 * // 简单重试
 * String value = RedisRetryUtil.execute(() -> redisTemplate.opsForValue().get(key));
 * 
 * // 带默认值的重试
 * String value = RedisRetryUtil.executeWithDefault(
 *     () -> redisTemplate.opsForValue().get(key),
 *     "default-value"
 * );
 * 
 * // 带异常处理的重试
 * try {
 *     String value = RedisRetryUtil.executeWithHandler(
 *         () -> redisTemplate.opsForValue().get(key),
 *         e -> log.error("获取 Redis 失败", e)
 *     );
 * } catch (Exception e) {
 *     // 处理最终失败
 * }
 * </pre>
 * 
 * @author HiveCloud Team
 * @date 2026-05-13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisRetryUtil {

    private final RetryTemplate retryTemplate;

    /**
     * 执行 Redis 操作，自动重试
     * 
     * @param operation Redis 操作
     * @param <T> 返回类型
     * @return 操作结果
     * @throws Throwable 如果重试耗尽后仍然失败
     */
    public <T> T execute(Supplier<T> operation) throws Throwable {
        return retryTemplate.execute(context -> operation.get());
    }

    /**
     * 执行 Redis 操作，失败时返回默认值
     * 
     * @param operation Redis 操作
     * @param defaultValue 默认值
     * @param <T> 返回类型
     * @return 操作结果或默认值
     */
    public <T> T executeWithDefault(Supplier<T> operation, T defaultValue) {
        try {
            return retryTemplate.execute(context -> {
                T result = operation.get();
                if (result == null) {
                    log.debug("Redis 操作返回 null");
                }
                return result;
            });
        } catch (Throwable e) {
            log.warn("Redis 操作失败，返回默认值：{}", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * 执行 Redis 操作，带异常处理器
     * 
     * @param operation Redis 操作
     * @param errorHandler 异常处理器
     * @param <T> 返回类型
     * @return 操作结果
     * @throws Throwable 如果重试耗尽后仍然失败
     */
    public <T> T executeWithHandler(Supplier<T> operation, 
                                   java.util.function.Consumer<Throwable> errorHandler) throws Throwable {
        try {
            return retryTemplate.execute(context -> operation.get());
        } catch (Throwable e) {
            errorHandler.accept(e);
            throw e;
        }
    }

    /**
     * 执行无返回值的 Redis 操作
     * 
     * @param operation Redis 操作
     * @throws Throwable 如果重试耗尽后仍然失败
     */
    public void execute(Runnable operation) throws Throwable {
        retryTemplate.execute(context -> {
            operation.run();
            return null;
        });
    }
}
