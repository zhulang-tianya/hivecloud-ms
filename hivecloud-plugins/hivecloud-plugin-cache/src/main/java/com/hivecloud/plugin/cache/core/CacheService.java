package com.hivecloud.plugin.cache.core;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface CacheService {

    void put(String key, Object value);

    void put(String key, Object value, long timeout, TimeUnit unit);

    Object get(String key);

    <T> T get(String key, Class<T> clazz);

    void delete(String key);

    void delete(Collection<String> keys);

    boolean hasKey(String key);

    void expire(String key, long timeout, TimeUnit unit);

    Long getExpire(String key);

    void clear();
}