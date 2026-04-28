package com.hivecloud.common.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * <p>
 * 提供常用的字符串操作方法，包括判空、格式化、转换等
 * </p>
 *
 * @author HiveCloud Team
 * @date 2026-04-28
 * @since 1.0.0
 */
public class StringUtil {

    /**
     * 空字符串
     */
    public static final String EMPTY = "";

    /**
     * 空白字符（空格、制表符、换行等）
     */
    public static final String BLANK = " \t\n\r\f";

    /**
     * 逗号分隔符
     */
    public static final String COMMA = ",";

    /**
     * 分号分隔符
     */
    public static final String SEMICOLON = ";";

    /**
     * 冒号分隔符
     */
    public static final String COLON = ":";

    /**
     * 下划线
     */
    public static final String UNDERLINE = "_";

    /**
     * 中划线
     */
    public static final String DASH = "-";

    /**
     * 点号
     */
    public static final String DOT = ".";

    /**
     * 斜杠
     */
    public static final String SLASH = "/";

    /**
     * 反斜杠
     */
    public static final String BACKSLASH = "\\";

    /**
     * 邮箱正则表达式
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");

    /**
     * 手机号正则表达式
     */
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 身份证号正则表达式
     */
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$");

    /**
     * URL 正则表达式
     */
    private static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");

    /**
     * IPv4 正则表达式
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile("^(\\d{1,3}\\.){3}\\d{1,3}$");

    private StringUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 判断字符串是否为空
     *
     * @param str 字符串
     * @return 空返回 true，否则返回 false
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str 字符串
     * @return 不为空返回 true，否则返回 false
     */
    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空白（null、空字符串或只包含空白符）
     *
     * @param str 字符串
     * @return 空白返回 true，否则返回 false
     */
    public static boolean isBlank(CharSequence str) {
        if (str == null) {
            return true;
        }
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否不为空白
     *
     * @param str 字符串
     * @return 不为空白返回 true，否则返回 false
     */
    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }

    /**
     * 判断字符串数组中是否有空字符串
     *
     * @param strs 字符串数组
     * @return 有空返回 true，否则返回 false
     */
    public static boolean hasEmpty(CharSequence... strs) {
        if (strs == null || strs.length == 0) {
            return true;
        }
        for (CharSequence str : strs) {
            if (isEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串数组中是否有空白字符串
     *
     * @param strs 字符串数组
     * @return 有空白返回 true，否则返回 false
     */
    public static boolean hasBlank(CharSequence... strs) {
        if (strs == null || strs.length == 0) {
            return true;
        }
        for (CharSequence str : strs) {
            if (isBlank(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 去除字符串首尾空白
     *
     * @param str 字符串
     * @return 去除空白后的字符串
     */
    public static String trim(CharSequence str) {
        return str == null ? null : str.toString().trim();
    }

    /**
     * 去除字符串首尾空白，空字符串返回空字符串
     *
     * @param str 字符串
     * @return 去除空白后的字符串
     */
    public static String trimToEmpty(CharSequence str) {
        return str == null ? EMPTY : str.toString().trim();
    }

    /**
     * 去除字符串首尾空白，空白字符串返回 null
     *
     * @param str 字符串
     * @return 去除空白后的字符串
     */
    public static String trimToNull(CharSequence str) {
        if (str == null) {
            return null;
        }
        String trimmed = str.toString().trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * 字符串转小写
     *
     * @param str 字符串
     * @return 小写字符串
     */
    public static String toLowerCase(CharSequence str) {
        return str == null ? null : str.toString().toLowerCase();
    }

    /**
     * 字符串转大写
     *
     * @param str 字符串
     * @return 大写字符串
     */
    public static String toUpperCase(CharSequence str) {
        return str == null ? null : str.toString().toUpperCase();
    }

    /**
     * 首字母大写
     *
     * @param str 字符串
     * @return 首字母大写后的字符串
     */
    public static String capitalize(CharSequence str) {
        if (isEmpty(str)) {
            return str == null ? null : EMPTY;
        }
        return Character.toUpperCase(str.charAt(0)) + str.toString().substring(1);
    }

    /**
     * 首字母小写
     *
     * @param str 字符串
     * @return 首字母小写后的字符串
     */
    public static String uncapitalize(CharSequence str) {
        if (isEmpty(str)) {
            return str == null ? null : EMPTY;
        }
        return Character.toLowerCase(str.charAt(0)) + str.toString().substring(1);
    }

    /**
     * 驼峰转下划线
     * <p>
     * userName -> user_name
     * </p>
     *
     * @param str 驼峰字符串
     * @return 下划线字符串
     */
    public static String camelToUnderline(CharSequence str) {
        if (isEmpty(str)) {
            return str == null ? null : EMPTY;
        }
        String camelCase = str.toString();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append(UNDERLINE);
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 下划线转驼峰
     * <p>
     * user_name -> userName
     * </p>
     *
     * @param str 下划线字符串
     * @return 驼峰字符串
     */
    public static String underlineToCamel(CharSequence str) {
        if (isEmpty(str)) {
            return str == null ? null : EMPTY;
        }
        String underlineCase = str.toString();
        StringBuilder sb = new StringBuilder();
        boolean nextUpperCase = false;
        for (int i = 0; i < underlineCase.length(); i++) {
            char c = underlineCase.charAt(i);
            if (c == UNDERLINE.charAt(0)) {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    sb.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 字符串格式化
     * <p>
     * 使用 {} 作为占位符，支持多个参数
     * </p>
     *
     * @param template 模板字符串
     * @param params 参数
     * @return 格式化后的字符串
     */
    public static String format(CharSequence template, Object... params) {
        if (isEmpty(template)) {
            return template == null ? null : EMPTY;
        }
        if (params == null || params.length == 0) {
            return template.toString();
        }
        String templateStr = template.toString();
        StringBuilder sb = new StringBuilder();
        int paramIndex = 0;
        for (int i = 0; i < templateStr.length(); i++) {
            char c = templateStr.charAt(i);
            if (c == '{' && i + 1 < templateStr.length() && templateStr.charAt(i + 1) == '}') {
                if (paramIndex < params.length) {
                    sb.append(params[paramIndex]);
                    paramIndex++;
                    i++;
                } else {
                    sb.append("{}");
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 判断字符串是否包含指定子串
     *
     * @param str 字符串
     * @param substring 子串
     * @return 包含返回 true，否则返回 false
     */
    public static boolean contains(CharSequence str, CharSequence substring) {
        if (str == null || substring == null) {
            return false;
        }
        return str.toString().contains(substring);
    }

    /**
     * 判断字符串是否以指定前缀开头
     *
     * @param str 字符串
     * @param prefix 前缀
     * @return 是返回 true，否则返回 false
     */
    public static boolean startsWith(CharSequence str, CharSequence prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        return str.toString().startsWith(prefix.toString());
    }

    /**
     * 判断字符串是否以指定后缀结尾
     *
     * @param str 字符串
     * @param suffix 后缀
     * @return 是返回 true，否则返回 false
     */
    public static boolean endsWith(CharSequence str, CharSequence suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        return str.toString().endsWith(suffix.toString());
    }

    /**
     * 验证邮箱格式
     *
     * @param email 邮箱地址
     * @return 格式正确返回 true，否则返回 false
     */
    public static boolean isEmail(CharSequence email) {
        if (isEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 验证手机号格式
     *
     * @param mobile 手机号
     * @return 格式正确返回 true，否则返回 false
     */
    public static boolean isMobile(CharSequence mobile) {
        if (isEmpty(mobile)) {
            return false;
        }
        return MOBILE_PATTERN.matcher(mobile).matches();
    }

    /**
     * 验证身份证号格式
     *
     * @param idCard 身份证号
     * @return 格式正确返回 true，否则返回 false
     */
    public static boolean isIdCard(CharSequence idCard) {
        if (isEmpty(idCard)) {
            return false;
        }
        return ID_CARD_PATTERN.matcher(idCard).matches();
    }

    /**
     * 验证 URL 格式
     *
     * @param url URL 地址
     * @return 格式正确返回 true，否则返回 false
     */
    public static boolean isUrl(CharSequence url) {
        if (isEmpty(url)) {
            return false;
        }
        return URL_PATTERN.matcher(url).matches();
    }

    /**
     * 验证 IPv4 地址格式
     *
     * @param ip IP 地址
     * @return 格式正确返回 true，否则返回 false
     */
    public static boolean isIpv4(CharSequence ip) {
        if (isEmpty(ip)) {
            return false;
        }
        if (!IPV4_PATTERN.matcher(ip).matches()) {
            return false;
        }
        String[] parts = ip.toString().split("\\.");
        for (String part : parts) {
            int value = Integer.parseInt(part);
            if (value < 0 || value > 255) {
                return false;
            }
        }
        return true;
    }

    /**
     * 重复字符串指定次数
     *
     * @param str 字符串
     * @param times 重复次数
     * @return 重复后的字符串
     */
    public static String repeat(CharSequence str, int times) {
        if (isEmpty(str) || times <= 0) {
            return EMPTY;
        }
        StringBuilder sb = new StringBuilder(str.length() * times);
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 字符串右补全（空格）
     *
     * @param str 字符串
     * @param length 总长度
     * @return 补全后的字符串
     */
    public static String padRight(CharSequence str, int length) {
        return padRight(str, length, ' ');
    }

    /**
     * 字符串右补全（指定字符）
     *
     * @param str 字符串
     * @param length 总长度
     * @param padChar 填充字符
     * @return 补全后的字符串
     */
    public static String padRight(CharSequence str, int length, char padChar) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < length) {
            sb.append(padChar);
        }
        return sb.toString();
    }

    /**
     * 字符串左补全（空格）
     *
     * @param str 字符串
     * @param length 总长度
     * @return 补全后的字符串
     */
    public static String padLeft(CharSequence str, int length) {
        return padLeft(str, length, ' ');
    }

    /**
     * 字符串左补全（指定字符）
     *
     * @param str 字符串
     * @param length 总长度
     * @param padChar 填充字符
     * @return 补全后的字符串
     */
    public static String padLeft(CharSequence str, int length, char padChar) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() + str.length() < length) {
            sb.append(padChar);
        }
        sb.append(str);
        return sb.toString();
    }

    /**
     * 截取字符串
     *
     * @param str 字符串
     * @param start 起始位置
     * @return 截取后的字符串
     */
    public static String sub(CharSequence str, int start) {
        if (isEmpty(str)) {
            return str == null ? null : EMPTY;
        }
        return str.toString().substring(start);
    }

    /**
     * 截取字符串
     *
     * @param str 字符串
     * @param start 起始位置
     * @param end 结束位置
     * @return 截取后的字符串
     */
    public static String sub(CharSequence str, int start, int end) {
        if (isEmpty(str)) {
            return str == null ? null : EMPTY;
        }
        return str.toString().substring(start, end);
    }

    /**
     * 截取字符串（支持负数索引）
     *
     * @param str 字符串
     * @param start 起始位置（负数表示从后往前）
     * @param end 结束位置（负数表示从后往前）
     * @return 截取后的字符串
     */
    public static String subWithNegative(CharSequence str, int start, int end) {
        if (isEmpty(str)) {
            return str == null ? null : EMPTY;
        }
        int length = str.length();
        if (start < 0) {
            start = length + start;
        }
        if (end < 0) {
            end = length + end;
        }
        return str.toString().substring(start, end);
    }

    /**
     * 删除字符串中的所有空白字符
     *
     * @param str 字符串
     * @return 删除空白后的字符串
     */
    public static String removeAllWhitespace(CharSequence str) {
        if (isEmpty(str)) {
            return str == null ? null : EMPTY;
        }
        return str.toString().replaceAll("\\s+", EMPTY);
    }

    /**
     * 转义 HTML 特殊字符
     *
     * @param str 字符串
     * @return 转义后的字符串
     */
    public static String escapeHtml(CharSequence str) {
        if (isEmpty(str)) {
            return str == null ? null : EMPTY;
        }
        return str.toString()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * 反转义 HTML 特殊字符
     *
     * @param str 字符串
     * @return 反转义后的字符串
     */
    public static String unescapeHtml(CharSequence str) {
        if (isEmpty(str)) {
            return str == null ? null : EMPTY;
        }
        return str.toString()
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'");
    }

    /**
     * 连接字符串数组
     *
     * @param delimiter 分隔符
     * @param elements 元素数组
     * @return 连接后的字符串
     */
    public static String join(CharSequence delimiter, CharSequence... elements) {
        if (elements == null || elements.length == 0) {
            return EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elements.length; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(elements[i]);
        }
        return sb.toString();
    }

    /**
     * 分割字符串
     *
     * @param str 字符串
     * @param delimiter 分隔符
     * @return 分割后的字符串数组
     */
    public static String[] split(CharSequence str, CharSequence delimiter) {
        if (isEmpty(str)) {
            return new String[0];
        }
        return str.toString().split(delimiter.toString());
    }
}
