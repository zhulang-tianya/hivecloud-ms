package com.hivecloud.plugin.cache.config;

import com.hivecloud.plugin.cache.config.CacheProperties;
import com.hivecloud.plugin.cache.core.CacheService;
import com.hivecloud.plugin.cache.core.CaffeineCacheService;
import com.hivecloud.plugin.cache.core.RedisCacheService;
import com.hivecloud.plugin.cache.core.TwoLevelCacheService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@AutoConfiguration
@EnableConfigurationProperties(CacheProperties.class)
public class CacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CaffeineCacheService caffeineCacheService(CacheProperties properties) {
        CacheProperties.Local local = properties.getLocal();
        return new CaffeineCacheService(
                local.getMaxSize(),
                local.getExpireAfterWrite(),
                TimeUnit.SECONDS
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisCacheService redisCacheService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisCacheService(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(CacheService.class)
    @ConditionalOnProperty(prefix = "hivecloud.cache", name = "type", havingValue = "local", matchIfMissing = false)
    public CacheService localCacheService(CaffeineCacheService caffeineCacheService) {
        return caffeineCacheService;
    }

    @Bean
    @ConditionalOnMissingBean(CacheService.class)
    @ConditionalOnProperty(prefix = "hivecloud.cache", name = "type", havingValue = "redis", matchIfMissing = false)
    public CacheService redisCacheService(RedisCacheService redisCacheService) {
        return redisCacheService;
    }

    @Bean
    @ConditionalOnMissingBean(CacheService.class)
    @ConditionalOnProperty(prefix = "hivecloud.cache", name = "type", havingValue = "two_level", matchIfMissing = true)
    public CacheService twoLevelCacheService(CaffeineCacheService caffeineCacheService,
                                              RedisCacheService redisCacheService,
                                              CacheProperties properties) {
        return new TwoLevelCacheService(
                caffeineCacheService,
                redisCacheService,
                properties.getKeyPrefix()
        );
    }
}