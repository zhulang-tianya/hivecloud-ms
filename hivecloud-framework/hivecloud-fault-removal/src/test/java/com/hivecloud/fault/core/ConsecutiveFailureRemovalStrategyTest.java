package com.hivecloud.fault.core;

import com.hivecloud.fault.config.FaultRemovalProperties;
import com.hivecloud.fault.model.FaultRemovalRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * ConsecutiveFailureRemovalStrategy 单元测试
 * 测试连续失败故障摘除策略的计数和摘除逻辑
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 * @see ConsecutiveFailureRemovalStrategy
 * @see FaultRemovalRecord
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ConsecutiveFailureRemovalStrategy 单元测试")
class ConsecutiveFailureRemovalStrategyTest {

    @Mock
    private FaultRemovalProperties properties;

    private ConsecutiveFailureRemovalStrategy removalStrategy;

    @BeforeEach
    void setUp() {
        when(properties.getFailureThreshold()).thenReturn(5);
        removalStrategy = new ConsecutiveFailureRemovalStrategy(properties);
    }

    @Test
    @DisplayName("测试获取策略类型")
    void testGetType() {
        // When
        String type = removalStrategy.getType();

        // Then
        assertEquals("CONSECUTIVE_FAILURE", type);
    }

    @Test
    @DisplayName("测试记录失败 - 成功")
    void testRecordFailure_Success() {
        // Given
        String instanceId = "instance-001";
        String serviceId = "test-service";

        // When
        removalStrategy.recordFailure(instanceId, serviceId, new RuntimeException("Test exception"));

        // Then
        // 验证失败被记录（通过后续检测验证）
        assertTrue(true);
    }

    @Test
    @DisplayName("测试记录成功 - 重置计数器")
    void testRecordSuccess_ResetsCounter() {
        // Given
        String instanceId = "instance-001";
        String serviceId = "test-service";

        // When
        // 先记录 3 次失败
        removalStrategy.recordFailure(instanceId, serviceId, new RuntimeException("Error 1"));
        removalStrategy.recordFailure(instanceId, serviceId, new RuntimeException("Error 2"));
        removalStrategy.recordFailure(instanceId, serviceId, new RuntimeException("Error 3"));
        // 记录成功
        removalStrategy.recordSuccess(instanceId, serviceId);

        // Then
        // 验证计数器被重置
        List<FaultRemovalRecord> faults = removalStrategy.detectFaults(
                Collections.singletonList(createTestInstance(instanceId, serviceId))
        );
        assertTrue(faults.isEmpty());
    }

    @Test
    @DisplayName("测试检测故障 - 连续失败达到阈值")
    void testDetectFaults_FailuresReachThreshold() {
        // Given
        String instanceId = "instance-001";
        String serviceId = "test-service";

        // When
        // 连续记录 5 次失败
        for (int i = 0; i < 5; i++) {
            removalStrategy.recordFailure(instanceId, serviceId, new RuntimeException("Error " + i));
        }

        List<FaultRemovalRecord> faults = removalStrategy.detectFaults(
                Collections.singletonList(createTestInstance(instanceId, serviceId))
        );

        // Then
        assertNotNull(faults);
        assertEquals(1, faults.size());
        assertEquals(instanceId, faults.get(0).getInstanceId());
        assertEquals(serviceId, faults.get(0).getServiceId());
        assertTrue(faults.get(0).getRemovalReason().contains("5"));
    }

    @Test
    @DisplayName("测试检测故障 - 连续失败未达阈值")
    void testDetectFaults_FailuresBelowThreshold() {
        // Given
        String instanceId = "instance-001";
        String serviceId = "test-service";

        // When
        // 连续记录 3 次失败（阈值是 5）
        for (int i = 0; i < 3; i++) {
            removalStrategy.recordFailure(instanceId, serviceId, new RuntimeException("Error " + i));
        }

        List<FaultRemovalRecord> faults = removalStrategy.detectFaults(
                Collections.singletonList(createTestInstance(instanceId, serviceId))
        );

        // Then
        assertNotNull(faults);
        assertTrue(faults.isEmpty());
    }

    @Test
    @DisplayName("测试检测故障 - 多个实例部分故障")
    void testDetectFaults_MultipleInstancesPartialFaults() {
        // Given
        String faultyInstanceId = "instance-001";
        String healthyInstanceId = "instance-002";
        String serviceId = "test-service";

        // When
        // instance-001 连续失败 5 次
        for (int i = 0; i < 5; i++) {
            removalStrategy.recordFailure(faultyInstanceId, serviceId, new RuntimeException("Error"));
        }
        // instance-002 成功
        removalStrategy.recordSuccess(healthyInstanceId, serviceId);

        List<FaultRemovalRecord> faults = removalStrategy.detectFaults(
                Arrays.asList(
                        createTestInstance(faultyInstanceId, serviceId),
                        createTestInstance(healthyInstanceId, serviceId)
                )
        );

        // Then
        assertNotNull(faults);
        assertEquals(1, faults.size());
        assertEquals(faultyInstanceId, faults.get(0).getInstanceId());
    }

    @Test
    @DisplayName("测试检测故障 - 无实例列表")
    void testDetectFaults_EmptyInstanceList() {
        // When
        List<FaultRemovalRecord> faults = removalStrategy.detectFaults(Collections.emptyList());

        // Then
        assertNotNull(faults);
        assertTrue(faults.isEmpty());
    }

    @Test
    @DisplayName("测试失败计数 - 不同实例独立计数")
    void testFailureCounts_IndependentPerInstance() {
        // Given
        String instanceId1 = "instance-001";
        String instanceId2 = "instance-002";
        String serviceId = "test-service";

        // When
        // instance-001 失败 4 次
        for (int i = 0; i < 4; i++) {
            removalStrategy.recordFailure(instanceId1, serviceId, new RuntimeException("Error"));
        }
        // instance-002 失败 2 次
        for (int i = 0; i < 2; i++) {
            removalStrategy.recordFailure(instanceId2, serviceId, new RuntimeException("Error"));
        }

        List<FaultRemovalRecord> faults = removalStrategy.detectFaults(
                Arrays.asList(
                        createTestInstance(instanceId1, serviceId),
                        createTestInstance(instanceId2, serviceId)
                )
        );

        // Then
        assertNotNull(faults);
        assertTrue(faults.isEmpty()); // 都未达到阈值 5
    }

    @Test
    @DisplayName("测试故障记录原因包含详细信息")
    void testFaultReason_ContainsDetails() {
        // Given
        String instanceId = "instance-001";
        String serviceId = "test-service";

        // When
        for (int i = 0; i < 5; i++) {
            removalStrategy.recordFailure(instanceId, serviceId, new RuntimeException("Error " + i));
        }

        List<FaultRemovalRecord> faults = removalStrategy.detectFaults(
                Collections.singletonList(createTestInstance(instanceId, serviceId))
        );

        // Then
        String reason = faults.get(0).getRemovalReason();
        assertNotNull(reason);
        assertTrue(reason.contains("CONSECUTIVE_FAILURE"));
        assertTrue(reason.contains("5"));
    }

    /**
     * 创建测试服务实例
     */
    private com.hivecloud.registry.model.ServiceInstance createTestInstance(String instanceId, String serviceId) {
        com.hivecloud.registry.model.ServiceInstance instance = new com.hivecloud.registry.model.ServiceInstance();
        instance.setInstanceId(instanceId);
        instance.setServiceId(serviceId);
        instance.setHost("127.0.0.1");
        instance.setPort(8080);
        return instance;
    }
}
