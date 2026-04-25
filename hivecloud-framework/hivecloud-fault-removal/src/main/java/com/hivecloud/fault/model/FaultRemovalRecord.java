package com.hivecloud.fault.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FaultRemovalRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private String instanceId;

    private String serviceId;

    private String reason;

    private String removalType;

    private LocalDateTime removalTime;

    private LocalDateTime lastHeartbeat;

    private Integer consecutiveFailures;
}
