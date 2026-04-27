package com.hivecloud.fault.core;

import com.hivecloud.fault.config.FaultRemovalProperties;
import com.hivecloud.fault.model.FaultRemovalRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * HealthCheckRemovalStrategy 单元测试
 * 测试健康检查故障摘除策略的检测和摘除逻辑
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 * @see HealthCheckRemovalStrategy
 * @see FaultRemovalRecord
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("HealthCheckRemovalStrategy 单元测试")
class HealthCheckRemovalStrategyTest {

    @Mock
    private FaultRemovalProperties properties;

    @Mock
    private RestTemplate restTemplate;

    private HealthCheckRemovalStrategy removalStrategy;

    @BeforeEach
    void setUp() {
        when(properties.getHealthCheckPath()).thenReturn("/actuator/health");
        when(properties.getFailureThreshold()).thenReturn(3);
        removalStrategy = new HealthCheckRemovalStrategy(properties, restTemplate);
    }

    @Test
    @DisplayName("测试获取策略类型")
    void testGetStrategyType() {
        // When
        String strategyType = removalStrategy.getStrategyType();

        // Then
        assertEquals("health-check", strategyType);
    }

    @Test
    @DisplayName("测试健康检查 - 成功")
    void testHealthCheck_Success() {
        // Given
        String instanceId = "instance-001";
        String serviceId = "test-service";
        String host = "127.0.0.1";
        int port = 8080;

        doNothing().when(restTemplate).getForObject(anyString(), Void.class);

        // When
        boolean isHealthy = removalStrategy.checkHealth(instanceId, serviceId, host, port);

        // Then
        assertTrue(isHealthy);
        verify(restTemplate).getForObject(
                eq("http://127.0.0.1:8080/actuator/health"),
                eq(Void.class)
        );
    }

    @Test
    @DisplayName("测试健康检查 - 失败")
    void testHealthCheck_Failure() {
        // Given
        String instanceId = "instance-001";
        String serviceId = "test-service";
        String host = "127.0.0.1";
        int port = 8080;

        doThrow(new ResourceAccessException("Connection refused"))
                .when(restTemplate).getForObject(anyString(), Void.class);

        // When
        boolean isHealthy = removalStrategy.checkHealth(instanceId, serviceId, host, port);

        // Then
        assertFalse(isHealthy);
    }

    @Test
    @DisplayName("测试检测故障实例 - 连续失败达到阈值")
    void testDetectFaults_ConsecutiveFailuresReachThreshold() {
        // Given
        String instanceId = "instance-001";
        String serviceId = "test-service";
        String host = "127.0.0.1";
        int port = 8080;

        // Mock 连续 3 次健康检查失败
        doThrow(new ResourceAccessException("Connection refused"))
                .doThrow(new ResourceAccessException("Connection refused"))
                .doThrow(new ResourceAccessException("Connection refused"))
                .when(restTemplate).getForObject(anyString(), Void.class);

        // When
        // 第一次检查
        removalStrategy.checkHealth(instanceId, serviceId, host, port);
        // 第二次检查
        removalStrategy.checkHealth(instanceId, serviceId, host, port);
        // 第三次检查
        removalStrategy.checkHealth(instanceId, serviceId, host, port);
        // 检测故障
        List<FaultRemovalRecord> faults = removalStrategy.detectFaults(
                Collections.singletonList(createTestInstance(instanceId, serviceId, host, port))
        );

        // Then
        assertNotNull(faults);
        assertEquals(1, faults.size());
        assertEquals(instanceId, faults.get(0).getInstanceId());
        assertEquals(serviceId, faults.get(0).getServiceId());
    }

    @Test
    @DisplayName("测试检测故障实例 - 失败次数未达阈值")
    void testDetectFaults_ConsecutiveFailuresBelowThreshold() {
        // Given
        String instanceId = "instance-001";
        String serviceId = "test-service";
        String host = "127.0.0.1";
        int port = 8080;

        // Mock 连续 2 次健康检查失败
        doThrow(new ResourceAccessException("Connection refused"))
                .doThrow(new ResourceAccessException("Connection refused"))
                .when(restTemplate).getForObject(anyString(), Void.class);

        // When
        // 第一次检查
        removalStrategy.checkHealth(instanceId, serviceId, host, port);
        // 第二次检查
        removalStrategy.checkHealth(instanceId, serviceId, host, port);
        // 检测故障
        List<FaultRemovalRecord> faults = removalStrategy.detectFaults(
                Collections.singletonList(createTestInstance(instanceId, serviceId, host, port))
        );

        // Then
        assertNotNull(faults);
        assertTrue(faults.isEmpty());
    }

    @Test
    @DisplayName("测试检测故障实例 - 健康检查成功后重置计数器")
    void testDetectFaults_SuccessResetsCounter() {
        // Given
        String instanceId = "instance-001";
        String serviceId = "test-service";
        String host = "127.0.0.1";
        int port = 8080;

        // Mock 2 次失败后 1 次成功
        doThrow(new ResourceAccessException("Connection refused"))
                .doThrow(new ResourceAccessException("Connection refused"))
                .doNothing()
                .when(restTemplate).getForObject(anyString(), Void.class);

        // When
        // 第一次检查（失败）
        removalStrategy.checkHealth(instanceId, serviceId, host, port);
        // 第二次检查（失败）
        removalStrategy.checkHealth(instanceId, serviceId, host, port);
        // 第三次检查（成功）
        removalStrategy.checkHealth(instanceId, serviceId, host, port);
        // 检测故障
        List<FaultRemovalRecord> faults = removalStrategy.detectFaults(
                Collections.singletonList(createTestInstance(instanceId, serviceId, host, port))
        );

        // Then
        assertNotNull(faults);
        assertTrue(faults.isEmpty());
    }

    @Test
    @DisplayName("测试检测故障实例 - 无实例列表")
    void testDetectFaults_EmptyInstanceList() {
        // When
        List<FaultRemovalRecord> faults = removalStrategy.detectFaults(Collections.emptyList());

        // Then
        assertNotNull(faults);
        assertTrue(faults.isEmpty());
    }

    @Test
    @DisplayName("测试故障记录信息完整性")
    void testFaultRemovalRecord_Integrity() {
        // Given
        String instanceId = "instance-001";
        String serviceId = "test-service";
        String reason = "Health check failed 3 times consecutively";

        // When
        FaultRemovalRecord record = new FaultRemovalRecord(serviceId, instanceId, reason);

        // Then
        assertNotNull(record.getRecordId());
        assertEquals(serviceId, record.getServiceId());
        assertEquals(instanceId, record.getInstanceId());
        assertEquals(reason, record.getRemovalReason());
        assertNotNull(record.getRemovalTime());
    }

    /**
     * 创建测试服务实例
     */
    private com.hivecloud.registry.model.ServiceInstance createTestInstance(
            String instanceId, String serviceId, String host, int port) {
        com.hivecloud.registry.model.ServiceInstance instance = new com.hivecloud.registry.model.ServiceInstance();
        instance.setInstanceId(instanceId);
        instance.setServiceId(serviceId);
        instance.setHost(host);
        instance.setPort(port);
        return instance;
    }
}
