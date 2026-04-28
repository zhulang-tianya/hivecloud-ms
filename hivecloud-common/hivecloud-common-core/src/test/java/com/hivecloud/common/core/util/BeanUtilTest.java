package com.hivecloud.common.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BeanUtil 单元测试类
 *
 * @author HiveCloud Team
 * @date 2026-04-28
 */
@DisplayName("BeanUtil 工具类测试")
class BeanUtilTest {

    /**
     * 测试用 Bean 类
     */
    static class TestBean {
        private String name;
        private Integer age;
        private String email;

        public TestBean() {}

        public TestBean(String name, Integer age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    /**
     * 测试用 DTO 类
     */
    static class TestDTO {
        private String name;
        private Integer age;
        private String email;

        public TestDTO() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    @Test
    @DisplayName("测试 copyProperties 方法 - 对象属性拷贝")
    void testCopyProperties() {
        TestBean source = new TestBean("张三", 25, "zhangsan@example.com");
        TestDTO target = new TestDTO();
        
        BeanUtil.copyProperties(source, target);
        
        assertEquals("张三", target.getName());
        assertEquals(25, target.getAge());
        assertEquals("zhangsan@example.com", target.getEmail());
    }

    @Test
    @DisplayName("测试 copyProperties 方法 - 创建新对象")
    void testCopyPropertiesWithClass() {
        TestBean source = new TestBean("李四", 30, "lisi@example.com");
        TestDTO target = BeanUtil.copyProperties(source, TestDTO.class);
        
        assertNotNull(target);
        assertEquals("李四", target.getName());
        assertEquals(30, target.getAge());
        assertEquals("lisi@example.com", target.getEmail());
    }

    @Test
    @DisplayName("测试 copyProperties 方法 - 使用 Supplier")
    void testCopyPropertiesWithSupplier() {
        TestBean source = new TestBean("王五", 28, "wangwu@example.com");
        TestDTO target = BeanUtil.copyProperties(source, TestDTO::new);
        
        assertNotNull(target);
        assertEquals("王五", target.getName());
        assertEquals(28, target.getAge());
        assertEquals("wangwu@example.com", target.getEmail());
    }

    @Test
    @DisplayName("测试 copyProperties 方法 - 忽略指定属性")
    void testCopyPropertiesWithIgnore() {
        TestBean source = new TestBean("赵六", 35, "zhaoliu@example.com");
        TestDTO target = new TestDTO();
        
        BeanUtil.copyProperties(source, target, "email");
        
        assertEquals("赵六", target.getName());
        assertEquals(35, target.getAge());
        assertNull(target.getEmail());
    }

    @Test
    @DisplayName("测试 copyProperties 方法 - null 值处理")
    void testCopyPropertiesNull() {
        assertDoesNotThrow(() -> BeanUtil.copyProperties(null, new TestDTO()));
        assertDoesNotThrow(() -> BeanUtil.copyProperties(new TestBean(), (TestDTO) null));
    }

    @Test
    @DisplayName("测试 copyList 方法 - List 转换")
    void testCopyList() {
        List<TestBean> sourceList = Arrays.asList(
            new TestBean("用户 1", 20, "user1@example.com"),
            new TestBean("用户 2", 22, "user2@example.com"),
            new TestBean("用户 3", 24, "user3@example.com")
        );
        
        List<TestDTO> targetList = BeanUtil.copyList(sourceList, TestDTO.class);
        
        assertEquals(3, targetList.size());
        assertEquals("用户 1", targetList.get(0).getName());
        assertEquals(22, targetList.get(1).getAge());
        assertEquals("user3@example.com", targetList.get(2).getEmail());
    }

    @Test
    @DisplayName("测试 copyList 方法 - 空列表处理")
    void testCopyListEmpty() {
        List<TestBean> emptyList = new ArrayList<>();
        List<TestDTO> result = BeanUtil.copyList(emptyList, TestDTO.class);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试 copyList 方法 - null 列表处理")
    void testCopyListNull() {
        List<TestDTO> result = BeanUtil.copyList((List<TestBean>) null, TestDTO.class);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试 mapToBean 方法 - Map 转 Bean")
    void testMapToBean() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "孙七");
        map.put("age", 40);
        map.put("email", "sunqi@example.com");
        
        TestBean bean = BeanUtil.mapToBean(map, TestBean.class);
        
        assertNotNull(bean);
        assertEquals("孙七", bean.getName());
        assertEquals(40, bean.getAge());
        assertEquals("sunqi@example.com", bean.getEmail());
    }

    @Test
    @DisplayName("测试 mapToBean 方法 - null Map 处理")
    void testMapToBeanNull() {
        TestBean bean = BeanUtil.mapToBean(null, TestBean.class);
        assertNull(bean);
    }

    @Test
    @DisplayName("测试 mapToBean 方法 - 空 Map 处理")
    void testMapToBeanEmpty() {
        TestBean bean = BeanUtil.mapToBean(new HashMap<>(), TestBean.class);
        assertNotNull(bean);
        assertNull(bean.getName());
        assertNull(bean.getAge());
    }

    @Test
    @DisplayName("测试 beanToMap 方法 - Bean 转 Map")
    void testBeanToMap() {
        TestBean bean = new TestBean("周八", 45, "zhouba@example.com");
        Map<String, Object> map = BeanUtil.beanToMap(bean);
        
        assertNotNull(map);
        assertEquals(3, map.size());
        assertEquals("周八", map.get("name"));
        assertEquals(45, map.get("age"));
        assertEquals("zhouba@example.com", map.get("email"));
    }

    @Test
    @DisplayName("测试 beanToMap 方法 - null Bean 处理")
    void testBeanToMapNull() {
        Map<String, Object> map = BeanUtil.beanToMap(null);
        assertNotNull(map);
        assertTrue(map.isEmpty());
    }

    @Test
    @DisplayName("测试 mapListToBeanList 方法 - List<Map>转 List<Bean>")
    void testMapListToBeanList() {
        List<Map<String, Object>> mapList = new ArrayList<>();
        
        Map<String, Object> map1 = new HashMap<>();
        map1.put("name", "用户 A");
        map1.put("age", 25);
        mapList.add(map1);
        
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", "用户 B");
        map2.put("age", 30);
        mapList.add(map2);
        
        List<TestBean> beanList = BeanUtil.mapListToBeanList(mapList, TestBean.class);
        
        assertEquals(2, beanList.size());
        assertEquals("用户 A", beanList.get(0).getName());
        assertEquals(30, beanList.get(1).getAge());
    }

    @Test
    @DisplayName("测试 beanListToMapList 方法 - List<Bean>转 List<Map>")
    void testBeanListToMapList() {
        List<TestBean> beanList = Arrays.asList(
            new TestBean("甲", 18, "jia@example.com"),
            new TestBean("乙", 20, "yi@example.com")
        );
        
        List<Map<String, Object>> mapList = BeanUtil.beanListToMapList(beanList);
        
        assertEquals(2, mapList.size());
        assertEquals("甲", mapList.get(0).get("name"));
        assertEquals(20, mapList.get(1).get("age"));
    }

    @Test
    @DisplayName("测试 isEmpty 方法 - 判断对象是否为空")
    void testIsEmpty() {
        assertTrue(BeanUtil.isEmpty(null));
        assertFalse(BeanUtil.isEmpty(new Object()));
        assertFalse(BeanUtil.isEmpty("abc"));
    }

    @Test
    @DisplayName("测试 isNotEmpty 方法 - 判断对象是否不为空")
    void testIsNotEmpty() {
        assertFalse(BeanUtil.isNotEmpty(null));
        assertTrue(BeanUtil.isNotEmpty(new Object()));
        assertTrue(BeanUtil.isNotEmpty("abc"));
    }

    @Test
    @DisplayName("测试 getProperty 方法 - 获取属性值")
    void testGetProperty() {
        TestBean bean = new TestBean("测试", 99, "test@example.com");
        
        assertEquals("测试", BeanUtil.getProperty(bean, "name"));
        assertEquals(99, BeanUtil.getProperty(bean, "age"));
        assertEquals("test@example.com", BeanUtil.getProperty(bean, "email"));
        assertNull(BeanUtil.getProperty(bean, "nonexistent"));
    }

    @Test
    @DisplayName("测试 getProperty 方法 - null 对象处理")
    void testGetPropertyNull() {
        assertNull(BeanUtil.getProperty(null, "name"));
    }

    @Test
    @DisplayName("测试 setProperty 方法 - 设置属性值")
    void testSetProperty() {
        TestBean bean = new TestBean();
        
        BeanUtil.setProperty(bean, "name", "新名称");
        BeanUtil.setProperty(bean, "age", 50);
        
        assertEquals("新名称", bean.getName());
        assertEquals(50, bean.getAge());
    }

    @Test
    @DisplayName("测试 getPropertyNames 方法 - 获取所有属性名")
    void testGetPropertyNames() {
        TestBean bean = new TestBean();
        String[] propertyNames = BeanUtil.getPropertyNames(bean);
        
        assertNotNull(propertyNames);
        assertTrue(propertyNames.length > 0);
        assertTrue(Arrays.asList(propertyNames).contains("name"));
        assertTrue(Arrays.asList(propertyNames).contains("age"));
        assertTrue(Arrays.asList(propertyNames).contains("email"));
    }

    @Test
    @DisplayName("测试 getPropertyNames 方法 - null 对象处理")
    void testGetPropertyNamesNull() {
        String[] propertyNames = BeanUtil.getPropertyNames(null);
        assertNotNull(propertyNames);
        assertEquals(0, propertyNames.length);
    }
}
