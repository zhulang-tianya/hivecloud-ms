package com.hivecloud.plugin.cache.impl;

import com.hivecloud.plugin.cache.core.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "hivecloud.cache.type", havingValue = "multi")
public class MultiLevelCacheService implements CacheService {

    private final LocalCacheService localCacheService;

    private final RedisCacheService redisCacheService;

    @Override
    public void put(String key, Object value) {
        localCacheService.put(key, value);
        redisCacheService.put(key, value);
    }

    @Override
    public void put(String key, Object value, long timeout, TimeUnit unit) {
        localCacheService.put(key, value, timeout, unit);
        redisCacheService.put(key, value, timeout, unit);
    }

    @Override
    public Object get(String key) {
        Object value = localCacheService.get(key);
        if (value != null) {
            return value;
        }
        value = redisCacheService.get(key);
        if (value != null) {
            localCacheService.put(key, value);
        }
        return value;
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        T value = localCacheService.get(key, clazz);
        if (value != null) {
            return value;
        }
        value = redisCacheService.get(key, clazz);
        if (value != null) {
            localCacheService.put(key, value);
        }
        return value;
    }

    @Override
    public void delete(String key) {
        localCacheService.delete(key);
        redisCacheService.delete(key);
    }

    @Override
    public void delete(Collection<String> keys) {
        localCacheService.delete(keys);
        redisCacheService.delete(keys);
    }

    @Override
    public boolean hasKey(String key) {
        return localCacheService.hasKey(key) || redisCacheService.hasKey(key);
    }

    @Override
    public void expire(String key, long timeout, TimeUnit unit) {
        redisCacheService.expire(key, timeout, unit);
    }

    @Override
    public Long getExpire(String key) {
        return redisCacheService.getExpire(key);
    }

    @Override
    public void clear() {
        localCacheService.clear();
        redisCacheService.clear();
    }
}
