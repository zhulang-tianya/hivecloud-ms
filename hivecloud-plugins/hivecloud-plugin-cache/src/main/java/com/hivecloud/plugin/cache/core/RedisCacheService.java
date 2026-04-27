package com.hivecloud.plugin.cache.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存服务实现
 * 基于 Spring RedisTemplate 实现分布式缓存操作
 * 提供字符串、对象等多种类型的缓存支持
 * 实现接口：CacheService
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see CacheService
 * @see RedisTemplate
 */
@Slf4j
@RequiredArgsConstructor
public class RedisCacheService implements CacheService {

    /**
     * Redis 模板，用于操作 Redis 存储
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 向缓存中放入数据
     *
     * @param key 缓存键
     * @param value 缓存值
     */
    @Override
    public void put(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 向缓存中放入数据，带过期时间
     *
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    @Override
    public void put(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 从缓存中获取数据
     *
     * @param key 缓存键
     * @return 缓存值，不存在时返回 null
     */
    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 从缓存中获取数据并转换为指定类型
     *
     * @param key 缓存键
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 转换后的缓存值，不存在或类型不匹配时返回 null
     * @throws ClassCastException 类型转换失败时抛出
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return (T) value;
        }
        throw new ClassCastException("Cached value is not of type: " + clazz.getName());
    }

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除缓存
     *
     * @param keys 缓存键集合
     */
    @Override
    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 检查缓存键是否存在
     *
     * @param key 缓存键
     * @return true-存在，false-不存在
     */
    @Override
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 设置缓存过期时间
     *
     * @param key 缓存键
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    @Override
    public void expire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    @Override
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    @Override
    public void clear() {
        log.warn("Redis cache clear operation is not supported for safety reasons");
    }

    public RedisTemplate<String, Object> getNativeCache() {
        return redisTemplate;
    }
}