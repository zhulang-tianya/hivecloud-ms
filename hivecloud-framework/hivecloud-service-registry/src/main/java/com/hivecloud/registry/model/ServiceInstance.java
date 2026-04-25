package com.hivecloud.registry.model;

import com.hivecloud.registry.constant.ServiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInstance implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serviceId;

    private String instanceId;

    private String host;

    private Integer port;

    private String protocol;

    private ServiceStatus status;

    private Long registerTime;

    private Long lastHeartbeatTime;

    private Map<String, String> metadata;

    public String getAddress() {
        return host + ":" + port;
    }

    public String getBaseUrl() {
        return protocol + "://" + host + ":" + port;
    }
}
