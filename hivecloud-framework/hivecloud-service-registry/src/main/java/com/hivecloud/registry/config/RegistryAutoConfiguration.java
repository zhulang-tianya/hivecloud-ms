package com.hivecloud.registry.config;

import com.hivecloud.registry.service.DefaultServiceDiscovery;
import com.hivecloud.registry.service.RedisServiceRegistry;
import com.hivecloud.registry.service.ServiceDiscovery;
import com.hivecloud.registry.service.ServiceRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableConfigurationProperties(RegistryProperties.class)
public class RegistryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ServiceRegistry serviceRegistry(RedisTemplate<String, Object> redisTemplate) {
        return new RedisServiceRegistry(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceDiscovery serviceDiscovery(ServiceRegistry serviceRegistry) {
        return new DefaultServiceDiscovery(serviceRegistry);
    }
}
