package com.hivecloud.plugin.cache.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hivecloud.plugin.cache.core.CacheService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "hivecloud.cache.type", havingValue = "local", matchIfMissing = true)
public class LocalCacheService implements CacheService {

    private final Cache<String, Object> cache;

    public LocalCacheService() {
        this.cache = Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .recordStats()
                .build();
    }

    @Override
    public void put(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public void put(String key, Object value, long timeout, TimeUnit unit) {
        Cache<String, Object> tempCache = Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(timeout, unit)
                .build();
        tempCache.put(key, value);
    }

    @Override
    public Object get(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object value = cache.getIfPresent(key);
        if (value != null && clazz.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    @Override
    public void delete(String key) {
        cache.invalidate(key);
    }

    @Override
    public void delete(Collection<String> keys) {
        cache.invalidateAll(keys);
    }

    @Override
    public boolean hasKey(String key) {
        return cache.getIfPresent(key) != null;
    }

    @Override
    public void expire(String key, long timeout, TimeUnit unit) {
        // Caffeine does not support dynamic expiration update
    }

    @Override
    public Long getExpire(String key) {
        return -1L;
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }
}
