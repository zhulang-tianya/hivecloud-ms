package com.hivecloud.fault.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hivecloud.fault-removal")
public class FaultRemovalProperties {

    private long timeoutMillis = 4000;

    private int consecutiveFailureThreshold = 3;

    private String healthCheckPath = "/actuator/health";

    private int healthCheckFailureThreshold = 2;

    private long checkIntervalMillis = 2000;

    private boolean enabled = true;
}
