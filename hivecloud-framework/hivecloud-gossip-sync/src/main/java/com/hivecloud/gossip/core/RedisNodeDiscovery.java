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

/**
 * 基于 Redis 的节点发现实现
 * 负责管理 Gossip 集群中的节点注册、注销和查询
 * 使用 Redis 存储节点元数据，支持 TTL 自动过期
 * 实现接口：NodeDiscovery
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see NodeDiscovery
 * @see GossipNode
 * @see GossipProperties
 */
@Slf4j
@Component
public class RedisNodeDiscovery implements NodeDiscovery {

    /**
     * Redis 模板，用于操作 Redis 存储
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Gossip 协议配置属性
     */
    private final GossipProperties properties;

    /**
     * 节点 Key 前缀
     * 格式：hivecloud:gossip:nodes:{nodeId}
     */
    private static final String NODE_KEY_PREFIX = "hivecloud:gossip:nodes:";

    /**
     * 构造函数
     *
     * @param redisTemplate Redis 模板
     * @param properties Gossip 协议配置
     */
    public RedisNodeDiscovery(RedisTemplate<String, Object> redisTemplate, GossipProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    /**
     * 注册节点
     * 将节点信息存储到 Redis，设置 TTL 自动过期
     *
     * @param node Gossip 节点对象
     */
    @Override
    public void registerNode(GossipNode node) {
        String key = NODE_KEY_PREFIX + node.getNodeId();
        redisTemplate.opsForValue().set(key, node, properties.getNodeTtlSeconds(), TimeUnit.SECONDS);
        log.info("Node registered: {}", node.getNodeId());
    }

    /**
     * 删除节点
     * 从 Redis 中删除节点信息
     *
     * @param nodeId 节点 ID
     */
    @Override
    public void removeNode(String nodeId) {
        String key = NODE_KEY_PREFIX + nodeId;
        redisTemplate.delete(key);
        log.info("Node removed: {}", nodeId);
    }

    /**
     * 获取所有活跃节点列表
     * 扫描 Redis 中所有未过期的节点
     *
     * @return 活跃节点列表，无节点时返回空列表
     */
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

    /**
     * 扫描匹配指定模式的 Redis Key
     * 使用 SCAN 命令避免阻塞 Redis
     *
     * @param pattern Key 匹配模式
     * @return 匹配的 Key 集合
     */
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

    /**
     * 随机选择指定数量的节点
     * 用于 Gossip 消息传播时选择邻居节点
     *
     * @param count 需要选择的节点数量
     * @return 随机选择的节点列表
     */
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
