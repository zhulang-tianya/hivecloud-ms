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

/**
 * 默认心跳推送器实现
 * 负责每秒推送心跳时间戳到 Redis 存储，维持服务实例在线状态
 * 实现接口：HeartbeatPusher
 * 依赖组件：HeartbeatStore（心跳存储）
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see HeartbeatPusher
 * @see HeartbeatStore
 * @see HeartbeatInfo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultHeartbeatPusher implements HeartbeatPusher {

    /**
     * 心跳存储组件，用于持久化心跳数据到 Redis
     */
    private final HeartbeatStore heartbeatStore;

    /**
     * 定时任务调度器，负责心跳推送任务的调度执行
     */
    private ThreadPoolTaskScheduler taskScheduler;

    /**
     * 定时任务 Future 对象，用于任务取消和状态管理
     */
    private ScheduledFuture<?> scheduledFuture;

    /**
     * 本地服务实例心跳信息，包含服务 ID、IP、端口等元数据
     */
    private HeartbeatInfo localHeartbeatInfo;

    /**
     * 心跳推送间隔时间（毫秒），默认 1000ms（1 秒）
     */
    private long intervalMillis = 1000;

    /**
     * 启动心跳推送任务
     * 初始化线程池，创建定时任务，每秒推送一次心跳
     *
     * @throws IllegalStateException 当 localHeartbeatInfo 未初始化时抛出
     */
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

    /**
     * 停止心跳推送任务
     * 取消定时任务，关闭线程池，释放资源
     */
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

    /**
     * 推送心跳到存储组件
     * 将当前服务实例的心跳信息保存到 Redis，维持服务在线状态
     *
     * @param heartbeatInfo 心跳信息，包含服务实例元数据
     */
    @Override
    public void pushHeartbeat(HeartbeatInfo heartbeatInfo) {
        try {
            heartbeatStore.saveHeartbeat(heartbeatInfo);
            log.debug("Heartbeat pushed for instance: {}", heartbeatInfo.getInstanceId());
        } catch (Exception e) {
            log.error("Failed to push heartbeat for instance: {}", heartbeatInfo.getInstanceId(), e);
        }
    }

    /**
     * 初始化本地心跳信息
     * 设置服务实例的元数据，包括服务 ID、IP、端口、状态等
     *
     * @param serviceId 服务 ID，标识所属服务
     * @param ip 服务实例 IP 地址
     * @param port 服务实例端口号
     * @param metadata 服务元数据（JSON 格式）
     */
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

    /**
     * 设置心跳推送间隔时间
     *
     * @param intervalMillis 间隔时间（毫秒）
     */
    public void setIntervalMillis(long intervalMillis) {
        this.intervalMillis = intervalMillis;
    }
}
