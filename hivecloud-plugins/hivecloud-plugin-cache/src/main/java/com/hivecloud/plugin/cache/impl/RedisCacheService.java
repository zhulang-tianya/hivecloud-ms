package com.hivecloud.plugin.cache.impl;

import com.hivecloud.common.redis.util.RedisKeyBuilder;
import com.hivecloud.plugin.cache.core.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 基于 Redis 的缓存服务实现
 * 使用统一的 Key 命名规范：hivecloud:cache:{module}:{key}
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "hivecloud.cache.type", havingValue = "redis")
public class RedisCacheService implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 默认模块名称，用于未指定模块的场景
     */
    private static final String DEFAULT_MODULE = "common";

    /**
     * 构建带模块前缀的缓存 Key
     * 格式：hivecloud:cache:{module}:{key}
     * 
     * @param key 原始键
     * @return 带模块前缀的完整键
     */
    private String buildCacheKey(String key) {
        return buildCacheKey(DEFAULT_MODULE, key);
    }
    
    /**
     * 构建带模块前缀的缓存 Key
     * 格式：hivecloud:cache:{module}:{key}
     * 
     * @param module 模块名称
     * @param key 原始键
     * @return 带模块前缀的完整键
     */
    private String buildCacheKey(String module, String key) {
        return RedisKeyBuilder.cache(module, key);
    }

    @Override
    public void put(String key, Object value) {
        String cacheKey = buildCacheKey(key);
        log.debug("缓存写入：key={}, value={}", cacheKey, value);
        redisTemplate.opsForValue().set(cacheKey, value);
    }

    @Override
    public void put(String key, Object value, long timeout, TimeUnit unit) {
        String cacheKey = buildCacheKey(key);
        log.debug("缓存写入（带过期）：key={}, value={}, timeout={} {}", cacheKey, value, timeout, unit);
        redisTemplate.opsForValue().set(cacheKey, value, timeout, unit);
    }

    @Override
    public Object get(String key) {
        String cacheKey = buildCacheKey(key);
        log.debug("缓存读取：key={}", cacheKey);
        return redisTemplate.opsForValue().get(cacheKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        String cacheKey = buildCacheKey(key);
        log.debug("缓存读取（带类型）：key={}, clazz={}", cacheKey, clazz.getSimpleName());
        Object value = redisTemplate.opsForValue().get(cacheKey);
        if (value != null && clazz.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    @Override
    public void delete(String key) {
        String cacheKey = buildCacheKey(key);
        log.debug("缓存删除：key={}", cacheKey);
        redisTemplate.delete(cacheKey);
    }

    @Override
    public void delete(Collection<String> keys) {
        Collection<String> cacheKeys = keys.stream()
            .map(this::buildCacheKey)
            .collect(java.util.stream.Collectors.toList());
        log.debug("批量缓存删除：keys={}", cacheKeys);
        redisTemplate.delete(cacheKeys);
    }

    @Override
    public boolean hasKey(String key) {
        String cacheKey = buildCacheKey(key);
        boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey));
        log.debug("缓存检查：key={}, exists={}", cacheKey, exists);
        return exists;
    }

    @Override
    public void expire(String key, long timeout, TimeUnit unit) {
        String cacheKey = buildCacheKey(key);
        log.debug("设置缓存过期：key={}, timeout={} {}", cacheKey, timeout, unit);
        redisTemplate.expire(cacheKey, timeout, unit);
    }

    @Override
    public Long getExpire(String key) {
        String cacheKey = buildCacheKey(key);
        Long expire = redisTemplate.getExpire(cacheKey, TimeUnit.SECONDS);
        log.debug("获取缓存过期时间：key={}, expire={}s", cacheKey, expire);
        return expire;
    }

    @Override
    public void clear() {
        // Not implemented for safety
    }
}
