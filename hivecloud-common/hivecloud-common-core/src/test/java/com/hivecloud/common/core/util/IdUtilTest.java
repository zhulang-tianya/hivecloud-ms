package com.hivecloud.common.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IdUtil 单元测试类
 *
 * @author HiveCloud Team
 * @date 2026-04-28
 */
@DisplayName("IdUtil 工具类测试")
class IdUtilTest {

    @Test
    @DisplayName("测试 uuid 方法 - 生成带连字符的 UUID")
    void testUuid() {
        String uuid = IdUtil.uuid();
        assertNotNull(uuid);
        assertEquals(36, uuid.length());
        assertTrue(uuid.contains("-"));
    }

    @Test
    @DisplayName("测试 uuidSimple 方法 - 生成不带连字符的 UUID")
    void testUuidSimple() {
        String uuid = IdUtil.uuidSimple();
        assertNotNull(uuid);
        assertEquals(32, uuid.length());
        assertFalse(uuid.contains("-"));
    }

    @Test
    @DisplayName("测试 uuid32 方法 - 生成小写 UUID")
    void testUuid32() {
        String uuid = IdUtil.uuid32();
        assertNotNull(uuid);
        assertEquals(32, uuid.length());
        assertEquals(uuid.toLowerCase(), uuid);
    }

    @Test
    @DisplayName("测试 uuid32Upper 方法 - 生成大写 UUID")
    void testUuid32Upper() {
        String uuid = IdUtil.uuid32Upper();
        assertNotNull(uuid);
        assertEquals(32, uuid.length());
        assertEquals(uuid.toUpperCase(), uuid);
    }

    @Test
    @DisplayName("测试 snowflake 方法 - 生成雪花算法 ID")
    void testSnowflake() {
        long snowflake = IdUtil.snowflake();
        assertTrue(snowflake > 0);
        assertTrue(snowflake < Long.MAX_VALUE);
    }

    @Test
    @DisplayName("测试 snowflakeString 方法 - 生成雪花 ID 字符串")
    void testSnowflakeString() {
        String snowflake = IdUtil.snowflakeString();
        assertNotNull(snowflake);
        assertTrue(snowflake.length() > 0);
        assertTrue(snowflake.length() <= 20);
    }

    @Test
    @DisplayName("测试 snowflake 方法 - 生成唯一 ID")
    void testSnowflakeUniqueness() {
        long id1 = IdUtil.snowflake();
        long id2 = IdUtil.snowflake();
        assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("测试 setWorkerId 方法 - 设置工作机器 ID")
    void testSetWorkerId() {
        assertDoesNotThrow(() -> IdUtil.setWorkerId(1));
        assertThrows(IllegalArgumentException.class, () -> IdUtil.setWorkerId(-1));
        assertThrows(IllegalArgumentException.class, () -> IdUtil.setWorkerId(32));
    }

    @Test
    @DisplayName("测试 setDatacenterId 方法 - 设置数据中心 ID")
    void testSetDatacenterId() {
        assertDoesNotThrow(() -> IdUtil.setDatacenterId(1));
        assertThrows(IllegalArgumentException.class, () -> IdUtil.setDatacenterId(-1));
        assertThrows(IllegalArgumentException.class, () -> IdUtil.setDatacenterId(32));
    }

    @Test
    @DisplayName("测试 timestampId 方法 - 生成时间戳 ID")
    void testTimestampId() {
        String timestampId = IdUtil.timestampId();
        assertNotNull(timestampId);
        assertEquals(19, timestampId.length());
        assertTrue(timestampId.matches("\\d{19}"));
    }

    @Test
    @DisplayName("测试 orderId 方法 - 生成订单 ID")
    void testOrderId() {
        String orderId = IdUtil.orderId();
        assertNotNull(orderId);
        assertEquals(20, orderId.length());
        assertTrue(orderId.matches("\\d{20}"));
    }

    @Test
    @DisplayName("测试 transactionId 方法 - 生成交易流水号")
    void testTransactionId() {
        String transactionId = IdUtil.transactionId();
        assertNotNull(transactionId);
        assertEquals(22, transactionId.length());
        assertTrue(transactionId.startsWith("T"));
    }

    @Test
    @DisplayName("测试 shortId 方法 - 生成短 ID")
    void testShortId() {
        String shortId = IdUtil.shortId();
        assertNotNull(shortId);
        assertEquals(8, shortId.length());
        assertTrue(shortId.matches("[A-Z0-9]{8}"));
    }

    @Test
    @DisplayName("测试 generateCode 方法 - 生成带前缀的唯一编号")
    void testGenerateCode() {
        String code = IdUtil.generateCode("CODE-");
        assertNotNull(code);
        assertTrue(code.startsWith("CODE-"));
        assertTrue(code.length() > 5);
    }

    @Test
    @DisplayName("测试 generatePrimaryKey 方法 - 生成数据库主键 ID")
    void testGeneratePrimaryKey() {
        long primaryKey = IdUtil.generatePrimaryKey();
        assertTrue(primaryKey > 0);
    }

    @Test
    @DisplayName("测试 nanoId 方法 - 生成 NanoID")
    void testNanoId() {
        String nanoId = IdUtil.nanoId();
        assertNotNull(nanoId);
        assertEquals(21, nanoId.length());
    }

    @Test
    @DisplayName("测试 randomString 方法 - 生成随机字符串")
    void testRandomString() {
        String randomString = IdUtil.randomString(10);
        assertNotNull(randomString);
        assertEquals(10, randomString.length());
        assertTrue(randomString.matches("[A-Za-z0-9]{10}"));
    }

    @Test
    @DisplayName("测试 randomNumber 方法 - 生成随机数字字符串")
    void testRandomNumber() {
        String randomNumber = IdUtil.randomNumber(6);
        assertNotNull(randomNumber);
        assertEquals(6, randomNumber.length());
        assertTrue(randomNumber.matches("\\d{6}"));
    }

    @Test
    @DisplayName("测试 verificationCode 方法 - 生成 4 位验证码")
    void testVerificationCode() {
        String code = IdUtil.verificationCode();
        assertNotNull(code);
        assertEquals(4, code.length());
        assertTrue(code.matches("\\d{4}"));
    }

    @Test
    @DisplayName("测试 verificationCode 方法 - 生成指定位数验证码")
    void testVerificationCodeWithLength() {
        String code = IdUtil.verificationCode(6);
        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}"));
    }

    @Test
    @DisplayName("测试 UUID 唯一性")
    void testUuidUniqueness() {
        String uuid1 = IdUtil.uuid();
        String uuid2 = IdUtil.uuid();
        assertNotEquals(uuid1, uuid2);
    }

    @Test
    @DisplayName("测试 timestampId 唯一性")
    void testTimestampIdUniqueness() {
        String id1 = IdUtil.timestampId();
        String id2 = IdUtil.timestampId();
        assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("测试批量生成雪花 ID 的唯一性")
    void testBatchSnowflakeUniqueness() {
        long[] ids = new long[1000];
        for (int i = 0; i < 1000; i++) {
            ids[i] = IdUtil.snowflake();
        }
        
        // 检查是否有重复
        for (int i = 0; i < ids.length; i++) {
            for (int j = i + 1; j < ids.length; j++) {
                assertNotEquals(ids[i], ids[j], "发现重复的雪花 ID");
            }
        }
    }
}
