package com.hivecloud.plugin.cache.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * TwoLevelCacheService 单元测试
 * 测试二级缓存服务的读写、删除、过期等功能
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 * @see TwoLevelCacheService
 * @see CaffeineCacheService
 * @see RedisCacheService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TwoLevelCacheService 单元测试")
class TwoLevelCacheServiceTest {

    @Mock
    private CaffeineCacheService localCache;

    @Mock
    private RedisCacheService distributedCache;

    private TwoLevelCacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new TwoLevelCacheService(localCache, distributedCache, "test:");
    }

    @Test
    @DisplayName("测试缓存写入 - 成功")
    void testPut_Success() {
        // Given
        String key = "user:1";
        Object value = "test-value";

        // When
        cacheService.put(key, value);

        // Then
        verify(localCache).put("test:user:1", value);
        verify(distributedCache).put("test:user:1", value);
    }

    @Test
    @DisplayName("测试缓存写入带过期时间 - 成功")
    void testPut_WithTimeout_Success() {
        // Given
        String key = "user:1";
        Object value = "test-value";
        long timeout = 3600L;
        TimeUnit unit = TimeUnit.SECONDS;

        // When
        cacheService.put(key, value, timeout, unit);

        // Then
        verify(localCache).put("test:user:1", value, timeout, unit);
        verify(distributedCache).put("test:user:1", value, timeout, unit);
    }

    @Test
    @DisplayName("测试缓存读取 - L1 命中")
    void testGet_L1Hit() {
        // Given
        String key = "user:1";
        Object expectedValue = "test-value";

        when(localCache.get("test:user:1")).thenReturn(expectedValue);

        // When
        Object result = cacheService.get(key);

        // Then
        assertNotNull(result);
        assertEquals(expectedValue, result);
        verify(localCache).get("test:user:1");
        verify(distributedCache, never()).get(anyString());
    }

    @Test
    @DisplayName("测试缓存读取 - L1 未命中，L2 命中")
    void testGet_L1Miss_L2Hit() {
        // Given
        String key = "user:1";
        Object expectedValue = "test-value";

        when(localCache.get("test:user:1")).thenReturn(null);
        when(distributedCache.get("test:user:1")).thenReturn(expectedValue);

        // When
        Object result = cacheService.get(key);

        // Then
        assertNotNull(result);
        assertEquals(expectedValue, result);
        verify(localCache).get("test:user:1");
        verify(distributedCache).get("test:user:1");
    }

    @Test
    @DisplayName("测试缓存读取 - L1 和 L2 都未命中")
    void testGet_L1Miss_L2Miss() {
        // Given
        String key = "user:1";

        when(localCache.get("test:user:1")).thenReturn(null);
        when(distributedCache.get("test:user:1")).thenReturn(null);

        // When
        Object result = cacheService.get(key);

        // Then
        assertNull(result);
        verify(localCache).get("test:user:1");
        verify(distributedCache).get("test:user:1");
    }

    @Test
    @DisplayName("测试缓存删除 - 成功")
    void testDelete_Success() {
        // Given
        String key = "user:1";

        // When
        cacheService.delete(key);

        // Then
        verify(localCache).delete("test:user:1");
        verify(distributedCache).delete("test:user:1");
    }

    @Test
    @DisplayName("测试缓存是否存在 - 存在")
    void testContains_True() {
        // Given
        String key = "user:1";

        when(localCache.contains("test:user:1")).thenReturn(true);

        // When
        boolean result = cacheService.contains(key);

        // Then
        assertTrue(result);
        verify(localCache).contains("test:user:1");
    }

    @Test
    @DisplayName("测试缓存是否存在 - 不存在")
    void testContains_False() {
        // Given
        String key = "user:1";

        when(localCache.contains("test:user:1")).thenReturn(false);
        when(distributedCache.contains("test:user:1")).thenReturn(false);

        // When
        boolean result = cacheService.contains(key);

        // Then
        assertFalse(result);
        verify(localCache).contains("test:user:1");
        verify(distributedCache).contains("test:user:1");
    }

    @Test
    @DisplayName("测试清空缓存 - 成功")
    void testClear_Success() {
        // When
        cacheService.clear();

        // Then
        verify(localCache).clear();
        verify(distributedCache).clear();
    }

    @Test
    @DisplayName("测试获取缓存大小 - 成功")
    void testSize_Success() {
        // Given
        when(localCache.size()).thenReturn(10L);
        when(distributedCache.size()).thenReturn(20L);

        // When
        long size = cacheService.size();

        // Then
        // 返回 L1 缓存大小
        assertEquals(10L, size);
        verify(localCache).size();
    }

    @Test
    @DisplayName("测试获取完整缓存 Key")
    void testGetFullKey() {
        // Given
        String key = "user:1";

        // When
        String fullKey = cacheService.getFullKey(key);

        // Then
        assertEquals("test:user:1", fullKey);
    }

    @Test
    @DisplayName("测试获取完整缓存 Key - 无前缀")
    void testGetFullKey_NoPrefix() {
        // Given
        TwoLevelCacheService noPrefixCache = new TwoLevelCacheService(localCache, distributedCache, "");
        String key = "user:1";

        // When
        String fullKey = noPrefixCache.getFullKey(key);

        // Then
        assertEquals("user:1", fullKey);
    }
}
