package com.hivecloud.gossip.core;

import com.hivecloud.gossip.config.GossipProperties;
import com.hivecloud.gossip.model.GossipMessage;
import com.hivecloud.gossip.model.GossipNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public class GossipSyncEngine implements GossipProtocol {

    private final NodeDiscovery nodeDiscovery;

    private final GossipProperties properties;

    private final RestTemplate restTemplate;

    private ThreadPoolTaskScheduler taskScheduler;

    private ScheduledFuture<?> scheduledFuture;

    private GossipNode localNode;

    public GossipSyncEngine(NodeDiscovery nodeDiscovery, GossipProperties properties, RestTemplate restTemplate) {
        this.nodeDiscovery = nodeDiscovery;
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendMessage(GossipMessage message) {
        selectPeersAndSpread(message);
    }

    @Override
    public void receiveMessage(GossipMessage message) {
        log.debug("Received gossip message from {}: {}", message.getSourceNodeId(), message.getMessageType());
    }

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

    private void syncCycle() {
        GossipMessage message = new GossipMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setSourceNodeId(localNode.getNodeId());
        message.setMessageType("SYNC");
        message.setTimestamp(System.currentTimeMillis());
        message.setVersion(localNode.getVersion());
        sendMessage(message);
    }

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
