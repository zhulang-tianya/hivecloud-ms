package com.hivecloud.plugin.log.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperLog {

    String title() default "";

    String businessType() default "OTHER";

    boolean isSaveRequestData() default true;

    boolean isSaveResponseData() default false;
}
