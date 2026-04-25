package com.hivecloud.heartbeat.config;

import com.hivecloud.heartbeat.core.DefaultHeartbeatPusher;
import com.hivecloud.heartbeat.core.HeartbeatStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(HeartbeatProperties.class)
@ConditionalOnProperty(name = "hivecloud.heartbeat.enabled", havingValue = "true", matchIfMissing = true)
public class HeartbeatAutoConfiguration {

    private final HeartbeatProperties heartbeatProperties;

    public HeartbeatAutoConfiguration(HeartbeatProperties heartbeatProperties) {
        this.heartbeatProperties = heartbeatProperties;
    }

    @Bean
    public DefaultHeartbeatPusher defaultHeartbeatPusher(HeartbeatStore heartbeatStore) {
        DefaultHeartbeatPusher pusher = new DefaultHeartbeatPusher(heartbeatStore);
        pusher.setIntervalMillis(heartbeatProperties.getInterval());
        pusher.initHeartbeatInfo(
                heartbeatProperties.getServiceId(),
                heartbeatProperties.getIp(),
                heartbeatProperties.getPort(),
                heartbeatProperties.getMetadata()
        );
        return pusher;
    }
}
