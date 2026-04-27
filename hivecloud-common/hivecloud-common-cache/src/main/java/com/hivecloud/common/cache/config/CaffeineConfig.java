package com.hivecloud.common.cache.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine 本地缓存配置
 * 配置不同业务场景的缓存参数，提升访问性能
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Configuration
public class CaffeineConfig {

    /**
     * 服务列表缓存
     * 最大 1000 个条目，过期时间 30 秒
     * 用于缓存服务注册与发现的服务列表
     *
     * @return 服务列表缓存
     */
    @Bean
    public Cache<String, List<Object>> serviceListCache() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .recordStats()
                .build();
    }

    /**
     * 用户信息缓存
     * 最大 5000 个条目，过期时间 5 分钟
     * 用于缓存用户基本信息，减少数据库查询
     *
     * @return 用户信息缓存
     */
    @Bean
    public Cache<Long, Object> userInfoCache() {
        return Caffeine.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats()
                .build();
    }

    /**
     * 字典数据缓存
     * 最大 500 个条目，过期时间 10 分钟
     * 用于缓存字典类型和字典数据
     *
     * @return 字典数据缓存
     */
    @Bean
    public Cache<String, Object> dictDataCache() {
        return Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats()
                .build();
    }

    /**
     * 操作日志缓存
     * 最大 2000 个条目，过期时间 1 分钟
     * 用于临时缓存操作日志，异步写入数据库
     *
     * @return 操作日志缓存
     */
    @Bean
    public Cache<String, Object> operLogCache() {
        return Caffeine.newBuilder()
                .maximumSize(2000)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .recordStats()
                .build();
    }
}
