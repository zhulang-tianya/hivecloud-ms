package com.hivecloud.heartbeat.core;

import com.hivecloud.heartbeat.model.HeartbeatInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * RedisHeartbeatStore 单元测试
 * 测试心跳存储的保存、查询、删除等功能
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 * @see RedisHeartbeatStore
 * @see HeartbeatInfo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RedisHeartbeatStore 单元测试")
class RedisHeartbeatStoreTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOps;

    private RedisHeartbeatStore heartbeatStore;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        heartbeatStore = new RedisHeartbeatStore(redisTemplate);
    }

    @Test
    @DisplayName("测试保存心跳信息 - 成功")
    void testSave_Success() {
        // Given
        HeartbeatInfo info = createTestHeartbeatInfo();

        // When
        heartbeatStore.save(info);

        // Then
        verify(valueOps).set(
                eq("hivecloud:heartbeat:instance-001"),
                any(HeartbeatInfo.class),
                eq(30L),
                eq(TimeUnit.SECONDS)
        );
    }

    @Test
    @DisplayName("测试获取心跳信息 - 存在")
    void testGet_Exists() {
        // Given
        String instanceId = "instance-001";
        HeartbeatInfo expectedInfo = createTestHeartbeatInfo();

        when(valueOps.get("hivecloud:heartbeat:instance-001")).thenReturn(expectedInfo);

        // When
        HeartbeatInfo result = heartbeatStore.get(instanceId);

        // Then
        assertNotNull(result);
        assertEquals(expectedInfo, result);
        assertEquals("instance-001", result.getInstanceId());
    }

    @Test
    @DisplayName("测试获取心跳信息 - 不存在")
    void testGet_NotExists() {
        // Given
        String instanceId = "instance-001";

        when(valueOps.get("hivecloud:heartbeat:instance-001")).thenReturn(null);

        // When
        HeartbeatInfo result = heartbeatStore.get(instanceId);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("测试删除心跳信息 - 成功")
    void testDelete_Success() {
        // Given
        String instanceId = "instance-001";

        // When
        heartbeatStore.delete(instanceId);

        // Then
        verify(redisTemplate).delete("hivecloud:heartbeat:instance-001");
    }

    @Test
    @DisplayName("测试心跳是否存在 - 存在")
    void testExists_True() {
        // Given
        String instanceId = "instance-001";

        when(redisTemplate.hasKey("hivecloud:heartbeat:instance-001")).thenReturn(true);

        // When
        boolean result = heartbeatStore.exists(instanceId);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("测试心跳是否存在 - 不存在")
    void testExists_False() {
        // Given
        String instanceId = "instance-001";

        when(redisTemplate.hasKey("hivecloud:heartbeat:instance-001")).thenReturn(false);

        // When
        boolean result = heartbeatStore.exists(instanceId);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("测试更新心跳信息 - 成功")
    void testUpdate_Success() {
        // Given
        HeartbeatInfo info = createTestHeartbeatInfo();

        // When
        heartbeatStore.update(info);

        // Then
        verify(valueOps).set(
                eq("hivecloud:heartbeat:instance-001"),
                eq(info),
                eq(30L),
                eq(TimeUnit.SECONDS)
        );
    }

    @Test
    @DisplayName("测试心跳 Key 构建 - 格式正确")
    void testBuildHeartbeatKey_CorrectFormat() {
        // Given
        String instanceId = "test-instance-123";

        // When
        // 通过保存操作验证 Key 格式
        HeartbeatInfo info = createTestHeartbeatInfo();
        info.setInstanceId(instanceId);
        heartbeatStore.save(info);

        // Then
        verify(valueOps).set(
                eq("hivecloud:heartbeat:test-instance-123"),
                any(),
                anyLong(),
                any()
        );
    }

    @Test
    @DisplayName("测试心跳过期时间 - 使用默认值")
    void testHeartbeatTTL_UsesDefault() {
        // Given
        HeartbeatInfo info = createTestHeartbeatInfo();

        // When
        heartbeatStore.save(info);

        // Then
        verify(valueOps).set(
                anyString(),
                any(),
                eq(30L),
                eq(TimeUnit.SECONDS)
        );
    }

    @Test
    @DisplayName("测试心跳信息完整性")
    void testHeartbeatInfo_Integrity() {
        // Given
        HeartbeatInfo info = createTestHeartbeatInfo();

        // Then
        assertNotNull(info.getServiceId());
        assertNotNull(info.getInstanceId());
        assertNotNull(info.getHost());
        assertTrue(info.getPort() > 0);
        assertTrue(info.getTimestamp() > 0);
    }

    /**
     * 创建测试心跳信息
     */
    private HeartbeatInfo createTestHeartbeatInfo() {
        HeartbeatInfo info = new HeartbeatInfo();
        info.setServiceId("test-service");
        info.setInstanceId("instance-001");
        info.setHost("127.0.0.1");
        info.setPort(8080);
        info.setTimestamp(System.currentTimeMillis());
        return info;
    }
}
