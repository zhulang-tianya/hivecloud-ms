package com.hivecloud.plugin.engine.spi;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HiveCloudPlugin {

    String name();

    String version() default "1.0.0";

    String description() default "";

    String[] dependsOn() default {};

    boolean autoStart() default true;

    int order() default 0;
}
