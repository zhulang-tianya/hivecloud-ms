package com.hivecloud.plugin.cache.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine 本地缓存服务实现
 * 基于 Caffeine 高性能本地缓存库
 * 支持最大容量限制和写入后自动过期
 * 实现接口：CacheService
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see CacheService
 * @see Cache
 */
@Slf4j
public class CaffeineCacheService implements CacheService {

    /**
     * Caffeine 缓存实例
     */
    private final Cache<String, Object> cache;

    /**
     * 构造函数
     * 创建带有最大容量和过期时间的缓存
     *
     * @param maxSize 最大缓存条目数
     * @param expireAfterWrite 写入后过期时间
     * @param timeUnit 时间单位
     */
    public CaffeineCacheService(long maxSize, long expireAfterWrite, TimeUnit timeUnit) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireAfterWrite, timeUnit)
                .recordStats()
                .build();
    }

    /**
     * 向缓存中放入数据
     *
     * @param key 缓存键
     * @param value 缓存值
     */
    @Override
    public void put(String key, Object value) {
        cache.put(key, value);
    }

    /**
     * 向缓存中放入数据，带过期时间
     * Caffeine 使用构造函数中配置的过期时间
     *
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间（未使用）
     * @param unit 时间单位（未使用）
     */
    @Override
    public void put(String key, Object value, long timeout, TimeUnit unit) {
        cache.put(key, value);
    }

    /**
     * 从缓存中获取数据
     *
     * @param key 缓存键
     * @return 缓存值，不存在时返回 null
     */
    @Override
    public Object get(String key) {
        return cache.getIfPresent(key);
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
        Object value = cache.getIfPresent(key);
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
        cache.invalidate(key);
    }

    /**
     * 批量删除缓存
     *
     * @param keys 缓存键集合
     */
    @Override
    public void delete(Collection<String> keys) {
        cache.invalidateAll(keys);
    }

    @Override
    public boolean hasKey(String key) {
        return cache.asMap().containsKey(key);
    }

    @Override
    public void expire(String key, long timeout, TimeUnit unit) {
        log.warn("Caffeine cache does not support dynamic expiration update");
    }

    @Override
    public Long getExpire(String key) {
        return -1L;
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }

    public Cache<String, Object> getNativeCache() {
        return cache;
    }
}