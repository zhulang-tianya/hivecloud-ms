package com.hivecloud.registry.service;

import com.hivecloud.registry.constant.ServiceStatus;
import com.hivecloud.registry.model.ServiceInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * RedisServiceRegistry 单元测试
 * 测试服务注册、注销、状态更新等功能
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 * @see RedisServiceRegistry
 * @see ServiceInstance
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RedisServiceRegistry 单元测试")
class RedisServiceRegistryTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOps;

    @Mock
    private SetOperations<String, Object> setOps;

    private RedisServiceRegistry registry;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.opsForSet()).thenReturn(setOps);
        registry = new RedisServiceRegistry(redisTemplate);
    }

    @Test
    @DisplayName("测试服务注册 - 成功")
    void testRegister_Success() {
        // Given
        ServiceInstance instance = createTestInstance("test-service", "instance-001");
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ServiceInstance> instanceCaptor = ArgumentCaptor.forClass(ServiceInstance.class);

        // When
        registry.register(instance);

        // Then
        verify(valueOps).set(keyCaptor.capture(), instanceCaptor.capture());
        verify(setOps).add(eq("hivecloud:registry:set:test-service"), eq("instance-001"));
        verify(setOps).add(eq("hivecloud:registry:set"), eq("test-service"));

        String capturedKey = keyCaptor.getValue();
        ServiceInstance capturedInstance = instanceCaptor.getValue();

        assertTrue(capturedKey.contains("hivecloud:registry:meta:test-service:instance-001"));
        assertEquals(ServiceStatus.UP, capturedInstance.getStatus());
        assertNotNull(capturedInstance.getRegisterTime());
        assertNotNull(capturedInstance.getLastHeartbeatTime());
    }

    @Test
    @DisplayName("测试服务注销 - 成功")
    void testDeregister_Success() {
        // Given
        String serviceId = "test-service";
        String instanceId = "instance-001";
        when(setOps.size(anyString())).thenReturn(0L);

        // When
        registry.deregister(serviceId, instanceId);

        // Then
        verify(redisTemplate).delete(eq("hivecloud:registry:meta:test-service:instance-001"));
        verify(setOps).remove(eq("hivecloud:registry:set:test-service"), eq("instance-001"));
        verify(setOps).remove(eq("hivecloud:registry:set"), eq("test-service"));
    }

    @Test
    @DisplayName("测试服务注销 - 集合不为空时不删除全局服务")
    void testDeregister_WhenSetNotEmpty() {
        // Given
        String serviceId = "test-service";
        String instanceId = "instance-001";
        when(setOps.size(anyString())).thenReturn(2L);

        // When
        registry.deregister(serviceId, instanceId);

        // Then
        verify(redisTemplate).delete(eq("hivecloud:registry:meta:test-service:instance-001"));
        verify(setOps).remove(eq("hivecloud:registry:set:test-service"), eq("instance-001"));
        verify(setOps, never()).remove(eq("hivecloud:registry:set"), eq("test-service"));
    }

    @Test
    @DisplayName("测试更新服务状态 - 成功")
    void testUpdateStatus_Success() {
        // Given
        String serviceId = "test-service";
        String instanceId = "instance-001";
        ServiceInstance existingInstance = createTestInstance(serviceId, instanceId);
        existingInstance.setStatus(ServiceStatus.UP);

        when(valueOps.get(anyString())).thenReturn(existingInstance);

        // When
        registry.updateStatus(serviceId, instanceId, ServiceStatus.DOWN);

        // Then
        verify(valueOps).set(anyString(), any(ServiceInstance.class));
        assertEquals(ServiceStatus.DOWN, existingInstance.getStatus());
        assertNotNull(existingInstance.getLastHeartbeatTime());
    }

    @Test
    @DisplayName("测试更新服务状态 - 实例不存在")
    void testUpdateStatus_InstanceNotFound() {
        // Given
        String serviceId = "test-service";
        String instanceId = "instance-001";
        when(valueOps.get(anyString())).thenReturn(null);

        // When
        registry.updateStatus(serviceId, instanceId, ServiceStatus.DOWN);

        // Then
        verify(valueOps, never()).set(anyString(), any());
    }

    @Test
    @DisplayName("测试获取服务实例 - 存在")
    void testGetInstance_Exists() {
        // Given
        String serviceId = "test-service";
        String instanceId = "instance-001";
        ServiceInstance expectedInstance = createTestInstance(serviceId, instanceId);

        when(valueOps.get(anyString())).thenReturn(expectedInstance);

        // When
        ServiceInstance result = registry.getInstance(serviceId, instanceId);

        // Then
        assertNotNull(result);
        assertEquals(instanceId, result.getInstanceId());
        assertEquals(serviceId, result.getServiceId());
    }

    @Test
    @DisplayName("测试获取服务实例 - 不存在")
    void testGetInstance_NotExists() {
        // Given
        when(valueOps.get(anyString())).thenReturn(null);

        // When
        ServiceInstance result = registry.getInstance("test-service", "instance-001");

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("测试获取服务所有实例 - 有多个实例")
    void testGetInstances_WithMultipleInstances() {
        // Given
        String serviceId = "test-service";
        Set<Object> instanceIds = Set.of("instance-001", "instance-002");

        when(setOps.members(anyString())).thenReturn(instanceIds);
        when(valueOps.get(anyString()))
                .thenReturn(createTestInstance(serviceId, "instance-001"))
                .thenReturn(createTestInstance(serviceId, "instance-002"));

        // When
        List<ServiceInstance> result = registry.getInstances(serviceId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("测试获取服务所有实例 - 无实例")
    void testGetInstances_Empty() {
        // Given
        when(setOps.members(anyString())).thenReturn(Collections.emptySet());

        // When
        List<ServiceInstance> result = registry.getInstances("test-service");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试获取所有服务名称 - 有服务")
    void testGetServiceNames_WithServices() {
        // Given
        Set<Object> serviceNames = Set.of("service-1", "service-2", "service-3");
        when(setOps.members(anyString())).thenReturn(serviceNames);

        // When
        List<String> result = registry.getServiceNames();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("service-1"));
        assertTrue(result.contains("service-2"));
        assertTrue(result.contains("service-3"));
    }

    @Test
    @DisplayName("测试获取所有服务名称 - 无服务")
    void testGetServiceNames_Empty() {
        // Given
        when(setOps.members(anyString())).thenReturn(null);

        // When
        List<String> result = registry.getServiceNames();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试服务实例是否存在 - 存在")
    void testExists_InstanceExists() {
        // Given
        when(redisTemplate.hasKey(anyString())).thenReturn(true);

        // When
        boolean result = registry.exists("test-service", "instance-001");

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("测试服务实例是否存在 - 不存在")
    void testExists_InstanceNotExists() {
        // Given
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        // When
        boolean result = registry.exists("test-service", "instance-001");

        // Then
        assertFalse(result);
    }

    /**
     * 创建测试服务实例
     */
    private ServiceInstance createTestInstance(String serviceId, String instanceId) {
        ServiceInstance instance = new ServiceInstance();
        instance.setServiceId(serviceId);
        instance.setInstanceId(instanceId);
        instance.setHost("127.0.0.1");
        instance.setPort(8080);
        instance.setStatus(ServiceStatus.UP);
        return instance;
    }
}
