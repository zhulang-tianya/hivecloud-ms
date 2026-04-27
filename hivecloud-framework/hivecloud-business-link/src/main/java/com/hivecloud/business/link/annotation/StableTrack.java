package com.hivecloud.business.link.annotation;

import java.lang.annotation.*;

/**
 * 稳轨注解（写事务链路）
 * 用于标记写业务方法，走稳定通道
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StableTrack {

    /**
     * 业务描述
     *
     * @return 业务描述
     */
    String value() default "";
}
