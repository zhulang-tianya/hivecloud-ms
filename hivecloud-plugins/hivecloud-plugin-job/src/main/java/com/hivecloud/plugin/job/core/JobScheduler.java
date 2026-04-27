package com.hivecloud.plugin.job.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 定时任务调度器
 * 基于 ThreadPoolTaskScheduler 实现任务调度
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Slf4j
@Component
public class JobScheduler {

    /**
     * 任务调度器
     */
    private final ThreadPoolTaskScheduler taskScheduler;

    /**
     * 任务注册表（任务 ID -> ScheduledFuture）
     */
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    /**
     * 构造函数，初始化任务调度器
     */
    public JobScheduler() {
        this.taskScheduler = new ThreadPoolTaskScheduler();
        this.taskScheduler.setPoolSize(10);
        this.taskScheduler.setThreadNamePrefix("job-scheduler-");
        this.taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        this.taskScheduler.setAwaitTerminationSeconds(60);
    }

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        taskScheduler.initialize();
        log.info("定时任务调度器初始化完成");
    }

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        taskScheduler.shutdown();
        log.info("定时任务调度器已关闭");
    }

    /**
     * 调度任务
     *
     * @param jobId 任务 ID
     * @param task 任务执行体
     * @param cronExpression cron 表达式
     */
    public void scheduleJob(Long jobId, Runnable task, String cronExpression) {
        log.info("开始调度任务，jobId:{}, cron:{}", jobId, cronExpression);

        try {
            ScheduledFuture<?> future = taskScheduler.schedule(task, triggerContext -> {
                Instant nextTime = triggerContext.lastScheduledExecutionTime()
                        .map(time -> time.plusMillis(1000))
                        .orElse(Instant.now());
                return nextTime;
            });

            scheduledTasks.put(jobId, future);
            log.info("任务调度成功，jobId:{}", jobId);
        } catch (Exception e) {
            log.error("任务调度失败，jobId:{}, cron:{}", jobId, cronExpression, e);
            throw new RuntimeException("任务调度失败", e);
        }
    }

    /**
     * 取消任务
     *
     * @param jobId 任务 ID
     */
    public void cancelJob(Long jobId) {
        log.info("开始取消任务，jobId:{}", jobId);

        ScheduledFuture<?> future = scheduledTasks.remove(jobId);
        if (future != null) {
            future.cancel(false);
            log.info("任务取消成功，jobId:{}", jobId);
        } else {
            log.warn("任务不存在，无需取消，jobId:{}", jobId);
        }
    }

    /**
     * 暂停任务
     *
     * @param jobId 任务 ID
     */
    public void pauseJob(Long jobId) {
        log.info("开始暂停任务，jobId:{}", jobId);

        ScheduledFuture<?> future = scheduledTasks.get(jobId);
        if (future != null) {
            future.cancel(false);
            log.info("任务暂停成功，jobId:{}", jobId);
        } else {
            log.warn("任务不存在，无法暂停，jobId:{}", jobId);
        }
    }

    /**
     * 恢复任务
     *
     * @param jobId 任务 ID
     * @param task 任务执行体
     * @param cronExpression cron 表达式
     */
    public void resumeJob(Long jobId, Runnable task, String cronExpression) {
        log.info("开始恢复任务，jobId:{}, cron:{}", jobId, cronExpression);
        scheduleJob(jobId, task, cronExpression);
    }

    /**
     * 获取任务状态
     *
     * @param jobId 任务 ID
     * @return true-运行中 false-已停止
     */
    public boolean isJobRunning(Long jobId) {
        ScheduledFuture<?> future = scheduledTasks.get(jobId);
        return future != null && !future.isCancelled() && !future.isDone();
    }
}
