package com.hivecloud.common.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Bean 工具类
 * <p>
 * 提供对象属性拷贝、转换等功能
 * 基于 Spring BeanUtils 实现，支持浅拷贝和深拷贝
 * </p>
 *
 * @author HiveCloud Team
 * @date 2026-04-28
 * @since 1.0.0
 */
public class BeanUtil {

    private BeanUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 拷贝对象属性（浅拷贝）
     * <p>
     * 将 source 对象的属性值拷贝到 target 对象
     * 只拷贝同名且类型兼容的属性
     * </p>
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }

    /**
     * 拷贝对象属性并返回新对象
     * <p>
     * 创建一个新的 target 类型对象，并拷贝 source 的属性
     * </p>
     *
     * @param source 源对象
     * @param targetClass 目标类型
     * @param <T> 目标类型
     * @return 新的目标对象
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + targetClass.getName(), e);
        }
    }

    /**
     * 拷贝对象属性（使用 Supplier）
     * <p>
     * 通过 Supplier 创建目标对象，然后拷贝属性
     * </p>
     *
     * @param source 源对象
     * @param supplier 目标对象创建者
     * @param <T> 目标类型
     * @return 新的目标对象
     */
    public static <T> T copyProperties(Object source, Supplier<T> supplier) {
        if (source == null || supplier == null) {
            return null;
        }
        T target = supplier.get();
        copyProperties(source, target);
        return target;
    }

    /**
     * 拷贝对象属性（忽略指定属性）
     *
     * @param source 源对象
     * @param target 目标对象
     * @param ignoreProperties 忽略的属性名数组
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        if (source == null || target == null) {
            return;
        }
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    /**
     * List 转换（浅拷贝）
     * <p>
     * 将 source List 中的每个元素拷贝到 target 类型
     * </p>
     *
     * @param sourceList 源列表
     * @param targetClass 目标类型
     * @param <T> 目标类型
     * @return 新的目标列表
     */
    public static <T> List<T> copyList(List<?> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> targetList = new ArrayList<>(sourceList.size());
        for (Object source : sourceList) {
            targetList.add(copyProperties(source, targetClass));
        }
        return targetList;
    }

    /**
     * List 转换（使用 Supplier）
     *
     * @param sourceList 源列表
     * @param supplier 目标对象创建者
     * @param <T> 目标类型
     * @return 新的目标列表
     */
    public static <T> List<T> copyList(List<?> sourceList, Supplier<T> supplier) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> targetList = new ArrayList<>(sourceList.size());
        for (Object source : sourceList) {
            targetList.add(copyProperties(source, supplier));
        }
        return targetList;
    }

    /**
     * Map 转 Bean
     *
     * @param map Map 数据
     * @param beanClass Bean 类型
     * @param <T> Bean 类型
     * @return Bean 对象
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        try {
            T bean = beanClass.getDeclaredConstructor().newInstance();
            org.springframework.beans.BeanWrapper wrapper = new org.springframework.beans.BeanWrapperImpl(bean);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String propertyName = entry.getKey();
                Object value = entry.getValue();
                try {
                    wrapper.setPropertyValue(propertyName, value);
                } catch (Exception e) {
                    // 忽略无法设置的属性
                }
            }
            return bean;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert map to bean", e);
        }
    }

    /**
     * Bean 转 Map
     *
     * @param bean Bean 对象
     * @return Map 数据
     */
    public static Map<String, Object> beanToMap(Object bean) {
        if (bean == null) {
            return new HashMap<>();
        }
        Map<String, Object> map = new HashMap<>();
        org.springframework.beans.BeanWrapper wrapper = new org.springframework.beans.BeanWrapperImpl(bean);
        for (java.beans.PropertyDescriptor pd : wrapper.getPropertyDescriptors()) {
            String propertyName = pd.getName();
            if (!"class".equals(propertyName)) {
                Object value = wrapper.getPropertyValue(propertyName);
                if (value != null) {
                    map.put(propertyName, value);
                }
            }
        }
        return map;
    }

    /**
     * List<Map> 转 List<Bean>
     *
     * @param mapList Map 列表
     * @param beanClass Bean 类型
     * @param <T> Bean 类型
     * @return Bean 列表
     */
    public static <T> List<T> mapListToBeanList(List<Map<String, Object>> mapList, Class<T> beanClass) {
        if (mapList == null || mapList.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> beanList = new ArrayList<>(mapList.size());
        for (Map<String, Object> map : mapList) {
            beanList.add(mapToBean(map, beanClass));
        }
        return beanList;
    }

    /**
     * List<Bean> 转 List<Map>
     *
     * @param beanList Bean 列表
     * @return Map 列表
     */
    public static List<Map<String, Object>> beanListToMapList(List<?> beanList) {
        if (beanList == null || beanList.isEmpty()) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> mapList = new ArrayList<>(beanList.size());
        for (Object bean : beanList) {
            mapList.add(beanToMap(bean));
        }
        return mapList;
    }

    /**
     * 验证对象是否为空
     *
     * @param obj 对象
     * @return 空返回 true，否则返回 false
     */
    public static boolean isEmpty(Object obj) {
        return obj == null;
    }

    /**
     * 验证对象是否不为空
     *
     * @param obj 对象
     * @return 不为空返回 true，否则返回 false
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 获取对象属性值
     *
     * @param obj 对象
     * @param propertyName 属性名
     * @return 属性值
     */
    public static Object getProperty(Object obj, String propertyName) {
        if (obj == null) {
            return null;
        }
        org.springframework.beans.BeanWrapper wrapper = new org.springframework.beans.BeanWrapperImpl(obj);
        return wrapper.getPropertyValue(propertyName);
    }

    /**
     * 设置对象属性值
     *
     * @param obj 对象
     * @param propertyName 属性名
     * @param value 属性值
     */
    public static void setProperty(Object obj, String propertyName, Object value) {
        if (obj == null) {
            return;
        }
        org.springframework.beans.BeanWrapper wrapper = new org.springframework.beans.BeanWrapperImpl(obj);
        wrapper.setPropertyValue(propertyName, value);
    }

    /**
     * 获取所有属性名
     *
     * @param obj 对象
     * @return 属性名数组
     */
    public static String[] getPropertyNames(Object obj) {
        if (obj == null) {
            return new String[0];
        }
        org.springframework.beans.BeanWrapper wrapper = new org.springframework.beans.BeanWrapperImpl(obj);
        java.beans.PropertyDescriptor[] pds = wrapper.getPropertyDescriptors();
        String[] names = new String[pds.length];
        for (int i = 0; i < pds.length; i++) {
            names[i] = pds[i].getName();
        }
        return names;
    }
}
