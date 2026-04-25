package com.hivecloud.heartbeat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hivecloud.heartbeat")
public class HeartbeatProperties {

    private long interval = 1000;

    private String serviceId;

    private String ip;

    private int port;

    private String metadata;

    private boolean enabled = true;
}
