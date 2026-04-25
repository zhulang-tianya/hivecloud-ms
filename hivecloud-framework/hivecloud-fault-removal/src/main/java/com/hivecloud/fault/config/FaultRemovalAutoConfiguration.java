package com.hivecloud.fault.config;

import com.hivecloud.fault.core.*;
import com.hivecloud.heartbeat.core.HeartbeatStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(FaultRemovalProperties.class)
@ConditionalOnProperty(name = "hivecloud.fault-removal.enabled", havingValue = "true", matchIfMissing = true)
public class FaultRemovalAutoConfiguration {

    private final FaultRemovalProperties properties;

    public FaultRemovalAutoConfiguration(FaultRemovalProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public TimeoutRemovalStrategy timeoutRemovalStrategy(HeartbeatStore heartbeatStore,
                                                         org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate) {
        return new TimeoutRemovalStrategy(heartbeatStore, redisTemplate, properties);
    }

    @Bean
    public ConsecutiveFailureRemovalStrategy consecutiveFailureRemovalStrategy() {
        return new ConsecutiveFailureRemovalStrategy(properties);
    }

    @Bean
    public HealthCheckRemovalStrategy healthCheckRemovalStrategy(RestTemplate restTemplate) {
        return new HealthCheckRemovalStrategy(properties, restTemplate);
    }

    @Bean
    public DefaultFaultRemovalScheduler faultRemovalScheduler(TimeoutRemovalStrategy timeoutStrategy,
                                                               ConsecutiveFailureRemovalStrategy consecutiveFailureStrategy,
                                                               HealthCheckRemovalStrategy healthCheckStrategy) {
        DefaultFaultRemovalScheduler scheduler = new DefaultFaultRemovalScheduler(properties);
        scheduler.registerStrategy(timeoutStrategy);
        scheduler.registerStrategy(consecutiveFailureStrategy);
        scheduler.registerStrategy(healthCheckStrategy);
        return scheduler;
    }
}
