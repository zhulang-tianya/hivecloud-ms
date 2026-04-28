package com.hivecloud.common.core.result;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Result 单元测试类
 *
 * @author HiveCloud Team
 * @date 2026-04-28
 */
@DisplayName("Result 统一响应结果测试")
class ResultTest {

    @Test
    @DisplayName("测试 success 方法 - 无数据成功响应")
    void testSuccess() {
        Result<Void> result = Result.success();
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
        assertNotNull(result.getTimestamp());
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("测试 success 方法 - 带数据成功响应")
    void testSuccessWithData() {
        String data = "测试数据";
        Result<String> result = Result.success(data);
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals("测试数据", result.getData());
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("测试 success 方法 - 带数据和自定义消息")
    void testSuccessWithDataAndMessage() {
        Integer data = 100;
        Result<Integer> result = Result.success(data, "自定义成功消息");
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("自定义成功消息", result.getMessage());
        assertEquals(100, result.getData());
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("测试 error 方法 - 默认错误响应")
    void testError() {
        Result<Void> result = Result.error("操作失败");
        
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("操作失败", result.getMessage());
        assertNull(result.getData());
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("测试 error 方法 - 自定义错误码")
    void testErrorWithCode() {
        Result<Void> result = Result.error(404, "资源未找到");
        
        assertNotNull(result);
        assertEquals(404, result.getCode());
        assertEquals("资源未找到", result.getMessage());
        assertNull(result.getData());
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("测试构造函数 - 带参数构造")
    void testConstructor() {
        Result<String> result = new Result<>(201, "创建成功", "新资源");
        
        assertEquals(201, result.getCode());
        assertEquals("创建成功", result.getMessage());
        assertEquals("新资源", result.getData());
        assertNotNull(result.getTimestamp());
    }

    @Test
    @DisplayName("测试构造函数 - 默认构造")
    void testDefaultConstructor() {
        Result<Void> result = new Result<>();
        
        assertNull(result.getCode());
        assertNull(result.getMessage());
        assertNull(result.getData());
        assertNotNull(result.getTimestamp());
    }

    @Test
    @DisplayName("测试 isSuccess 方法")
    void testIsSuccess() {
        Result<Void> successResult = Result.success();
        Result<Void> errorResult = Result.error("失败");
        
        assertTrue(successResult.isSuccess());
        assertFalse(errorResult.isSuccess());
    }

    @Test
    @DisplayName("测试泛型数据 - 对象类型")
    void testGenericWithObject() {
        class User {
            String name;
            Integer age;
            
            User(String name, Integer age) {
                this.name = name;
                this.age = age;
            }
        }
        
        User user = new User("张三", 25);
        Result<User> result = Result.success(user);
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("张三", result.getData().name);
        assertEquals(25, result.getData().age);
    }

    @Test
    @DisplayName("测试泛型数据 - List 类型")
    void testGenericWithList() {
        List<String> dataList = Arrays.asList("数据 1", "数据 2", "数据 3");
        Result<List<String>> result = Result.success(dataList);
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(3, result.getData().size());
        assertEquals("数据 1", result.getData().get(0));
    }

    @Test
    @DisplayName("测试时间戳 - 自动生成")
    void testTimestamp() {
        long before = System.currentTimeMillis();
        Result<Void> result = Result.success();
        long after = System.currentTimeMillis();
        
        assertNotNull(result.getTimestamp());
        assertTrue(result.getTimestamp() >= before);
        assertTrue(result.getTimestamp() <= after);
    }

    @Test
    @DisplayName("测试时间戳 - 序列化兼容性")
    void testTimestampSerialization() {
        Result<String> result1 = Result.success("测试");
        Long timestamp1 = result1.getTimestamp();
        
        assertNotNull(timestamp1);
        assertTrue(timestamp1 > 0);
    }

    @Test
    @DisplayName("测试错误码范围")
    void testErrorCodeRange() {
        Result<Void> badRequest = Result.error(400, "请求参数错误");
        Result<Void> unauthorized = Result.error(401, "未认证");
        Result<Void> forbidden = Result.error(403, "禁止访问");
        Result<Void> notFound = Result.error(404, "资源不存在");
        Result<Void> internalError = Result.error(500, "服务器错误");
        
        assertEquals(400, badRequest.getCode());
        assertEquals(401, unauthorized.getCode());
        assertEquals(403, forbidden.getCode());
        assertEquals(404, notFound.getCode());
        assertEquals(500, internalError.getCode());
    }

    @Test
    @DisplayName("测试消息内容 - 支持 null")
    void testMessageWithNull() {
        Result<String> result = new Result<>(500, null, null);
        
        assertNull(result.getMessage());
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("测试消息内容 - 支持空字符串")
    void testMessageWithEmpty() {
        Result<String> result = new Result<>(500, "", null);
        
        assertEquals("", result.getMessage());
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("测试复杂业务场景 - 分页数据")
    void testComplexBusinessScenario() {
        PageResult<String> pageData = PageResult.of(
            Arrays.asList("item1", "item2"),
            100L,
            1,
            10
        );
        
        Result<PageResult<String>> result = Result.success(pageData);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(2, result.getData().getItems().size());
        assertEquals(100L, result.getData().getTotal());
    }
}
