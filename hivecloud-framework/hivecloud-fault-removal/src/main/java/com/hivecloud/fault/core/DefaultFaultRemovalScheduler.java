package com.hivecloud.fault.core;

import com.hivecloud.fault.config.FaultRemovalProperties;
import com.hivecloud.fault.model.FaultRemovalRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

/**
 * 默认故障摘除调度器实现
 * 负责周期性执行服务实例的健康检查，自动摘除故障实例
 * 支持多种故障检测策略，可配置检查间隔时间
 * 实现接口：FaultRemovalScheduler
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see FaultRemovalScheduler
 * @see FaultRemovalStrategy
 * @see FaultRemovalProperties
 * @see FaultRemovalRecord
 */
@Slf4j
public class DefaultFaultRemovalScheduler implements FaultRemovalScheduler {

    /**
     * 故障摘除配置属性，包含检查间隔、超时阈值等
     */
    private final FaultRemovalProperties properties;

    /**
     * 故障摘除策略列表，支持多种检测策略并发执行
     */
    private final List<FaultRemovalStrategy> strategies = new CopyOnWriteArrayList<>();

    /**
     * 定时任务调度器，负责周期性健康检查任务调度
     */
    private ThreadPoolTaskScheduler taskScheduler;

    /**
     * 定时任务 Future 对象，用于任务取消和状态管理
     */
    private ScheduledFuture<?> scheduledFuture;

    /**
     * 构造函数
     *
     * @param properties 故障摘除配置属性
     */
    public DefaultFaultRemovalScheduler(FaultRemovalProperties properties) {
        this.properties = properties;
    }

    /**
     * 启动故障摘除调度器
     * 初始化线程池，创建周期性检查任务
     */
    @Override
    public void start() {
        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("fault-removal-");
        taskScheduler.initialize();

        scheduledFuture = taskScheduler.scheduleAtFixedRate(
                this::executeRemoval,
                Instant.now(),
                Duration.ofMillis(properties.getCheckIntervalMillis())
        );
        log.info("Fault removal scheduler started with interval {}ms", properties.getCheckIntervalMillis());
    }

    /**
     * 停止故障摘除调度器
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
        log.info("Fault removal scheduler stopped");
    }

    /**
     * 注册故障摘除策略
     * 支持动态添加多种检测策略
     *
     * @param strategy 故障摘除策略实现
     */
    @Override
    public void registerStrategy(FaultRemovalStrategy strategy) {
        strategies.add(strategy);
        log.info("Registered fault removal strategy: {}", strategy.getType());
    }

    /**
     * 执行故障摘除任务
     * 遍历所有策略，执行故障检测并摘除故障实例
     *
     * @return 被摘除的故障实例记录列表
     */
    @Override
    public List<FaultRemovalRecord> executeRemoval() {
        List<FaultRemovalRecord> allRemoved = new ArrayList<>();
        for (FaultRemovalStrategy strategy : strategies) {
            try {
                List<FaultRemovalRecord> removed = strategy.detectAndRemove();
                allRemoved.addAll(removed);
                if (!removed.isEmpty()) {
                    log.info("Strategy {} removed {} instances", strategy.getType(), removed.size());
                }
            } catch (Exception e) {
                log.error("Error executing strategy {}", strategy.getType(), e);
            }
        }
        return allRemoved;
    }
}
