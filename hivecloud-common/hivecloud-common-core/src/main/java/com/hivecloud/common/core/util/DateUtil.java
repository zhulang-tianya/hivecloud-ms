package com.hivecloud.common.core.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 日期时间工具类
 * <p>
 * 提供 Java 8 Date/Time API 的便捷方法，支持 Date、LocalDateTime、Instant 之间的转换
 * 提供日期格式化、解析、计算等常用功能
 * </p>
 *
 * @author HiveCloud Team
 * @date 2026-04-28
 * @since 1.0.0
 */
public class DateUtil {

    /**
     * 默认日期时间格式
     */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 默认时间格式
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    private DateUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 获取当前 LocalDateTime
     *
     * @return 当前 LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前日期（ LocalDate）
     *
     * @return 当前 LocalDate
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * 获取当前时间（ LocalTime）
     *
     * @return 当前 LocalTime
     */
    public static LocalTime currentTime() {
        return LocalTime.now();
    }

    /**
     * LocalDateTime 转 Date
     *
     * @param localDateTime LocalDateTime
     * @return Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * Date 转 LocalDateTime
     *
     * @param date Date
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Instant 转 LocalDateTime
     *
     * @param instant Instant
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * LocalDateTime 转 Instant
     *
     * @param localDateTime LocalDateTime
     * @return Instant
     */
    public static Instant toInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    /**
     * Date 转 Instant
     *
     * @param date Date
     * @return Instant
     */
    public static Instant toInstant(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant();
    }

    /**
     * Instant 转 Date
     *
     * @param instant Instant
     * @return Date
     */
    public static Date toDate(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Date.from(instant);
    }

    /**
     * 格式化 LocalDateTime 为字符串（默认格式）
     *
     * @param localDateTime LocalDateTime
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime localDateTime) {
        return format(localDateTime, DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * 格式化 LocalDateTime 为字符串（指定格式）
     *
     * @param localDateTime LocalDateTime
     * @param pattern 日期格式
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime localDateTime, String pattern) {
        if (localDateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(formatter);
    }

    /**
     * 解析字符串为 LocalDateTime（默认格式）
     *
     * @param text 日期时间字符串
     * @return LocalDateTime
     */
    public static LocalDateTime parse(String text) {
        return parse(text, DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * 解析字符串为 LocalDateTime（指定格式）
     *
     * @param text 日期时间字符串
     * @param pattern 日期格式
     * @return LocalDateTime
     */
    public static LocalDateTime parse(String text, String pattern) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(text, formatter);
    }

    /**
     * 增加年数
     *
     * @param localDateTime LocalDateTime
     * @param years 年数
     * @return 增加后的 LocalDateTime
     */
    public static LocalDateTime plusYears(LocalDateTime localDateTime, long years) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.plusYears(years);
    }

    /**
     * 增加月数
     *
     * @param localDateTime LocalDateTime
     * @param months 月数
     * @return 增加后的 LocalDateTime
     */
    public static LocalDateTime plusMonths(LocalDateTime localDateTime, long months) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.plusMonths(months);
    }

    /**
     * 增加天数
     *
     * @param localDateTime LocalDateTime
     * @param days 天数
     * @return 增加后的 LocalDateTime
     */
    public static LocalDateTime plusDays(LocalDateTime localDateTime, long days) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.plusDays(days);
    }

    /**
     * 增加小时数
     *
     * @param localDateTime LocalDateTime
     * @param hours 小时数
     * @return 增加后的 LocalDateTime
     */
    public static LocalDateTime plusHours(LocalDateTime localDateTime, long hours) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.plusHours(hours);
    }

    /**
     * 增加分钟数
     *
     * @param localDateTime LocalDateTime
     * @param minutes 分钟数
     * @return 增加后的 LocalDateTime
     */
    public static LocalDateTime plusMinutes(LocalDateTime localDateTime, long minutes) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.plusMinutes(minutes);
    }

    /**
     * 增加秒数
     *
     * @param localDateTime LocalDateTime
     * @param seconds 秒数
     * @return 增加后的 LocalDateTime
     */
    public static LocalDateTime plusSeconds(LocalDateTime localDateTime, long seconds) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.plusSeconds(seconds);
    }

    /**
     * 减少年数
     *
     * @param localDateTime LocalDateTime
     * @param years 年数
     * @return 减少后的 LocalDateTime
     */
    public static LocalDateTime minusYears(LocalDateTime localDateTime, long years) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.minusYears(years);
    }

    /**
     * 减少月数
     *
     * @param localDateTime LocalDateTime
     * @param months 月数
     * @return 减少后的 LocalDateTime
     */
    public static LocalDateTime minusMonths(LocalDateTime localDateTime, long months) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.minusMonths(months);
    }

    /**
     * 减少天数
     *
     * @param localDateTime LocalDateTime
     * @param days 天数
     * @return 减少后的 LocalDateTime
     */
    public static LocalDateTime minusDays(LocalDateTime localDateTime, long days) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.minusDays(days);
    }

    /**
     * 计算两个日期之间的天数差
     *
     * @param start 开始日期
     * @param end 结束日期
     * @return 天数差
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算两个日期之间的小时数差
     *
     * @param start 开始时间
     * @param end 结束时间
     * @return 小时数差
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * 计算两个日期之间的分钟数差
     *
     * @param start 开始时间
     * @param end 结束时间
     * @return 分钟数差
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * 计算两个日期之间的秒数差
     *
     * @param start 开始时间
     * @param end 结束时间
     * @return 秒数差
     */
    public static long secondsBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.SECONDS.between(start, end);
    }

    /**
     * 判断是否为闰年
     *
     * @param year 年份
     * @return 闰年返回 true，否则返回 false
     */
    public static boolean isLeapYear(int year) {
        return Year.isLeap(year);
    }

    /**
     * 获取指定年份的天数
     *
     * @param year 年份
     * @return 天数
     */
    public static int getDaysInYear(int year) {
        return isLeapYear(year) ? 366 : 365;
    }

    /**
     * 获取指定月份的天数
     *
     * @param year 年份
     * @param month 月份（1-12）
     * @return 天数
     */
    public static int getDaysInMonth(int year, int month) {
        return YearMonth.of(year, month).lengthOfMonth();
    }

    /**
     * 获取指定日期是星期几
     *
     * @param localDate 日期
     * @return 星期几（1-7，1 表示星期一）
     */
    public static int getDayOfWeek(LocalDate localDate) {
        if (localDate == null) {
            return 0;
        }
        return localDate.getDayOfWeek().getValue();
    }

    /**
     * 获取指定日期是星期几（中文）
     *
     * @param localDate 日期
     * @return 星期几（星期一、星期二...）
     */
    public static String getDayOfWeekChinese(LocalDate localDate) {
        if (localDate == null) {
            return "";
        }
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        return weekDays[localDate.getDayOfWeek().getValue() % 7];
    }

    /**
     * 判断日期是否在今天
     *
     * @param localDate 日期
     * @return 在今天返回 true，否则返回 false
     */
    public static boolean isToday(LocalDate localDate) {
        if (localDate == null) {
            return false;
        }
        return localDate.equals(LocalDate.now());
    }

    /**
     * 判断日期是否在昨天
     *
     * @param localDate 日期
     * @return 在昨天返回 true，否则返回 false
     */
    public static boolean isYesterday(LocalDate localDate) {
        if (localDate == null) {
            return false;
        }
        return localDate.equals(LocalDate.now().minusDays(1));
    }

    /**
     * 判断日期是否在当前年份
     *
     * @param localDate 日期
     * @return 在当前年份返回 true，否则返回 false
     */
    public static boolean isCurrentYear(LocalDate localDate) {
        if (localDate == null) {
            return false;
        }
        return localDate.getYear() == LocalDate.now().getYear();
    }

    /**
     * 判断日期是否在当前月份
     *
     * @param localDate 日期
     * @return 在当前月份返回 true，否则返回 false
     */
    public static boolean isCurrentMonth(LocalDate localDate) {
        if (localDate == null) {
            return false;
        }
        LocalDate now = LocalDate.now();
        return localDate.getYear() == now.getYear() && localDate.getMonthValue() == now.getMonthValue();
    }

    /**
     * 获取当前时间戳（毫秒）
     *
     * @return 时间戳
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间戳（秒）
     *
     * @return 时间戳（秒）
     */
    public static long getCurrentTimestampSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 时间戳转 LocalDateTime（毫秒）
     *
     * @param timestamp 时间戳（毫秒）
     * @return LocalDateTime
     */
    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    /**
     * LocalDateTime 转时间戳（毫秒）
     *
     * @param localDateTime LocalDateTime
     * @return 时间戳（毫秒）
     */
    public static long localDateTimeToTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return 0;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
