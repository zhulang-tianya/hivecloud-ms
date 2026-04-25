package com.hivecloud.plugin.log.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.hivecloud.plugin.log")
@MapperScan(basePackages = "com.hivecloud.plugin.log.mapper")
public class LogAutoConfiguration {
}
