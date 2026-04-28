package com.hivecloud.plugin.crypto;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import com.hivecloud.plugin.crypto.service.CryptoService;
import com.hivecloud.plugin.crypto.service.impl.CryptoServiceImpl;

/**
 * 加密自动配置类
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@AutoConfiguration
public class CryptoAutoConfiguration {

    /**
     * 创建加密服务 Bean
     *
     * @return 加密服务实例
     */
    @Bean
    @ConditionalOnMissingBean
    public CryptoService cryptoService() {
        return new CryptoServiceImpl();
    }
}
