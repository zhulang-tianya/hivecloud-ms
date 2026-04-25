package com.hivecloud.registry.service;

import com.hivecloud.registry.model.ServiceInstance;

import java.util.List;

public interface ServiceDiscovery {

    List<ServiceInstance> getHealthyInstances(String serviceId);

    ServiceInstance chooseInstance(String serviceId);

    ServiceInstance chooseInstance(String serviceId, String strategy);

    List<String> getHealthyServiceNames();

    int getHealthyInstanceCount(String serviceId);
}
