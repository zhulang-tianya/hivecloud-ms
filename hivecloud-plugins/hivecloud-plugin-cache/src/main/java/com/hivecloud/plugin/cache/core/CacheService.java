package com.hivecloud.plugin.cache.core;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务接口
 * 定义缓存操作的基本方法
 * 支持本地缓存、分布式缓存和二级缓存实现
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see CaffeineCacheService
 * @see RedisCacheService
 * @see TwoLevelCacheService
 */
public interface CacheService {

    /**
     * 向缓存中放入数据
     *
     * @param key 缓存键
     * @param value 缓存值
     */
    void put(String key, Object value);

    /**
     * 向缓存中放入数据，带过期时间
     *
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    void put(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 从缓存中获取数据
     *
     * @param key 缓存键
     * @return 缓存值，不存在时返回 null
     */
    Object get(String key);

    /**
     * 从缓存中获取数据并转换为指定类型
     *
     * @param key 缓存键
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 转换后的缓存值
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    void delete(String key);

    /**
     * 批量删除缓存
     *
     * @param keys 缓存键集合
     */
    void delete(Collection<String> keys);

    /**
     * 检查缓存键是否存在
     *
     * @param key 缓存键
     * @return true-存在，false-不存在
     */
    boolean hasKey(String key);

    /**
     * 设置缓存过期时间
     *
     * @param key 缓存键
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    void expire(String key, long timeout, TimeUnit unit);

    /**
     * 获取缓存过期时间
     *
     * @param key 缓存键
     * @return 过期时间（单位：秒），-1 表示永不过期
     */
    Long getExpire(String key);

    /**
     * 清空所有缓存
     */
    void clear();
}