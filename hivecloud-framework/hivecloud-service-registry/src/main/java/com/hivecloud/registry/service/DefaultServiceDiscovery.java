package com.hivecloud.registry.service;

import com.hivecloud.registry.constant.ServiceStatus;
import com.hivecloud.registry.model.ServiceInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DefaultServiceDiscovery implements ServiceDiscovery {

    private final ServiceRegistry serviceRegistry;

    @Override
    public List<ServiceInstance> getHealthyInstances(String serviceId) {
        return serviceRegistry.getInstances(serviceId).stream()
                .filter(instance -> ServiceStatus.UP.equals(instance.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public ServiceInstance chooseInstance(String serviceId) {
        return chooseInstance(serviceId, "random");
    }

    @Override
    public ServiceInstance chooseInstance(String serviceId, String strategy) {
        List<ServiceInstance> instances = getHealthyInstances(serviceId);
        if (instances.isEmpty()) {
            throw new IllegalStateException("No healthy instances for service: " + serviceId);
        }
        return switch (strategy) {
            case "round-robin" -> roundRobin(instances, serviceId);
            case "random" -> random(instances);
            default -> random(instances);
        };
    }

    @Override
    public List<String> getHealthyServiceNames() {
        return serviceRegistry.getServiceNames().stream()
                .filter(name -> !getHealthyInstances(name).isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public int getHealthyInstanceCount(String serviceId) {
        return getHealthyInstances(serviceId).size();
    }

    private ServiceInstance random(List<ServiceInstance> instances) {
        return instances.get(ThreadLocalRandom.current().nextInt(instances.size()));
    }

    private ServiceInstance roundRobin(List<ServiceInstance> instances, String serviceId) {
        int index = (int) (System.currentTimeMillis() / 1000 % instances.size());
        return instances.get(index);
    }
}
