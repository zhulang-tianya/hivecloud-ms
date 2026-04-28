package com.hivecloud.gossip.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hivecloud.gossip")
public class GossipProperties {

    private String ip;

    private Integer port;

    private int fanout = 3;

    private long syncIntervalMillis = 5000;

    private long nodeTtlSeconds = 30;

    private boolean enabled = true;
}
