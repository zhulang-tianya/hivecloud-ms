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

@Slf4j
public class DefaultFaultRemovalScheduler implements FaultRemovalScheduler {

    private final FaultRemovalProperties properties;

    private final List<FaultRemovalStrategy> strategies = new CopyOnWriteArrayList<>();

    private ThreadPoolTaskScheduler taskScheduler;

    private ScheduledFuture<?> scheduledFuture;

    public DefaultFaultRemovalScheduler(FaultRemovalProperties properties) {
        this.properties = properties;
    }

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

    @Override
    public void registerStrategy(FaultRemovalStrategy strategy) {
        strategies.add(strategy);
        log.info("Registered fault removal strategy: {}", strategy.getType());
    }

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
