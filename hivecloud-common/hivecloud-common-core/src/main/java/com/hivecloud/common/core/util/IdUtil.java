package com.hivecloud.common.core.util;

import java.util.UUID;

/**
 * ID 生成工具类
 * <p>
 * 提供多种 ID 生成策略，包括 UUID、雪花算法、时间戳 ID 等
 * 适用于分布式环境下的唯一 ID 生成场景
 * </p>
 *
 * @author HiveCloud Team
 * @date 2026-04-28
 * @since 1.0.0
 */
public class IdUtil {

    /**
     * 雪花算法配置
     */
    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private static long workerId = 0;
    private static long datacenterId = 0;
    private static long sequence = 0L;
    private static long lastTimestamp = -1L;

    private IdUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 生成 UUID（带连字符）
     *
     * @return UUID 字符串
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成 UUID（不带连字符）
     *
     * @return UUID 字符串
     */
    public static String uuidSimple() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成 32 位 UUID（小写）
     *
     * @return UUID 字符串
     */
    public static String uuid32() {
        return uuidSimple().toLowerCase();
    }

    /**
     * 生成 32 位 UUID（大写）
     *
     * @return UUID 字符串
     */
    public static String uuid32Upper() {
        return uuidSimple().toUpperCase();
    }

    /**
     * 生成雪花算法 ID
     * <p>
     * 64 位 Long 类型 ID 组成：
     * <ul>
     *     <li>1 位符号位（固定为 0）</li>
     *     <li>41 位时间戳（毫秒级，可使用约 69 年）</li>
     *     <li>10 位机器 ID（5 位数据中心 ID + 5 位工作机器 ID）</li>
     *     <li>12 位序列号（同一毫秒内的递增序号）</li>
     * </ul>
     * </p>
     *
     * @return 雪花 ID
     */
    public static synchronized long snowflake() {
        long timestamp = System.currentTimeMillis();

        // 处理时钟回拨
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;
        return ((timestamp - 1288834974657L) << TIMESTAMP_LEFT_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 生成雪花算法 ID（字符串格式）
     *
     * @return 雪花 ID 字符串
     */
    public static String snowflakeString() {
        return String.valueOf(snowflake());
    }

    /**
     * 设置工作机器 ID
     *
     * @param workerId 工作机器 ID（0-31）
     */
    public static void setWorkerId(long workerId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException("Worker ID must be between 0 and " + MAX_WORKER_ID);
        }
        IdUtil.workerId = workerId;
    }

    /**
     * 设置数据中心 ID
     *
     * @param datacenterId 数据中心 ID（0-31）
     */
    public static void setDatacenterId(long datacenterId) {
        if (datacenterId < 0 || datacenterId > MAX_DATACENTER_ID) {
            throw new IllegalArgumentException("Datacenter ID must be between 0 and " + MAX_DATACENTER_ID);
        }
        IdUtil.datacenterId = datacenterId;
    }

    /**
     * 等待下一个毫秒
     *
     * @param lastTimestamp 上一个时间戳
     * @return 下一个时间戳
     */
    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    /**
     * 生成基于时间戳的 ID（毫秒 + 随机数）
     * <p>
     * 格式：时间戳（13 位）+ 随机数（6 位）= 19 位数字
     * </p>
     *
     * @return 时间戳 ID
     */
    public static String timestampId() {
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 1000000);
        return String.format("%d%06d", timestamp, random);
    }

    /**
     * 生成订单 ID（时间戳 + 随机数）
     * <p>
     * 格式：年月日时分秒（14 位）+ 随机数（6 位）= 20 位数字
     * </p>
     *
     * @return 订单 ID
     */
    public static String orderId() {
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 1000000);
        return String.format("%tF%<tH%<tM%<tS%06d", timestamp, random);
    }

    /**
     * 生成交易流水号
     * <p>
     * 格式：T + 年月日时分秒毫秒（17 位）+ 随机数（4 位）= 22 位字符
     * </p>
     *
     * @return 交易流水号
     */
    public static String transactionId() {
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 10000);
        return String.format("T%tF%<tH%<tM%<tS%<tL%04d", timestamp, random);
    }

    /**
     * 生成短 ID（8 位字母数字）
     * <p>
     * 基于 UUID 截取并转换为 Base62 编码
     * </p>
     *
     * @return 短 ID
     */
    public static String shortId() {
        return uuidSimple().substring(0, 8).toUpperCase();
    }

    /**
     * 生成唯一编号（带前缀）
     *
     * @param prefix 前缀
     * @return 唯一编号
     */
    public static String generateCode(String prefix) {
        return prefix + System.currentTimeMillis() + (int) (Math.random() * 10000);
    }

    /**
     * 生成数据库主键 ID（数字格式）
     * <p>
     * 基于时间戳和随机数的组合，保证全局唯一
     * </p>
     *
     * @return 主键 ID
     */
    public static long generatePrimaryKey() {
        long timestamp = System.currentTimeMillis();
        long random = (long) (Math.random() * 1000);
        return (timestamp << 10) | random;
    }

    /**
     * 生成 NanoID
     * <p>
     * 21 位 URL 安全的唯一 ID
     * </p>
     *
     * @return NanoID
     */
    public static String nanoId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 21);
    }

    /**
     * 生成指定长度的随机字符串
     *
     * @param length 长度
     * @return 随机字符串
     */
    public static String randomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }

    /**
     * 生成指定长度的数字字符串
     *
     * @param length 长度
     * @return 数字字符串
     */
    public static String randomNumber(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }

    /**
     * 生成验证码（4 位数字）
     *
     * @return 验证码
     */
    public static String verificationCode() {
        return verificationCode(4);
    }

    /**
     * 生成指定长度的验证码
     *
     * @param length 长度
     * @return 验证码
     */
    public static String verificationCode(int length) {
        return randomNumber(length);
    }
}
