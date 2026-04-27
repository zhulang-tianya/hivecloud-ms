package com.hivecloud.registry.service;

import com.hivecloud.registry.constant.ServiceStatus;
import com.hivecloud.registry.model.ServiceInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于 Redis 的服务注册与发现实现
 * 负责服务实例的注册、注销、状态更新、查询等功能
 * 使用 Redis Hash 存储服务元数据，Redis Set 存储服务列表
 * 实现接口：ServiceRegistry
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see ServiceRegistry
 * @see ServiceInstance
 * @see ServiceStatus
 */
@Slf4j
@RequiredArgsConstructor
public class RedisServiceRegistry implements ServiceRegistry {

    /**
     * Redis Key 前缀：服务注册与发现模块
     * 统一使用 hivecloud:registry: 前缀
     */
    private static final String REGISTRY_PREFIX = "hivecloud:registry:";
    
    /**
     * 服务元数据 Key 模板
     * 格式：hivecloud:registry:meta:{serviceId}:{instanceId}
     */
    private static final String SERVICE_META_KEY_TEMPLATE = REGISTRY_PREFIX + "meta:{serviceId}:{instanceId}";
    
    /**
     * 服务集合 Key 模板
     * 格式：hivecloud:registry:set:{serviceId}
     */
    private static final String SERVICE_SET_KEY_TEMPLATE = REGISTRY_PREFIX + "set:{serviceId}";
    
    /**
     * 全局服务集合 Key
     * 格式：hivecloud:registry:set
     */
    private static final String GLOBAL_SERVICE_SET_KEY = REGISTRY_PREFIX + "set";

    /**
     * Redis 模板，用于操作 Redis 存储
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 构建服务元数据 Key
     *
     * @param serviceId 服务 ID
     * @param instanceId 实例 ID
     * @return 格式化的 Key
     */
    private String buildServiceMetaKey(String serviceId, String instanceId) {
        return SERVICE_META_KEY_TEMPLATE
                .replace("{serviceId}", serviceId)
                .replace("{instanceId}", instanceId);
    }

    /**
     * 构建服务集合 Key
     *
     * @param serviceId 服务 ID
     * @return 格式化的 Key
     */
    private String buildServiceSetKey(String serviceId) {
        return SERVICE_SET_KEY_TEMPLATE.replace("{serviceId}", serviceId);
    }

    /**
     * 注册服务实例
     * 将服务实例元数据保存到 Redis，设置初始状态为 UP
     *
     * @param instance 服务实例信息，包含服务 ID、实例 ID、IP、端口等
     */
    @Override
    public void register(ServiceInstance instance) {
        String metaKey = buildServiceMetaKey(instance.getServiceId(), instance.getInstanceId());
        String setKey = buildServiceSetKey(instance.getServiceId());
        instance.setRegisterTime(System.currentTimeMillis());
        instance.setLastHeartbeatTime(System.currentTimeMillis());
        instance.setStatus(ServiceStatus.UP);
        redisTemplate.opsForValue().set(metaKey, instance);
        redisTemplate.opsForSet().add(setKey, instance.getInstanceId());
        redisTemplate.opsForSet().add(GLOBAL_SERVICE_SET_KEY, instance.getServiceId());
        log.info("Service registered: {} ({})", instance.getServiceId(), instance.getInstanceId());
    }

    /**
     * 注销服务实例
     * 从 Redis 中删除服务元数据，清理服务集合
     *
     * @param serviceId 服务 ID
     * @param instanceId 服务实例 ID
     */
    @Override
    public void deregister(String serviceId, String instanceId) {
        String metaKey = buildServiceMetaKey(serviceId, instanceId);
        String setKey = buildServiceSetKey(serviceId);
        redisTemplate.delete(metaKey);
        redisTemplate.opsForSet().remove(setKey, instanceId);
        Long size = redisTemplate.opsForSet().size(setKey);
        if (size == null || size == 0) {
            redisTemplate.opsForSet().remove(GLOBAL_SERVICE_SET_KEY, serviceId);
        }
        log.info("Service deregistered: {} ({})", serviceId, instanceId);
    }

    /**
     * 更新服务实例状态
     * 修改服务实例的运行状态（UP/DOWN），并更新最后心跳时间
     *
     * @param serviceId 服务 ID
     * @param instanceId 服务实例 ID
     * @param status 新的服务状态
     */
    @Override
    public void updateStatus(String serviceId, String instanceId, ServiceStatus status) {
        String metaKey = buildServiceMetaKey(serviceId, instanceId);
        ServiceInstance instance = (ServiceInstance) redisTemplate.opsForValue().get(metaKey);
        if (instance != null) {
            instance.setStatus(status);
            instance.setLastHeartbeatTime(System.currentTimeMillis());
            redisTemplate.opsForValue().set(metaKey, instance);
        }
    }

    /**
     * 获取指定服务实例的详细信息
     *
     * @param serviceId 服务 ID
     * @param instanceId 服务实例 ID
     * @return 服务实例信息，不存在时返回 null
     */
    @Override
    public ServiceInstance getInstance(String serviceId, String instanceId) {
        String metaKey = buildServiceMetaKey(serviceId, instanceId);
        return (ServiceInstance) redisTemplate.opsForValue().get(metaKey);
    }

    /**
     * 获取指定服务的所有实例列表
     *
     * @param serviceId 服务 ID
     * @return 服务实例列表，无实例时返回空列表
     */
    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        String setKey = buildServiceSetKey(serviceId);
        Set<Object> members = redisTemplate.opsForSet().members(setKey);
        if (members == null || members.isEmpty()) {
            return Collections.emptyList();
        }
        return members.stream()
                .map(instanceId -> getInstance(serviceId, instanceId.toString()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有已注册的服务名称列表
     *
     * @return 服务名称列表
     */
    @Override
    public List<String> getServiceNames() {
        Set<Object> members = redisTemplate.opsForSet().members(GLOBAL_SERVICE_SET_KEY);
        if (members == null) {
            return Collections.emptyList();
        }
        return members.stream().map(Object::toString).collect(Collectors.toList());
    }

    /**
     * 检查服务实例是否存在
     *
     * @param serviceId 服务 ID
     * @param instanceId 服务实例 ID
     * @return true-存在，false-不存在
     */
    @Override
    public boolean exists(String serviceId, String instanceId) {
        String metaKey = buildServiceMetaKey(serviceId, instanceId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(metaKey));
    }
}
