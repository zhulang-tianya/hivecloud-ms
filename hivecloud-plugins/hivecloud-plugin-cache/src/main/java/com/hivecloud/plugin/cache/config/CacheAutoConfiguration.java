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

/**
 * 缓存自动配置类
 * 根据配置自动创建本地缓存、Redis 缓存或二级缓存 Bean
 * 支持条件化装配，可灵活切换缓存类型
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see CacheProperties
 * @see CacheService
 * @see CaffeineCacheService
 * @see RedisCacheService
 * @see TwoLevelCacheService
 */
@AutoConfiguration
@EnableConfigurationProperties(CacheProperties.class)
public class CacheAutoConfiguration {

    /**
     * 创建 Caffeine 本地缓存服务
     *
     * @param properties 缓存配置属性
     * @return Caffeine 缓存服务实例
     */
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

    /**
     * 创建 Redis 缓存服务
     *
     * @param redisTemplate Redis 模板
     * @return Redis 缓存服务实例
     */
    @Bean
    @ConditionalOnMissingBean
    public RedisCacheService redisCacheService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisCacheService(redisTemplate);
    }

    /**
     * 创建本地缓存服务（Caffeine 实现）
     * 当配置 hivecloud.cache.type=local 时生效
     *
     * @param caffeineCacheService Caffeine 缓存服务
     * @return 缓存服务实例
     */
    @Bean
    @ConditionalOnMissingBean(CacheService.class)
    @ConditionalOnProperty(prefix = "hivecloud.cache", name = "type", havingValue = "local", matchIfMissing = false)
    public CacheService localCacheService(CaffeineCacheService caffeineCacheService) {
        return caffeineCacheService;
    }

    /**
     * 创建 Redis 缓存服务
     * 当配置 hivecloud.cache.type=redis 时生效
     *
     * @param redisCacheService Redis 缓存服务
     * @return 缓存服务实例
     */
    @Bean
    @ConditionalOnMissingBean(CacheService.class)
    @ConditionalOnProperty(prefix = "hivecloud.cache", name = "type", havingValue = "redis", matchIfMissing = false)
    public CacheService redisCacheService(RedisCacheService redisCacheService) {
        return redisCacheService;
    }

    /**
     * 创建二级缓存服务（Caffeine + Redis）
     * 默认缓存类型，结合本地缓存和分布式缓存优势
     * 当配置 hivecloud.cache.type=two_level 时生效（默认）
     *
     * @param caffeineCacheService Caffeine 缓存服务
     * @param redisCacheService Redis 缓存服务
     * @param properties 缓存配置属性
     * @return 二级缓存服务实例
     */
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