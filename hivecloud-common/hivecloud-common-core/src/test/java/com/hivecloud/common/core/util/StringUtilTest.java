package com.hivecloud.common.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StringUtil 单元测试类
 *
 * @author HiveCloud Team
 * @date 2026-04-28
 */
@DisplayName("StringUtil 工具类测试")
class StringUtilTest {

    @Test
    @DisplayName("测试 isEmpty 方法 - 判断空字符串")
    void testIsEmpty() {
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty(""));
        assertFalse(StringUtil.isEmpty(" "));
        assertFalse(StringUtil.isEmpty("abc"));
    }

    @Test
    @DisplayName("测试 isNotEmpty 方法 - 判断非空字符串")
    void testIsNotEmpty() {
        assertFalse(StringUtil.isNotEmpty(null));
        assertFalse(StringUtil.isNotEmpty(""));
        assertTrue(StringUtil.isNotEmpty(" "));
        assertTrue(StringUtil.isNotEmpty("abc"));
    }

    @Test
    @DisplayName("测试 isBlank 方法 - 判断空白字符串")
    void testIsBlank() {
        assertTrue(StringUtil.isBlank(null));
        assertTrue(StringUtil.isBlank(""));
        assertTrue(StringUtil.isBlank(" "));
        assertTrue(StringUtil.isBlank("\t\n"));
        assertFalse(StringUtil.isBlank("abc"));
    }

    @Test
    @DisplayName("测试 isNotBlank 方法 - 判断非空白字符串")
    void testIsNotBlank() {
        assertFalse(StringUtil.isNotBlank(null));
        assertFalse(StringUtil.isNotBlank(""));
        assertFalse(StringUtil.isNotBlank(" "));
        assertTrue(StringUtil.isNotBlank("abc"));
    }

    @Test
    @DisplayName("测试 hasEmpty 方法 - 判断数组中是否有空字符串")
    void testHasEmpty() {
        assertTrue(StringUtil.hasEmpty(null, ""));
        assertTrue(StringUtil.hasEmpty("abc", "", "def"));
        assertFalse(StringUtil.hasEmpty("abc", "def"));
    }

    @Test
    @DisplayName("测试 hasBlank 方法 - 判断数组中是否有空白字符串")
    void testHasBlank() {
        assertTrue(StringUtil.hasBlank(null, " "));
        assertTrue(StringUtil.hasBlank("abc", " ", "def"));
        assertFalse(StringUtil.hasBlank("abc", "def"));
    }

    @Test
    @DisplayName("测试 trim 方法 - 去除首尾空白")
    void testTrim() {
        assertEquals("abc", StringUtil.trim("  abc  "));
        assertEquals("abc", StringUtil.trim("abc"));
        assertNull(StringUtil.trim(null));
    }

    @Test
    @DisplayName("测试 trimToEmpty 方法 - 去除空白返回空字符串")
    void testTrimToEmpty() {
        assertEquals("abc", StringUtil.trimToEmpty("  abc  "));
        assertEquals("", StringUtil.trimToEmpty(null));
        assertEquals("", StringUtil.trimToEmpty("   "));
    }

    @Test
    @DisplayName("测试 trimToNull 方法 - 去除空白返回 null")
    void testTrimToNull() {
        assertEquals("abc", StringUtil.trimToNull("  abc  "));
        assertNull(StringUtil.trimToNull(null));
        assertNull(StringUtil.trimToNull("   "));
    }

    @Test
    @DisplayName("测试 toLowerCase 方法 - 转小写")
    void testToLowerCase() {
        assertEquals("abc", StringUtil.toLowerCase("ABC"));
        assertEquals("abc", StringUtil.toLowerCase("abc"));
        assertNull(StringUtil.toLowerCase(null));
    }

    @Test
    @DisplayName("测试 toUpperCase 方法 - 转大写")
    void testToUpperCase() {
        assertEquals("ABC", StringUtil.toUpperCase("abc"));
        assertEquals("ABC", StringUtil.toUpperCase("ABC"));
        assertNull(StringUtil.toUpperCase(null));
    }

    @Test
    @DisplayName("测试 capitalize 方法 - 首字母大写")
    void testCapitalize() {
        assertEquals("Abc", StringUtil.capitalize("abc"));
        assertEquals("ABC", StringUtil.capitalize("ABC"));
        assertEquals("", StringUtil.capitalize(""));
        assertNull(StringUtil.capitalize(null));
    }

    @Test
    @DisplayName("测试 uncapitalize 方法 - 首字母小写")
    void testUncapitalize() {
        assertEquals("abc", StringUtil.uncapitalize("Abc"));
        assertEquals("abc", StringUtil.uncapitalize("abc"));
        assertEquals("", StringUtil.uncapitalize(""));
        assertNull(StringUtil.uncapitalize(null));
    }

    @Test
    @DisplayName("测试 camelToUnderline 方法 - 驼峰转下划线")
    void testCamelToUnderline() {
        assertEquals("user_name", StringUtil.camelToUnderline("userName"));
        assertEquals("user_first_name", StringUtil.camelToUnderline("userFirstName"));
        assertEquals("abc", StringUtil.camelToUnderline("abc"));
        assertEquals("", StringUtil.camelToUnderline(""));
        assertNull(StringUtil.camelToUnderline(null));
    }

    @Test
    @DisplayName("测试 underlineToCamel 方法 - 下划线转驼峰")
    void testUnderlineToCamel() {
        assertEquals("userName", StringUtil.underlineToCamel("user_name"));
        assertEquals("userFirstName", StringUtil.underlineToCamel("user_first_name"));
        assertEquals("abc", StringUtil.underlineToCamel("abc"));
        assertEquals("", StringUtil.underlineToCamel(""));
        assertNull(StringUtil.underlineToCamel(null));
    }

    @Test
    @DisplayName("测试 format 方法 - 字符串格式化")
    void testFormat() {
        assertEquals("Hello, World!", StringUtil.format("Hello, {}!", "World"));
        assertEquals("User: admin, Age: 25", StringUtil.format("User: {}, Age: {}", "admin", 25));
        assertEquals("No params", StringUtil.format("No params"));
        assertEquals("", StringUtil.format(""));
        assertNull(StringUtil.format(null));
    }

    @Test
    @DisplayName("测试 contains 方法 - 判断是否包含子串")
    void testContains() {
        assertTrue(StringUtil.contains("Hello World", "World"));
        assertTrue(StringUtil.contains("Hello World", "Hello"));
        assertFalse(StringUtil.contains("Hello World", "world"));
        assertFalse(StringUtil.contains(null, "abc"));
        assertFalse(StringUtil.contains("abc", null));
    }

    @Test
    @DisplayName("测试 startsWith 方法 - 判断是否以前缀开头")
    void testStartsWith() {
        assertTrue(StringUtil.startsWith("Hello World", "Hello"));
        assertFalse(StringUtil.startsWith("Hello World", "World"));
        assertFalse(StringUtil.startsWith(null, "abc"));
        assertFalse(StringUtil.startsWith("abc", null));
    }

    @Test
    @DisplayName("测试 endsWith 方法 - 判断是否以后缀结尾")
    void testEndsWith() {
        assertTrue(StringUtil.endsWith("Hello World", "World"));
        assertFalse(StringUtil.endsWith("Hello World", "Hello"));
        assertFalse(StringUtil.endsWith(null, "abc"));
        assertFalse(StringUtil.endsWith("abc", null));
    }

    @Test
    @DisplayName("测试 isEmail 方法 - 验证邮箱格式")
    void testIsEmail() {
        assertTrue(StringUtil.isEmail("test@example.com"));
        assertTrue(StringUtil.isEmail("user.name@domain.co.uk"));
        assertFalse(StringUtil.isEmail("invalid.email"));
        assertFalse(StringUtil.isEmail("@example.com"));
        assertFalse(StringUtil.isEmail(""));
        assertFalse(StringUtil.isEmail(null));
    }

    @Test
    @DisplayName("测试 isMobile 方法 - 验证手机号格式")
    void testIsMobile() {
        assertTrue(StringUtil.isMobile("13800138000"));
        assertTrue(StringUtil.isMobile("19912345678"));
        assertFalse(StringUtil.isMobile("12345678901"));
        assertFalse(StringUtil.isMobile("1380013800"));
        assertFalse(StringUtil.isMobile(""));
        assertFalse(StringUtil.isMobile(null));
    }

    @Test
    @DisplayName("测试 isIdCard 方法 - 验证身份证号格式")
    void testIsIdCard() {
        assertTrue(StringUtil.isIdCard("110101199001011234"));
        assertTrue(StringUtil.isIdCard("11010119900101123X"));
        assertFalse(StringUtil.isIdCard("11010119900101123"));
        assertFalse(StringUtil.isIdCard(""));
        assertFalse(StringUtil.isIdCard(null));
    }

    @Test
    @DisplayName("测试 isUrl 方法 - 验证 URL 格式")
    void testIsUrl() {
        assertTrue(StringUtil.isUrl("https://www.example.com"));
        assertTrue(StringUtil.isUrl("http://example.com"));
        assertTrue(StringUtil.isUrl("ftp://ftp.example.com"));
        assertFalse(StringUtil.isUrl("www.example.com"));
        assertFalse(StringUtil.isUrl(""));
        assertFalse(StringUtil.isUrl(null));
    }

    @Test
    @DisplayName("测试 isIpv4 方法 - 验证 IPv4 格式")
    void testIsIpv4() {
        assertTrue(StringUtil.isIpv4("192.168.1.1"));
        assertTrue(StringUtil.isIpv4("0.0.0.0"));
        assertTrue(StringUtil.isIpv4("255.255.255.255"));
        assertFalse(StringUtil.isIpv4("256.1.1.1"));
        assertFalse(StringUtil.isIpv4("192.168.1"));
        assertFalse(StringUtil.isIpv4(""));
        assertFalse(StringUtil.isIpv4(null));
    }

    @Test
    @DisplayName("测试 repeat 方法 - 重复字符串")
    void testRepeat() {
        assertEquals("abcabcabc", StringUtil.repeat("abc", 3));
        assertEquals("", StringUtil.repeat("abc", 0));
        assertEquals("", StringUtil.repeat("abc", -1));
        assertEquals("", StringUtil.repeat(null, 3));
    }

    @Test
    @DisplayName("测试 padRight 方法 - 右补全")
    void testPadRight() {
        assertEquals("abc   ", StringUtil.padRight("abc", 6));
        assertEquals("abc***", StringUtil.padRight("abc", 6, '*'));
        assertEquals("abc", StringUtil.padRight("abc", 3));
        assertNull(StringUtil.padRight(null, 5));
    }

    @Test
    @DisplayName("测试 padLeft 方法 - 左补全")
    void testPadLeft() {
        assertEquals("   abc", StringUtil.padLeft("abc", 6));
        assertEquals("***abc", StringUtil.padLeft("abc", 6, '*'));
        assertEquals("abc", StringUtil.padLeft("abc", 3));
        assertNull(StringUtil.padLeft(null, 5));
    }

    @Test
    @DisplayName("测试 sub 方法 - 截取字符串")
    void testSub() {
        assertEquals("World", StringUtil.sub("Hello World", 6, 11));
        assertEquals("Hello", StringUtil.sub("Hello World", 0, 5));
        assertEquals("", StringUtil.sub("abc", 0, 0));
        assertNull(StringUtil.sub(null, 0, 5));
    }

    @Test
    @DisplayName("测试 removeAllWhitespace 方法 - 删除所有空白")
    void testRemoveAllWhitespace() {
        assertEquals("HelloWorld", StringUtil.removeAllWhitespace("Hello World"));
        assertEquals("abc", StringUtil.removeAllWhitespace(" a b c "));
        assertEquals("", StringUtil.removeAllWhitespace("   "));
        assertEquals("", StringUtil.removeAllWhitespace(null));
    }

    @Test
    @DisplayName("测试 escapeHtml 方法 - HTML 转义")
    void testEscapeHtml() {
        assertEquals("&lt;script&gt;", StringUtil.escapeHtml("<script>"));
        assertEquals("Hello &amp; World", StringUtil.escapeHtml("Hello & World"));
        assertEquals("&quot;quoted&quot;", StringUtil.escapeHtml("\"quoted\""));
        assertEquals("", StringUtil.escapeHtml(""));
        assertNull(StringUtil.escapeHtml(null));
    }

    @Test
    @DisplayName("测试 unescapeHtml 方法 - HTML 反转义")
    void testUnescapeHtml() {
        assertEquals("<script>", StringUtil.unescapeHtml("&lt;script&gt;"));
        assertEquals("Hello & World", StringUtil.unescapeHtml("Hello &amp; World"));
        assertEquals("\"quoted\"", StringUtil.unescapeHtml("&quot;quoted&quot;"));
        assertEquals("", StringUtil.unescapeHtml(""));
        assertNull(StringUtil.unescapeHtml(null));
    }

    @Test
    @DisplayName("测试 join 方法 - 连接字符串")
    void testJoin() {
        assertEquals("a,b,c", StringUtil.join(",", "a", "b", "c"));
        assertEquals("a-b-c", StringUtil.join("-", "a", "b", "c"));
        assertEquals("abc", StringUtil.join("", "a", "b", "c"));
        assertEquals("", StringUtil.join(",", new String[]{}));
    }

    @Test
    @DisplayName("测试 split 方法 - 分割字符串")
    void testSplit() {
        String[] result = StringUtil.split("a,b,c", ",");
        assertArrayEquals(new String[]{"a", "b", "c"}, result);
        
        result = StringUtil.split("a-b-c", "-");
        assertArrayEquals(new String[]{"a", "b", "c"}, result);
        
        result = StringUtil.split("", ",");
        assertArrayEquals(new String[]{}, result);
    }

    @Test
    @DisplayName("测试 subWithNegative 方法 - 支持负数索引的截取")
    void testSubWithNegative() {
        assertEquals("World", StringUtil.subWithNegative("Hello World", -5, -1));
        assertEquals("Hello", StringUtil.subWithNegative("Hello World", 0, 5));
        assertEquals("", StringUtil.subWithNegative(null, 0, 5));
    }
}
