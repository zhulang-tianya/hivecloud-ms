package com.hivecloud.common.redis.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("Redis set failed for key: {}", key, e);
            return false;
        }
    }

    public boolean set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            return true;
        } catch (Exception e) {
            log.error("Redis set with timeout failed for key: {}", key, e);
            return false;
        }
    }

    public Object get(String key) {
        try {
            return key != null ? redisTemplate.opsForValue().get(key) : null;
        } catch (Exception e) {
            log.error("Redis get failed for key: {}", key, e);
            return null;
        }
    }

    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Redis hasKey failed for key: {}", key, e);
            return false;
        }
    }

    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
        } catch (Exception e) {
            log.error("Redis expire failed for key: {}", key, e);
            return false;
        }
    }

    public long getExpire(String key) {
        try {
            Long ttl = redisTemplate.getExpire(key);
            return ttl != null ? ttl : -1;
        } catch (Exception e) {
            log.error("Redis getExpire failed for key: {}", key, e);
            return -1;
        }
    }

    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.error("Redis delete failed for key: {}", key, e);
            return false;
        }
    }

    public long delete(Collection<String> keys) {
        try {
            Long count = redisTemplate.delete(keys);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Redis batch delete failed", e);
            return 0;
        }
    }

    public boolean hSet(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            return true;
        } catch (Exception e) {
            log.error("Redis hSet failed for key: {}, field: {}", key, field, e);
            return false;
        }
    }

    public Object hGet(String key, String field) {
        try {
            return redisTemplate.opsForHash().get(key, field);
        } catch (Exception e) {
            log.error("Redis hGet failed for key: {}, field: {}", key, field, e);
            return null;
        }
    }

    public Map<Object, Object> hGetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("Redis hGetAll failed for key: {}", key, e);
            return Map.of();
        }
    }

    public boolean hDelete(String key, Object... fields) {
        try {
            return redisTemplate.opsForHash().delete(key, fields) > 0;
        } catch (Exception e) {
            log.error("Redis hDelete failed for key: {}", key, e);
            return false;
        }
    }

    public long lPush(String key, Object value) {
        try {
            return redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            log.error("Redis lPush failed for key: {}", key, e);
            return 0;
        }
    }

    public long rPush(String key, Object value) {
        try {
            return redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            log.error("Redis rPush failed for key: {}", key, e);
            return 0;
        }
    }

    public Object lPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            log.error("Redis lPop failed for key: {}", key, e);
            return null;
        }
    }

    public Object rPop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            log.error("Redis rPop failed for key: {}", key, e);
            return null;
        }
    }

    public List<Object> lRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("Redis lRange failed for key: {}", key, e);
            return List.of();
        }
    }

    public long sAdd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("Redis sAdd failed for key: {}", key, e);
            return 0;
        }
    }

    public Set<Object> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("Redis sMembers failed for key: {}", key, e);
            return Set.of();
        }
    }

    public boolean sIsMember(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            log.error("Redis sIsMember failed for key: {}", key, e);
            return false;
        }
    }

    public long sRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Redis sRemove failed for key: {}", key, e);
            return 0;
        }
    }

    public boolean zAdd(String key, Object value, double score) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, value, score));
        } catch (Exception e) {
            log.error("Redis zAdd failed for key: {}", key, e);
            return false;
        }
    }

    public Set<Object> zRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            log.error("Redis zRange failed for key: {}", key, e);
            return Set.of();
        }
    }

    public Double zScore(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().score(key, value);
        } catch (Exception e) {
            log.error("Redis zScore failed for key: {}", key, e);
            return null;
        }
    }

    public long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("Redis increment failed for key: {}", key, e);
            return 0;
        }
    }

    public double increment(String key, double delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("Redis increment failed for key: {}", key, e);
            return 0.0;
        }
    }
}
