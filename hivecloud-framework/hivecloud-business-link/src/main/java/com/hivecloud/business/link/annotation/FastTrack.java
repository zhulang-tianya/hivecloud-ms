package com.hivecloud.business.link.annotation;

import java.lang.annotation.*;

/**
 * 快轨注解（只读链路）
 * 用于标记只读业务方法，走快速通道
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FastTrack {

    /**
     * 业务描述
     *
     * @return 业务描述
     */
    String value() default "";
}
