package com.hivecloud.registry.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hivecloud.registry")
public class RegistryProperties {

    private boolean enabled = true;

    private String namespace = "default";

    private long metadataTtl = 30000;

    private String heartbeatKeyPrefix = "hivecloud:heartbeat:";

    private String metadataKeyPrefix = "hivecloud:service:meta:";
}
