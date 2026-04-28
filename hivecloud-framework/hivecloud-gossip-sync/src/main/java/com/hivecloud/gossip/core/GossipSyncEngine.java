package com.hivecloud.gossip.core;

import com.hivecloud.gossip.config.GossipProperties;
import com.hivecloud.gossip.model.GossipMessage;
import com.hivecloud.gossip.model.GossipNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

/**
 * Gossip 协议同步引擎实现
 * 基于 Gossip 协议实现节点间的元数据同步
 * 通过随机选择邻居节点传播消息，最终达到集群状态一致
 * 实现接口：GossipProtocol
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see GossipProtocol
 * @see NodeDiscovery
 * @see GossipMessage
 * @see GossipNode
 */
@Slf4j
public class GossipSyncEngine implements GossipProtocol {

    /**
     * 节点发现组件，用于管理集群节点列表
     */
    private final NodeDiscovery nodeDiscovery;

    /**
     * Gossip 协议配置属性
     */
    private final GossipProperties properties;

    /**
     * REST 模板，用于节点间 HTTP 通信
     */
    private final RestTemplate restTemplate;

    /**
     * 定时任务调度器，负责周期性同步任务调度
     */
    private ThreadPoolTaskScheduler taskScheduler;

    /**
     * 定时任务 Future 对象，用于任务取消
     */
    private ScheduledFuture<?> scheduledFuture;

    /**
     * 本地节点信息，包含节点 ID、IP、端口等元数据
     */
    private GossipNode localNode;

    /**
     * 构造函数
     *
     * @param nodeDiscovery 节点发现组件
     * @param properties Gossip 协议配置
     * @param restTemplate REST 通信模板
     */
    public GossipSyncEngine(NodeDiscovery nodeDiscovery, GossipProperties properties, RestTemplate restTemplate) {
        this.nodeDiscovery = nodeDiscovery;
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    /**
     * 初始化本地节点信息
     * 在 Spring 容器启动后自动执行
     */
    @PostConstruct
    public void init() {
        String ip = properties.getIp() != null ? properties.getIp() : "127.0.0.1";
        Integer port = properties.getPort() != null ? properties.getPort() : 8080;
        initLocalNode(ip, port);
        log.info("Local node initialized: {}", localNode.getNodeId());
    }

    /**
     * 发送 Gossip 消息
     * 选择邻居节点并传播消息
     *
     * @param message Gossip 消息对象
     */
    @Override
    public void sendMessage(GossipMessage message) {
        selectPeersAndSpread(message);
    }

    /**
     * 接收 Gossip 消息
     * 处理从其他节点收到的 Gossip 消息
     *
     * @param message 接收到的 Gossip 消息
     */
    @Override
    public void receiveMessage(GossipMessage message) {
        log.debug("Received gossip message from {}: {}", message.getSourceNodeId(), message.getMessageType());
    }

    /**
     * 选择邻居节点并传播消息
     * 随机选择 fanout 个邻居节点，通过 HTTP 发送消息
     *
     * @param message Gossip 消息对象
     */
    @Override
    public void selectPeersAndSpread(GossipMessage message) {
        List<GossipNode> peers = nodeDiscovery.selectRandomPeers(properties.getFanout());
        for (GossipNode peer : peers) {
            if (peer.getNodeId().equals(localNode.getNodeId())) {
                continue;
            }
            try {
                String url = "http://" + peer.getIp() + ":" + peer.getPort() + "/gossip/receive";
                restTemplate.postForObject(url, message, Void.class);
                log.debug("Sent gossip message to peer: {}", peer.getNodeId());
            } catch (Exception e) {
                log.warn("Failed to send gossip message to peer {}: {}", peer.getNodeId(), e.getMessage());
            }
        }
    }

    /**
     * 启动 Gossip 同步引擎
     * 注册本地节点，启动周期性同步任务
     *
     * @throws IllegalStateException 当本地节点未初始化时抛出
     */
    public void start() {
        if (localNode == null) {
            throw new IllegalStateException("Local node not initialized");
        }
        nodeDiscovery.registerNode(localNode);

        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("gossip-sync-");
        taskScheduler.initialize();

        scheduledFuture = taskScheduler.scheduleAtFixedRate(
                this::syncCycle,
                Instant.now(),
                Duration.ofMillis(properties.getSyncIntervalMillis())
        );
        log.info("Gossip sync engine started for node: {}", localNode.getNodeId());
    }

    /**
     * 停止 Gossip 同步引擎
     * 取消定时任务，关闭线程池，注销本地节点
     */
    public void stop() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        if (taskScheduler != null) {
            taskScheduler.shutdown();
        }
        nodeDiscovery.removeNode(localNode.getNodeId());
        log.info("Gossip sync engine stopped");
    }

    /**
     * 同步周期任务
     * 创建同步消息并发送到邻居节点
     */
    private void syncCycle() {
        GossipMessage message = new GossipMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setSourceNodeId(localNode.getNodeId());
        message.setMessageType("SYNC");
        message.setTimestamp(System.currentTimeMillis());
        message.setVersion(localNode.getVersion());
        sendMessage(message);
    }

    /**
     * 初始化本地节点信息
     * 设置节点 ID、IP、端口、状态等元数据
     *
     * @param ip 节点 IP 地址
     * @param port 节点端口号
     */
    public void initLocalNode(String ip, Integer port) {
        this.localNode = new GossipNode();
        this.localNode.setNodeId(UUID.randomUUID().toString());
        this.localNode.setIp(ip);
        this.localNode.setPort(port);
        this.localNode.setStatus("ACTIVE");
        this.localNode.setLastSyncTime(System.currentTimeMillis());
        this.localNode.setVersion(1);
    }
}
