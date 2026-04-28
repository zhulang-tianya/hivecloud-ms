package com.hivecloud.common.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DateUtil 单元测试类
 *
 * @author HiveCloud Team
 * @date 2026-04-28
 */
@DisplayName("DateUtil 工具类测试")
class DateUtilTest {

    @Test
    @DisplayName("测试 now 方法 - 获取当前 LocalDateTime")
    void testNow() {
        LocalDateTime now = DateUtil.now();
        assertNotNull(now);
        assertTrue(now instanceof LocalDateTime);
    }

    @Test
    @DisplayName("测试 today 方法 - 获取当前 LocalDate")
    void testToday() {
        LocalDate today = DateUtil.today();
        assertNotNull(today);
        assertEquals(LocalDate.now(), today);
    }

    @Test
    @DisplayName("测试 currentTime 方法 - 获取当前 LocalTime")
    void testCurrentTime() {
        LocalTime currentTime = DateUtil.currentTime();
        assertNotNull(currentTime);
        assertTrue(currentTime instanceof LocalTime);
    }

    @Test
    @DisplayName("测试 toDate 方法 - LocalDateTime 转 Date")
    void testToDateFromLocalDateTime() {
        LocalDateTime localDateTime = LocalDateTime.of(2026, 4, 28, 10, 30, 0);
        Date date = DateUtil.toDate(localDateTime);
        assertNotNull(date);
        assertEquals(localDateTime, DateUtil.toLocalDateTime(date));
    }

    @Test
    @DisplayName("测试 toDate 方法 - null 值处理")
    void testToDateNull() {
        assertNull(DateUtil.toDate((LocalDateTime) null));
    }

    @Test
    @DisplayName("测试 toLocalDateTime 方法 - Date 转 LocalDateTime")
    void testToLocalDateTimeFromDate() {
        Date date = new Date();
        LocalDateTime localDateTime = DateUtil.toLocalDateTime(date);
        assertNotNull(localDateTime);
        assertEquals(date.toInstant(), DateUtil.toInstant(localDateTime));
    }

    @Test
    @DisplayName("测试 toLocalDateTime 方法 - Instant 转 LocalDateTime")
    void testToLocalDateTimeFromInstant() {
        java.time.Instant instant = java.time.Instant.now();
        LocalDateTime localDateTime = DateUtil.toLocalDateTime(instant);
        assertNotNull(localDateTime);
    }

    @Test
    @DisplayName("测试 toInstant 方法 - LocalDateTime 转 Instant")
    void testToInstant() {
        LocalDateTime localDateTime = LocalDateTime.now();
        java.time.Instant instant = DateUtil.toInstant(localDateTime);
        assertNotNull(instant);
    }

    @Test
    @DisplayName("测试 format 方法 - 格式化 LocalDateTime")
    void testFormat() {
        LocalDateTime localDateTime = LocalDateTime.of(2026, 4, 28, 14, 30, 45);
        String formatted = DateUtil.format(localDateTime);
        assertEquals("2026-04-28 14:30:45", formatted);
    }

    @Test
    @DisplayName("测试 format 方法 - 自定义格式")
    void testFormatWithPattern() {
        LocalDateTime localDateTime = LocalDateTime.of(2026, 4, 28, 14, 30, 45);
        String formatted = DateUtil.format(localDateTime, "yyyy/MM/dd HH:mm");
        assertEquals("2026/04/28 14:30", formatted);
    }

    @Test
    @DisplayName("测试 parse 方法 - 解析字符串")
    void testParse() {
        String text = "2026-04-28 14:30:45";
        LocalDateTime localDateTime = DateUtil.parse(text);
        assertNotNull(localDateTime);
        assertEquals(2026, localDateTime.getYear());
        assertEquals(4, localDateTime.getMonthValue());
        assertEquals(28, localDateTime.getDayOfMonth());
        assertEquals(14, localDateTime.getHour());
        assertEquals(30, localDateTime.getMinute());
        assertEquals(45, localDateTime.getSecond());
    }

    @Test
    @DisplayName("测试 parse 方法 - 自定义格式解析")
    void testParseWithPattern() {
        String text = "2026/04/28 14:30";
        LocalDateTime localDateTime = DateUtil.parse(text, "yyyy/MM/dd HH:mm");
        assertNotNull(localDateTime);
        assertEquals(2026, localDateTime.getYear());
        assertEquals(14, localDateTime.getHour());
    }

    @Test
    @DisplayName("测试 plusDays 方法 - 增加天数")
    void testPlusDays() {
        LocalDateTime localDateTime = LocalDateTime.of(2026, 4, 28, 10, 0);
        LocalDateTime result = DateUtil.plusDays(localDateTime, 7);
        assertEquals(2026, result.getYear());
        assertEquals(5, result.getMonthValue());
        assertEquals(5, result.getDayOfMonth());
    }

    @Test
    @DisplayName("测试 plusMonths 方法 - 增加月数")
    void testPlusMonths() {
        LocalDateTime localDateTime = LocalDateTime.of(2026, 4, 28, 10, 0);
        LocalDateTime result = DateUtil.plusMonths(localDateTime, 3);
        assertEquals(2026, result.getYear());
        assertEquals(7, result.getMonthValue());
        assertEquals(28, result.getDayOfMonth());
    }

    @Test
    @DisplayName("测试 plusYears 方法 - 增加年数")
    void testPlusYears() {
        LocalDateTime localDateTime = LocalDateTime.of(2026, 4, 28, 10, 0);
        LocalDateTime result = DateUtil.plusYears(localDateTime, 1);
        assertEquals(2027, result.getYear());
        assertEquals(4, result.getMonthValue());
        assertEquals(28, result.getDayOfMonth());
    }

    @Test
    @DisplayName("测试 minusDays 方法 - 减少天数")
    void testMinusDays() {
        LocalDateTime localDateTime = LocalDateTime.of(2026, 4, 28, 10, 0);
        LocalDateTime result = DateUtil.minusDays(localDateTime, 5);
        assertEquals(2026, result.getYear());
        assertEquals(4, result.getMonthValue());
        assertEquals(23, result.getDayOfMonth());
    }

    @Test
    @DisplayName("测试 daysBetween 方法 - 计算天数差")
    void testDaysBetween() {
        LocalDate start = LocalDate.of(2026, 4, 1);
        LocalDate end = LocalDate.of(2026, 4, 28);
        long days = DateUtil.daysBetween(start, end);
        assertEquals(27, days);
    }

    @Test
    @DisplayName("测试 hoursBetween 方法 - 计算小时差")
    void testHoursBetween() {
        LocalDateTime start = LocalDateTime.of(2026, 4, 28, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 4, 28, 15, 30);
        long hours = DateUtil.hoursBetween(start, end);
        assertEquals(5, hours);
    }

    @Test
    @DisplayName("测试 isLeapYear 方法 - 闰年判断")
    void testIsLeapYear() {
        assertTrue(DateUtil.isLeapYear(2024));
        assertFalse(DateUtil.isLeapYear(2023));
        assertTrue(DateUtil.isLeapYear(2000));
        assertFalse(DateUtil.isLeapYear(1900));
    }

    @Test
    @DisplayName("测试 getDaysInMonth 方法 - 获取月份天数")
    void testGetDaysInMonth() {
        assertEquals(31, DateUtil.getDaysInMonth(2026, 1));
        assertEquals(28, DateUtil.getDaysInMonth(2026, 2));
        assertEquals(29, DateUtil.getDaysInMonth(2024, 2));
        assertEquals(30, DateUtil.getDaysInMonth(2026, 4));
    }

    @Test
    @DisplayName("测试 getDayOfWeek 方法 - 获取星期几")
    void testGetDayOfWeek() {
        LocalDate date = LocalDate.of(2026, 4, 28);
        int dayOfWeek = DateUtil.getDayOfWeek(date);
        assertEquals(2, dayOfWeek); // 星期二
    }

    @Test
    @DisplayName("测试 getDayOfWeekChinese 方法 - 获取中文星期")
    void testGetDayOfWeekChinese() {
        LocalDate date = LocalDate.of(2026, 4, 28);
        String dayOfWeek = DateUtil.getDayOfWeekChinese(date);
        assertEquals("星期二", dayOfWeek);
    }

    @Test
    @DisplayName("测试 isToday 方法 - 判断是否今天")
    void testIsToday() {
        assertTrue(DateUtil.isToday(LocalDate.now()));
        assertFalse(DateUtil.isToday(LocalDate.of(2026, 4, 27)));
    }

    @Test
    @DisplayName("测试 isYesterday 方法 - 判断是否昨天")
    void testIsYesterday() {
        assertTrue(DateUtil.isYesterday(LocalDate.now().minusDays(1)));
        assertFalse(DateUtil.isYesterday(LocalDate.now()));
    }

    @Test
    @DisplayName("测试 getCurrentTimestamp 方法 - 获取当前时间戳")
    void testGetCurrentTimestamp() {
        long timestamp = DateUtil.getCurrentTimestamp();
        assertTrue(timestamp > 0);
        assertTrue(timestamp <= System.currentTimeMillis());
    }

    @Test
    @DisplayName("测试 timestampToLocalDateTime 方法 - 时间戳转 LocalDateTime")
    void testTimestampToLocalDateTime() {
        long timestamp = 1682668245000L;
        LocalDateTime localDateTime = DateUtil.timestampToLocalDateTime(timestamp);
        assertNotNull(localDateTime);
        assertEquals(2023, localDateTime.getYear());
    }

    @Test
    @DisplayName("测试 localDateTimeToTimestamp 方法 - LocalDateTime 转时间戳")
    void testLocalDateTimeToTimestamp() {
        LocalDateTime localDateTime = LocalDateTime.of(2026, 4, 28, 10, 30, 0);
        long timestamp = DateUtil.localDateTimeToTimestamp(localDateTime);
        assertTrue(timestamp > 0);
    }

    @Test
    @DisplayName("测试 null 值处理")
    void testNullHandling() {
        assertNull(DateUtil.toDate((LocalDateTime) null));
        assertNull(DateUtil.toLocalDateTime((Date) null));
        assertNull(DateUtil.format(null));
        assertNull(DateUtil.parse(null));
        assertNull(DateUtil.plusDays(null, 1));
        assertNull(DateUtil.minusDays(null, 1));
    }
}
