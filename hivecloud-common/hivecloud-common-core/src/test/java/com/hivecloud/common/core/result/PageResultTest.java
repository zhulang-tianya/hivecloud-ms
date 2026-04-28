package com.hivecloud.common.core.result;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PageResult 单元测试类
 *
 * @author HiveCloud Team
 * @date 2026-04-28
 */
@DisplayName("PageResult 分页响应结果测试")
class PageResultTest {

    @Test
    @DisplayName("测试构造函数 - 带参数构造")
    void testConstructor() {
        List<String> items = Arrays.asList("数据 1", "数据 2", "数据 3");
        PageResult<String> pageResult = new PageResult<>(items, 100L, 1, 10);
        
        assertNotNull(pageResult);
        assertEquals(3, pageResult.getItems().size());
        assertEquals(100L, pageResult.getTotal());
        assertEquals(1, pageResult.getPage());
        assertEquals(10, pageResult.getSize());
    }

    @Test
    @DisplayName("测试构造函数 - 默认构造")
    void testDefaultConstructor() {
        PageResult<String> pageResult = new PageResult<>();
        
        assertNotNull(pageResult);
        assertNull(pageResult.getItems());
        assertNull(pageResult.getTotal());
        assertNull(pageResult.getPage());
        assertNull(pageResult.getSize());
    }

    @Test
    @DisplayName("测试 of 方法 - 创建分页结果")
    void testOf() {
        List<Integer> items = Arrays.asList(1, 2, 3, 4, 5);
        PageResult<Integer> pageResult = PageResult.of(items, 50L, 2, 5);
        
        assertNotNull(pageResult);
        assertEquals(5, pageResult.getItems().size());
        assertEquals(50L, pageResult.getTotal());
        assertEquals(2, pageResult.getPage());
        assertEquals(5, pageResult.getSize());
    }

    @Test
    @DisplayName("测试 empty 方法 - 创建空分页结果")
    void testEmpty() {
        PageResult<String> pageResult = PageResult.empty();
        
        assertNotNull(pageResult);
        assertTrue(pageResult.getItems().isEmpty());
        assertEquals(0L, pageResult.getTotal());
        assertEquals(1, pageResult.getPage());
        assertEquals(10, pageResult.getSize());
    }

    @Test
    @DisplayName("测试 getTotalPages 方法 - 计算总页数")
    void testGetTotalPages() {
        List<String> items = Arrays.asList("数据 1", "数据 2");
        PageResult<String> pageResult = new PageResult<>(items, 95L, 1, 10);
        
        assertEquals(10, pageResult.getTotalPages());
    }

    @Test
    @DisplayName("测试 getTotalPages 方法 - 向上取整")
    void testGetTotalPagesRounding() {
        List<String> items = Arrays.asList("数据 1");
        PageResult<String> pageResult = new PageResult<>(items, 91L, 1, 10);
        
        assertEquals(10, pageResult.getTotalPages());
    }

    @Test
    @DisplayName("测试 getTotalPages 方法 - size 为 null")
    void testGetTotalPagesNullSize() {
        List<String> items = Arrays.asList("数据 1");
        PageResult<String> pageResult = new PageResult<>(items, 100L, 1, null);
        
        assertEquals(0, pageResult.getTotalPages());
    }

    @Test
    @DisplayName("测试 getTotalPages 方法 - size 为 0")
    void testGetTotalPagesZeroSize() {
        List<String> items = Arrays.asList("数据 1");
        PageResult<String> pageResult = new PageResult<>(items, 100L, 1, 0);
        
        assertEquals(0, pageResult.getTotalPages());
    }

    @Test
    @DisplayName("测试 getTotalPages 方法 - 正好整除")
    void testGetTotalPagesExactDivision() {
        List<String> items = Arrays.asList("数据 1");
        PageResult<String> pageResult = new PageResult<>(items, 100L, 1, 10);
        
        assertEquals(10, pageResult.getTotalPages());
    }

    @Test
    @DisplayName("测试 hasNext 方法 - 有下一页")
    void testHasNext() {
        List<String> items = Arrays.asList("数据 1");
        PageResult<String> pageResult = new PageResult<>(items, 100L, 5, 10);
        
        assertTrue(pageResult.hasNext());
    }

    @Test
    @DisplayName("测试 hasNext 方法 - 无下一页")
    void testHasNextFalse() {
        List<String> items = Arrays.asList("数据 1");
        PageResult<String> pageResult = new PageResult<>(items, 100L, 10, 10);
        
        assertFalse(pageResult.hasNext());
    }

    @Test
    @DisplayName("测试 hasNext 方法 - page 为 null")
    void testHasNextNullPage() {
        List<String> items = Arrays.asList("数据 1");
        PageResult<String> pageResult = new PageResult<>(items, 100L, null, 10);
        
        assertFalse(pageResult.hasNext());
    }

    @Test
    @DisplayName("测试 hasPrevious 方法 - 有上一页")
    void testHasPrevious() {
        List<String> items = Arrays.asList("数据 1");
        PageResult<String> pageResult = new PageResult<>(items, 100L, 5, 10);
        
        assertTrue(pageResult.hasPrevious());
    }

    @Test
    @DisplayName("测试 hasPrevious 方法 - 无上一页（第一页）")
    void testHasPreviousFalse() {
        List<String> items = Arrays.asList("数据 1");
        PageResult<String> pageResult = new PageResult<>(items, 100L, 1, 10);
        
        assertFalse(pageResult.hasPrevious());
    }

    @Test
    @DisplayName("测试 hasPrevious 方法 - page 为 null")
    void testHasPreviousNullPage() {
        List<String> items = Arrays.asList("数据 1");
        PageResult<String> pageResult = new PageResult<>(items, 100L, null, 10);
        
        assertFalse(pageResult.hasPrevious());
    }

    @Test
    @DisplayName("测试分页场景 - 第一页")
    void testPaginationFirstPage() {
        List<String> items = Arrays.asList("数据 1", "数据 2", "数据 3", "数据 4", "数据 5");
        PageResult<String> pageResult = new PageResult<>(items, 23L, 1, 5);
        
        assertEquals(5, pageResult.getItems().size());
        assertEquals(1, pageResult.getPage());
        assertEquals(5, pageResult.getSize());
        assertEquals(5, pageResult.getTotalPages());
        assertFalse(pageResult.hasPrevious());
        assertTrue(pageResult.hasNext());
    }

    @Test
    @DisplayName("测试分页场景 - 中间页")
    void testPaginationMiddlePage() {
        List<String> items = Arrays.asList("数据 6", "数据 7", "数据 8", "数据 9", "数据 10");
        PageResult<String> pageResult = new PageResult<>(items, 23L, 2, 5);
        
        assertEquals(5, pageResult.getItems().size());
        assertEquals(2, pageResult.getPage());
        assertEquals(5, pageResult.getSize());
        assertEquals(5, pageResult.getTotalPages());
        assertTrue(pageResult.hasPrevious());
        assertTrue(pageResult.hasNext());
    }

    @Test
    @DisplayName("测试分页场景 - 最后一页")
    void testPaginationLastPage() {
        List<String> items = Arrays.asList("数据 21", "数据 22", "数据 23");
        PageResult<String> pageResult = new PageResult<>(items, 23L, 5, 5);
        
        assertEquals(3, pageResult.getItems().size());
        assertEquals(5, pageResult.getPage());
        assertEquals(5, pageResult.getSize());
        assertEquals(5, pageResult.getTotalPages());
        assertTrue(pageResult.hasPrevious());
        assertFalse(pageResult.hasNext());
    }

    @Test
    @DisplayName("测试分页场景 - 空结果")
    void testPaginationEmpty() {
        PageResult<String> pageResult = new PageResult<>(Collections.emptyList(), 0L, 1, 10);
        
        assertTrue(pageResult.getItems().isEmpty());
        assertEquals(0L, pageResult.getTotal());
        assertEquals(0, pageResult.getTotalPages());
        assertFalse(pageResult.hasPrevious());
        assertFalse(pageResult.hasNext());
    }

    @Test
    @DisplayName("测试泛型支持 - 对象类型")
    void testGenericWithObject() {
        class User {
            String name;
            Integer age;
            
            User(String name, Integer age) {
                this.name = name;
                this.age = age;
            }
        }
        
        List<User> users = Arrays.asList(
            new User("张三", 25),
            new User("李四", 30)
        );
        
        PageResult<User> pageResult = new PageResult<>(users, 2L, 1, 10);
        
        assertNotNull(pageResult);
        assertEquals(2, pageResult.getItems().size());
        assertEquals("张三", pageResult.getItems().get(0).name);
        assertEquals(30, pageResult.getItems().get(1).age);
    }

    @Test
    @DisplayName("测试泛型支持 - 基本类型包装类")
    void testGenericWithPrimitives() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        PageResult<Integer> pageResult = new PageResult<>(numbers, 100L, 1, 5);
        
        assertEquals(5, pageResult.getItems().size());
        assertEquals(1, pageResult.getItems().get(0));
        assertEquals(5, pageResult.getItems().get(4));
    }

    @Test
    @DisplayName("测试边界条件 - total 为 0")
    void testEdgeCaseZeroTotal() {
        PageResult<String> pageResult = new PageResult<>(Collections.emptyList(), 0L, 1, 10);
        assertEquals(0, pageResult.getTotalPages());
    }

    @Test
    @DisplayName("测试边界条件 - total 小于 pageSize")
    void testEdgeCaseTotalLessThanPageSize() {
        List<String> items = Arrays.asList("数据 1", "数据 2", "数据 3");
        PageResult<String> pageResult = new PageResult<>(items, 3L, 1, 10);
        
        assertEquals(1, pageResult.getTotalPages());
        assertFalse(pageResult.hasNext());
    }

    @Test
    @DisplayName("测试边界条件 - total 等于 pageSize")
    void testEdgeCaseTotalEqualsPageSize() {
        List<String> items = Arrays.asList("数据 1", "数据 2", "数据 3", "数据 4", "数据 5");
        PageResult<String> pageResult = new PageResult<>(items, 5L, 1, 5);
        
        assertEquals(1, pageResult.getTotalPages());
        assertFalse(pageResult.hasNext());
    }

    @Test
    @DisplayName("测试边界条件 - total 略大于 pageSize")
    void testEdgeCaseTotalSlightlyMoreThanPageSize() {
        List<String> items = Arrays.asList("数据 1", "数据 2", "数据 3", "数据 4", "数据 5");
        PageResult<String> pageResult = new PageResult<>(items, 6L, 1, 5);
        
        assertEquals(2, pageResult.getTotalPages());
        assertTrue(pageResult.hasNext());
    }
}
