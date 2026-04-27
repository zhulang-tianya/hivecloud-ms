package com.hivecloud.plugin.auth.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.hivecloud.plugin.auth.interceptor.CustomDataPermissionInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 认证授权自动配置类
 * 配置数据权限拦截器和 MybatisPlus 拦截器
 * 实现自定义数据权限控制逻辑
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see CustomDataPermissionInterceptor
 * @see MybatisPlusInterceptor
 */
@Configuration
public class AuthAutoConfiguration {

    /**
     * 创建自定义数据权限拦截器
     *
     * @return 数据权限拦截器实例
     */
    @Bean
    public CustomDataPermissionInterceptor customDataPermissionInterceptor() {
        return new CustomDataPermissionInterceptor();
    }

    /**
     * 创建 MybatisPlus 拦截器
     * 配置数据权限拦截器到 MybatisPlus 插件链
     *
     * @param dataPermissionInterceptor 数据权限拦截器
     * @return MybatisPlus 拦截器实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(CustomDataPermissionInterceptor dataPermissionInterceptor) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        List<InnerInterceptor> innerInterceptors = new ArrayList<>();
        innerInterceptors.add(dataPermissionInterceptor);
        interceptor.setInterceptors(innerInterceptors);
        return interceptor;
    }
}
