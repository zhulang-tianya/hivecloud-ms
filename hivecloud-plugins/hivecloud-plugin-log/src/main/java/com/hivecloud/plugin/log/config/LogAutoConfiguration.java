package com.hivecloud.plugin.log.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 日志模块自动配置类
 * 扫描日志插件的组件和 Mapper 接口
 * 实现日志模块的自动装配
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see ComponentScan
 * @see MapperScan
 */
@Configuration
@ComponentScan(basePackages = "com.hivecloud.plugin.log")
@MapperScan(basePackages = "com.hivecloud.plugin.log.mapper")
public class LogAutoConfiguration {
}
