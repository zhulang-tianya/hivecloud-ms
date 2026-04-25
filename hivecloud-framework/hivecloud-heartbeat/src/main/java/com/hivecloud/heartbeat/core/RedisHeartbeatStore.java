package com.hivecloud.heartbeat.core;

import com.hivecloud.heartbeat.model.HeartbeatInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisHeartbeatStore implements HeartbeatStore {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String HEARTBEAT_KEY_PREFIX = "hivecloud:heartbeat:";

    private static final long HEARTBEAT_TTL_SECONDS = 30;

    @Override
    public void saveHeartbeat(HeartbeatInfo heartbeatInfo) {
        String key = HEARTBEAT_KEY_PREFIX + heartbeatInfo.getInstanceId();
        heartbeatInfo.setLastHeartbeat(LocalDateTime.now());
        redisTemplate.opsForValue().set(key, heartbeatInfo, HEARTBEAT_TTL_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public HeartbeatInfo getHeartbeat(String instanceId) {
        String key = HEARTBEAT_KEY_PREFIX + instanceId;
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof HeartbeatInfo) {
            return (HeartbeatInfo) value;
        }
        return null;
    }

    @Override
    public void removeHeartbeat(String instanceId) {
        String key = HEARTBEAT_KEY_PREFIX + instanceId;
        redisTemplate.delete(key);
    }

    @Override
    public boolean exists(String instanceId) {
        String key = HEARTBEAT_KEY_PREFIX + instanceId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
