package com.hivecloud.plugin.cache.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CaffeineCacheService implements CacheService {

    private final Cache<String, Object> cache;

    public CaffeineCacheService(long maxSize, long expireAfterWrite, TimeUnit timeUnit) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireAfterWrite, timeUnit)
                .recordStats()
                .build();
    }

    @Override
    public void put(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public void put(String key, Object value, long timeout, TimeUnit unit) {
        cache.put(key, value);
    }

    @Override
    public Object get(String key) {
        return cache.getIfPresent(key);
    }

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