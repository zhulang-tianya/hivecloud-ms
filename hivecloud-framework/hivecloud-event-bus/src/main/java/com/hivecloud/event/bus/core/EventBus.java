package com.hivecloud.event.bus.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 事件总线
 * 基于 Spring Event 实现发布订阅模式
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Slf4j
@Component
public class EventBus implements ApplicationListener<ApplicationEvent> {

    /**
     * 事件发布器
     */
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 构造函数
     *
     * @param eventPublisher 事件发布器
     */
    public EventBus(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 发布事件
     *
     * @param event 事件对象
     */
    public void publish(ApplicationEvent event) {
        log.debug("发布事件，eventType:{}", event.getClass().getSimpleName());
        eventPublisher.publishEvent(event);
    }

    /**
     * 监听事件
     *
     * @param event 事件对象
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        log.debug("监听到事件，eventType:{}", event.getClass().getSimpleName());
    }
}
