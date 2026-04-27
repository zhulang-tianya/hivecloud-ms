package com.hivecloud.plugin.cache.core;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * 二级缓存服务实现（Caffeine + Redis）
 * 结合本地缓存和分布式缓存的优势，提供高性能缓存服务
 * L1: Caffeine 本地缓存（快速访问）
 * L2: Redis 分布式缓存（数据持久化）
 * 实现接口：CacheService
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see CacheService
 * @see CaffeineCacheService
 * @see RedisCacheService
 */
@Slf4j
public class TwoLevelCacheService implements CacheService {

    /**
     * Caffeine 本地缓存（L1）
     */
    private final CaffeineCacheService localCache;

    /**
     * Redis 分布式缓存（L2）
     */
    private final RedisCacheService distributedCache;

    /**
     * 缓存 Key 前缀，用于隔离不同业务缓存
     */
    private final String keyPrefix;

    /**
     * 构造函数
     *
     * @param localCache Caffeine 本地缓存服务
     * @param distributedCache Redis 分布式缓存服务
     * @param keyPrefix 缓存 Key 前缀
     */
    public TwoLevelCacheService(CaffeineCacheService localCache,
                                 RedisCacheService distributedCache,
                                 String keyPrefix) {
        this.localCache = localCache;
        this.distributedCache = distributedCache;
        this.keyPrefix = keyPrefix;
    }

    /**
     * 向缓存中放入数据
     * 同时写入 L1 和 L2 缓存
     *
     * @param key 缓存键
     * @param value 缓存值
     */
    @Override
    public void put(String key, Object value) {
        String fullKey = getFullKey(key);
        localCache.put(fullKey, value);
        distributedCache.put(fullKey, value);
    }

    /**
     * 向缓存中放入数据，带过期时间
     * 同时写入 L1 和 L2 缓存
     *
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    @Override
    public void put(String key, Object value, long timeout, TimeUnit unit) {
        String fullKey = getFullKey(key);
        localCache.put(fullKey, value, timeout, unit);
        distributedCache.put(fullKey, value, timeout, unit);
    }

    /**
     * 从缓存中获取数据
     * 优先从 L1 缓存获取，未命中则从 L2 获取并回写到 L1
     *
     * @param key 缓存键
     * @return 缓存值，不存在时返回 null
     */
    @Override
    public Object get(String key) {
        String fullKey = getFullKey(key);
        Object value = localCache.get(fullKey);
        if (value != null) {
            log.debug("Cache hit from L1 (Caffeine): {}", fullKey);
            return value;
        }
        value = distributedCache.get(fullKey);
        if (value != null) {
            log.debug("Cache hit from L2 (Redis): {}", fullKey);
            localCache.put(fullKey, value);
        } else {
            log.debug("Cache miss: {}", fullKey);
        }
        return value;
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
        Object value = get(key);
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
     * 同时删除 L1 和 L2 缓存
     *
     * @param key 缓存键
     */
    @Override
    public void delete(String key) {
        String fullKey = getFullKey(key);
        localCache.delete(fullKey);
        distributedCache.delete(fullKey);
    }

    /**
     * 批量删除缓存
     *
     * @param keys 缓存键集合
     */
    @Override
    public void delete(Collection<String> keys) {
        keys.forEach(this::delete);
    }

    /**
     * 检查缓存键是否存在
     *
     * @param key 缓存键
     * @return true-存在，false-不存在
     */
    @Override
    public boolean hasKey(String key) {
        String fullKey = getFullKey(key);
        return localCache.hasKey(fullKey) || distributedCache.hasKey(fullKey);
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
        String fullKey = getFullKey(key);
        distributedCache.expire(fullKey, timeout, unit);
    }

    /**
     * 获取缓存剩余过期时间
     *
     * @param key 缓存键
     * @return 剩余过期时间（秒），无过期时间时返回 null
     */
    @Override
    public Long getExpire(String key) {
        String fullKey = getFullKey(key);
        return distributedCache.getExpire(fullKey);
    }

    /**
     * 清空所有缓存
     * 同时清空 L1 和 L2 缓存
     */
    @Override
    public void clear() {
        localCache.clear();
        log.warn("Two-level cache L1 cleared, L2 (Redis) not cleared for safety");
    }

    private String getFullKey(String key) {
        return keyPrefix + ":" + key;
    }
}