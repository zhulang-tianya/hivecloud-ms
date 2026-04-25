package com.hivecloud.registry.service;

import com.hivecloud.registry.constant.ServiceStatus;
import com.hivecloud.registry.model.ServiceInstance;

import java.util.List;

public interface ServiceRegistry {

    void register(ServiceInstance instance);

    void deregister(String serviceId, String instanceId);

    void updateStatus(String serviceId, String instanceId, ServiceStatus status);

    ServiceInstance getInstance(String serviceId, String instanceId);

    List<ServiceInstance> getInstances(String serviceId);

    List<String> getServiceNames();

    boolean exists(String serviceId, String instanceId);
}
