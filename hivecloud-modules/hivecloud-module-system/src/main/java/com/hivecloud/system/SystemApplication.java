package com.hivecloud.system;

import com.hivecloud.system.config.MybatisPlusConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 系统管理模块启动类
 * 启用 Nacos 服务发现
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.hivecloud.system.mapper")
@ComponentScan(basePackages = {"com.hivecloud.system", "com.hivecloud.common"},
               excludeFilters = {
                   @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = com.hivecloud.common.mybatis.config.MybatisPlusConfig.class)
               })
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}
