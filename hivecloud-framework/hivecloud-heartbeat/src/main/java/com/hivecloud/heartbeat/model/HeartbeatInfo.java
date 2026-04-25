package com.hivecloud.heartbeat.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class HeartbeatInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serviceId;

    private String instanceId;

    private String ip;

    private Integer port;

    private LocalDateTime lastHeartbeat;

    private Long heartbeatInterval;

    private Integer status;

    private String metadata;
}
