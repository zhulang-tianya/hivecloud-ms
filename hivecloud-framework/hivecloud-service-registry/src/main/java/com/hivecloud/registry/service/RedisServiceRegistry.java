package com.hivecloud.registry.service;

import com.hivecloud.registry.constant.ServiceStatus;
import com.hivecloud.registry.model.ServiceInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class RedisServiceRegistry implements ServiceRegistry {

    private static final String SERVICE_META_KEY = "hivecloud:service:meta:";
    private static final String SERVICE_SET_KEY = "hivecloud:service:set";

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void register(ServiceInstance instance) {
        String metaKey = SERVICE_META_KEY + instance.getServiceId() + ":" + instance.getInstanceId();
        instance.setRegisterTime(System.currentTimeMillis());
        instance.setLastHeartbeatTime(System.currentTimeMillis());
        instance.setStatus(ServiceStatus.UP);
        redisTemplate.opsForValue().set(metaKey, instance);
        redisTemplate.opsForSet().add(SERVICE_SET_KEY, instance.getServiceId());
        log.info("Service registered: {} ({})", instance.getServiceId(), instance.getInstanceId());
    }

    @Override
    public void deregister(String serviceId, String instanceId) {
        String metaKey = SERVICE_META_KEY + serviceId + ":" + instanceId;
        redisTemplate.delete(metaKey);
        Long size = redisTemplate.opsForSet().size(SERVICE_SET_KEY + serviceId);
        if (size == null || size == 0) {
            redisTemplate.opsForSet().remove(SERVICE_SET_KEY, serviceId);
        }
        log.info("Service deregistered: {} ({})", serviceId, instanceId);
    }

    @Override
    public void updateStatus(String serviceId, String instanceId, ServiceStatus status) {
        String metaKey = SERVICE_META_KEY + serviceId + ":" + instanceId;
        ServiceInstance instance = (ServiceInstance) redisTemplate.opsForValue().get(metaKey);
        if (instance != null) {
            instance.setStatus(status);
            instance.setLastHeartbeatTime(System.currentTimeMillis());
            redisTemplate.opsForValue().set(metaKey, instance);
        }
    }

    @Override
    public ServiceInstance getInstance(String serviceId, String instanceId) {
        String metaKey = SERVICE_META_KEY + serviceId + ":" + instanceId;
        return (ServiceInstance) redisTemplate.opsForValue().get(metaKey);
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        Set<Object> members = redisTemplate.opsForSet().members(SERVICE_SET_KEY + serviceId);
        if (members == null || members.isEmpty()) {
            return Collections.emptyList();
        }
        return members.stream()
                .map(instanceId -> getInstance(serviceId, instanceId.toString()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getServiceNames() {
        Set<Object> members = redisTemplate.opsForSet().members(SERVICE_SET_KEY);
        if (members == null) {
            return Collections.emptyList();
        }
        return members.stream().map(Object::toString).collect(Collectors.toList());
    }

    @Override
    public boolean exists(String serviceId, String instanceId) {
        String metaKey = SERVICE_META_KEY + serviceId + ":" + instanceId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(metaKey));
    }
}
