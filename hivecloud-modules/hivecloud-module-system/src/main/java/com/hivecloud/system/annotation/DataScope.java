package com.hivecloud.system.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据范围过滤注解
 * 标注在需要数据范围过滤的方法上
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 角色对象的参数名（用于获取角色的数据范围设置）
     * 默认值为 "role"
     */
    String roleParam() default "role";

    /**
     * 用户 ID 的参数名（用于获取用户所属部门）
     * 默认值为 "userId"
     */
    String userIdParam() default "userId";

    /**
     * 部门 ID 的字段名（用于 SQL 中的部门 ID 过滤）
     * 默认值为 "deptId"
     */
    String deptIdField() default "deptId";
}
