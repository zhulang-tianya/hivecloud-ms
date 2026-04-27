package com.hivecloud.heartbeat.core;

import com.hivecloud.heartbeat.model.HeartbeatInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 基于 Redis 的心跳存储实现
 * 负责将服务实例心跳信息存储到 Redis，设置 TTL 自动过期
 * 实现接口：HeartbeatStore
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see HeartbeatStore
 * @see HeartbeatInfo
 */
@Component
@RequiredArgsConstructor
public class RedisHeartbeatStore implements HeartbeatStore {

    /**
     * Redis 模板，用于操作 Redis 存储
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 心跳 Key 前缀
     * 格式：hivecloud:heartbeat:{instanceId}
     */
    private static final String HEARTBEAT_KEY_PREFIX = "hivecloud:heartbeat:";

    /**
     * 心跳过期时间（秒），默认 30 秒
     * 超过 30 秒未更新心跳的实例将被视为故障
     */
    private static final long HEARTBEAT_TTL_SECONDS = 30;

    /**
     * 保存心跳信息
     * 将心跳信息存储到 Redis，并设置 30 秒过期时间
     *
     * @param heartbeatInfo 心跳信息对象
     */
    @Override
    public void saveHeartbeat(HeartbeatInfo heartbeatInfo) {
        String key = HEARTBEAT_KEY_PREFIX + heartbeatInfo.getInstanceId();
        heartbeatInfo.setLastHeartbeat(LocalDateTime.now());
        redisTemplate.opsForValue().set(key, heartbeatInfo, HEARTBEAT_TTL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 获取心跳信息
     *
     * @param instanceId 服务实例 ID
     * @return 心跳信息对象，不存在时返回 null
     */
    @Override
    public HeartbeatInfo getHeartbeat(String instanceId) {
        String key = HEARTBEAT_KEY_PREFIX + instanceId;
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof HeartbeatInfo) {
            return (HeartbeatInfo) value;
        }
        return null;
    }

    /**
     * 删除心跳信息
     *
     * @param instanceId 服务实例 ID
     */
    @Override
    public void removeHeartbeat(String instanceId) {
        String key = HEARTBEAT_KEY_PREFIX + instanceId;
        redisTemplate.delete(key);
    }

    /**
     * 检查心跳信息是否存在
     *
     * @param instanceId 服务实例 ID
     * @return true-存在，false-不存在
     */
    @Override
    public boolean exists(String instanceId) {
        String key = HEARTBEAT_KEY_PREFIX + instanceId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
