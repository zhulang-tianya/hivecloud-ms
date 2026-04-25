package com.hivecloud.plugin.cache.core;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TwoLevelCacheService implements CacheService {

    private final CaffeineCacheService localCache;
    private final RedisCacheService distributedCache;
    private final String keyPrefix;

    public TwoLevelCacheService(CaffeineCacheService localCache,
                                 RedisCacheService distributedCache,
                                 String keyPrefix) {
        this.localCache = localCache;
        this.distributedCache = distributedCache;
        this.keyPrefix = keyPrefix;
    }

    @Override
    public void put(String key, Object value) {
        String fullKey = getFullKey(key);
        localCache.put(fullKey, value);
        distributedCache.put(fullKey, value);
    }

    @Override
    public void put(String key, Object value, long timeout, TimeUnit unit) {
        String fullKey = getFullKey(key);
        localCache.put(fullKey, value, timeout, unit);
        distributedCache.put(fullKey, value, timeout, unit);
    }

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

    @Override
    public void delete(String key) {
        String fullKey = getFullKey(key);
        localCache.delete(fullKey);
        distributedCache.delete(fullKey);
    }

    @Override
    public void delete(Collection<String> keys) {
        keys.forEach(this::delete);
    }

    @Override
    public boolean hasKey(String key) {
        String fullKey = getFullKey(key);
        return localCache.hasKey(fullKey) || distributedCache.hasKey(fullKey);
    }

    @Override
    public void expire(String key, long timeout, TimeUnit unit) {
        String fullKey = getFullKey(key);
        distributedCache.expire(fullKey, timeout, unit);
    }

    @Override
    public Long getExpire(String key) {
        String fullKey = getFullKey(key);
        return distributedCache.getExpire(fullKey);
    }

    @Override
    public void clear() {
        localCache.clear();
        log.warn("Two-level cache L1 cleared, L2 (Redis) not cleared for safety");
    }

    private String getFullKey(String key) {
        return keyPrefix + ":" + key;
    }
}