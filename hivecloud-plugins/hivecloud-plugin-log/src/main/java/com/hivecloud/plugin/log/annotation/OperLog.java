package com.hivecloud.plugin.log.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 标注在 Controller 方法上，自动记录操作日志
 * 支持配置操作标题、业务类型、是否保存请求/响应数据
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see com.hivecloud.plugin.log.aspect.OperLogAspect
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperLog {

    /**
     * 操作标题
     *
     * @return 操作标题
     */
    String title() default "";

    /**
     * 业务类型
     *
     * @return 业务类型，默认为 OTHER
     */
    String businessType() default "OTHER";

    /**
     * 是否保存请求数据
     *
     * @return true-保存，false-不保存
     */
    boolean isSaveRequestData() default true;

    /**
     * 是否保存响应数据
     *
     * @return true-保存，false-不保存
     */
    boolean isSaveResponseData() default false;
}
