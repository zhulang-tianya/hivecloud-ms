package com.hivecloud.gossip.core;

import com.hivecloud.gossip.config.GossipProperties;
import com.hivecloud.gossip.model.GossipNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RedisNodeDiscovery implements NodeDiscovery {

    private final RedisTemplate<String, Object> redisTemplate;

    private final GossipProperties properties;

    private static final String NODE_KEY_PREFIX = "hivecloud:gossip:nodes:";

    public RedisNodeDiscovery(RedisTemplate<String, Object> redisTemplate, GossipProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    @Override
    public void registerNode(GossipNode node) {
        String key = NODE_KEY_PREFIX + node.getNodeId();
        redisTemplate.opsForValue().set(key, node, properties.getNodeTtlSeconds(), TimeUnit.SECONDS);
        log.info("Node registered: {}", node.getNodeId());
    }

    @Override
    public void removeNode(String nodeId) {
        String key = NODE_KEY_PREFIX + nodeId;
        redisTemplate.delete(key);
        log.info("Node removed: {}", nodeId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<GossipNode> getActiveNodes() {
        Set<String> keys = scanKeys(NODE_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<Object> values = redisTemplate.opsForValue().multiGet(keys);
        return values.stream()
                .filter(Objects::nonNull)
                .map(v -> (GossipNode) v)
                .collect(Collectors.toList());
    }

    private Set<String> scanKeys(String pattern) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keys = new HashSet<>();
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions()
                    .match(pattern)
                    .count(100)
                    .build())) {
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
                }
            }
            return keys;
        });
    }

    @Override
    public List<GossipNode> selectRandomPeers(int count) {
        List<GossipNode> activeNodes = getActiveNodes();
        if (activeNodes.size() <= count) {
            return activeNodes;
        }
        Collections.shuffle(activeNodes);
        return activeNodes.subList(0, count);
    }
}
