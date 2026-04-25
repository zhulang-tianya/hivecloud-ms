package com.hivecloud.plugin.auth.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.hivecloud.plugin.auth.interceptor.CustomDataPermissionInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AuthAutoConfiguration {

    @Bean
    public CustomDataPermissionInterceptor customDataPermissionInterceptor() {
        return new CustomDataPermissionInterceptor();
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(CustomDataPermissionInterceptor dataPermissionInterceptor) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        List<InnerInterceptor> innerInterceptors = new ArrayList<>();
        innerInterceptors.add(dataPermissionInterceptor);
        interceptor.setInterceptors(innerInterceptors);
        return interceptor;
    }
}
