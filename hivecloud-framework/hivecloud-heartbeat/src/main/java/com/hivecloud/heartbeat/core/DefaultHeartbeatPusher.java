package com.hivecloud.heartbeat.core;

import com.hivecloud.heartbeat.model.HeartbeatInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultHeartbeatPusher implements HeartbeatPusher {

    private final HeartbeatStore heartbeatStore;

    private ThreadPoolTaskScheduler taskScheduler;

    private ScheduledFuture<?> scheduledFuture;

    private HeartbeatInfo localHeartbeatInfo;

    private long intervalMillis = 1000;

    @Override
    public void start() {
        if (localHeartbeatInfo == null) {
            throw new IllegalStateException("HeartbeatInfo not initialized");
        }
        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("heartbeat-pusher-");
        taskScheduler.initialize();

        scheduledFuture = taskScheduler.scheduleAtFixedRate(
                () -> pushHeartbeat(localHeartbeatInfo),
                Instant.now(),
                Duration.ofMillis(intervalMillis)
        );
        log.info("Heartbeat pusher started with interval {}ms", intervalMillis);
    }

    @Override
    public void stop() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        if (taskScheduler != null) {
            taskScheduler.shutdown();
        }
        log.info("Heartbeat pusher stopped");
    }

    @Override
    public void pushHeartbeat(HeartbeatInfo heartbeatInfo) {
        try {
            heartbeatStore.saveHeartbeat(heartbeatInfo);
            log.debug("Heartbeat pushed for instance: {}", heartbeatInfo.getInstanceId());
        } catch (Exception e) {
            log.error("Failed to push heartbeat for instance: {}", heartbeatInfo.getInstanceId(), e);
        }
    }

    public void initHeartbeatInfo(String serviceId, String ip, Integer port, String metadata) {
        this.localHeartbeatInfo = new HeartbeatInfo();
        this.localHeartbeatInfo.setServiceId(serviceId);
        this.localHeartbeatInfo.setInstanceId(UUID.randomUUID().toString());
        this.localHeartbeatInfo.setIp(ip);
        this.localHeartbeatInfo.setPort(port);
        this.localHeartbeatInfo.setMetadata(metadata);
        this.localHeartbeatInfo.setStatus(1);
        this.localHeartbeatInfo.setHeartbeatInterval(intervalMillis);
    }

    public void setIntervalMillis(long intervalMillis) {
        this.intervalMillis = intervalMillis;
    }
}
