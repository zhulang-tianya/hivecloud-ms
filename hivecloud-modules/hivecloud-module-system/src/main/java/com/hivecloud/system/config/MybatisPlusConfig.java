package com.hivecloud.system.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import com.hivecloud.system.context.TenantContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * MyBatis-Plus 配置类
 * 配置租户插件，实现自动租户隔离
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 租户表白名单（不需要租户隔离的表）
     */
    private static final List<String> IGNORE_TABLES = Arrays.asList(
            "sys_tenant",           // 租户表
            "sys_tenant_package"    // 租户套餐表
    );

    /**
     * 配置 MyBatis-Plus 拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 租户插件
        TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor();
        tenantInterceptor.setTenantLineHandler(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                // 忽略不需要租户隔离的表（在 ignoreTable 中处理）
                // 从上下文获取租户 ID
                Long tenantId = TenantContext.getTenantId();
                if (tenantId == null) {
                    // 默认租户 ID（可根据实际情况调整）
                    tenantId = 1L;
                }
                return new LongValue(tenantId);
            }

            @Override
            public boolean ignoreTable(String tableName) {
                // 忽略不需要租户隔离的表
                return IGNORE_TABLES.contains(tableName);
            }
        });

        interceptor.addInnerInterceptor(tenantInterceptor);

        return interceptor;
    }
}
