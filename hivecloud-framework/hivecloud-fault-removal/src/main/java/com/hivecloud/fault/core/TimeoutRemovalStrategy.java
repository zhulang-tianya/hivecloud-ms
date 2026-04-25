package com.hivecloud.fault.core;

import com.hivecloud.fault.config.FaultRemovalProperties;
import com.hivecloud.fault.model.FaultRemovalRecord;
import com.hivecloud.heartbeat.core.HeartbeatStore;
import com.hivecloud.heartbeat.model.HeartbeatInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class TimeoutRemovalStrategy extends BaseFaultRemovalStrategy {

    private final HeartbeatStore heartbeatStore;

    private final RedisTemplate<String, Object> redisTemplate;

    private final FaultRemovalProperties properties;

    private static final String HEARTBEAT_KEY_PREFIX = "hivecloud:heartbeat:";

    public TimeoutRemovalStrategy(HeartbeatStore heartbeatStore,
                                  RedisTemplate<String, Object> redisTemplate,
                                  FaultRemovalProperties properties) {
        this.heartbeatStore = heartbeatStore;
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    @Override
    public String getType() {
        return "TIMEOUT";
    }

    @Override
    public List<FaultRemovalRecord> detectAndRemove() {
        List<FaultRemovalRecord> removed = new ArrayList<>();
        long timeoutMillis = properties.getTimeoutMillis();
        LocalDateTime threshold = LocalDateTime.now().minusNanos(timeoutMillis * 1_000_000);

        Set<String> keys = scanHeartbeatKeys();
        for (String key : keys) {
            String instanceId = key.substring(HEARTBEAT_KEY_PREFIX.length());
            HeartbeatInfo heartbeat = heartbeatStore.getHeartbeat(instanceId);
            if (heartbeat != null && heartbeat.getLastHeartbeat() != null
                    && heartbeat.getLastHeartbeat().isBefore(threshold)) {
                FaultRemovalRecord record = createRecord(
                        instanceId,
                        heartbeat.getServiceId(),
                        "Heartbeat timeout after " + timeoutMillis + "ms",
                        "TIMEOUT"
                );
                record.setLastHeartbeat(heartbeat.getLastHeartbeat());
                heartbeatStore.removeHeartbeat(instanceId);
                recordRemoval(record);
                removed.add(record);
                log.warn("Instance {} removed due to timeout, last heartbeat: {}",
                        instanceId, heartbeat.getLastHeartbeat());
            }
        }
        return removed;
    }

    @SuppressWarnings("unchecked")
    private Set<String> scanHeartbeatKeys() {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keys = new java.util.HashSet<>();
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions()
                    .match(HEARTBEAT_KEY_PREFIX + "*")
                    .count(100)
                    .build())) {
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next()));
                }
            }
            return keys;
        });
    }
}
