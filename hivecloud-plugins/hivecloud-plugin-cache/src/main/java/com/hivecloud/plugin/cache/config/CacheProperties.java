package com.hivecloud.plugin.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 缓存配置属性类
 * 从配置文件读取 hivecloud.cache 前缀的配置项
 * 支持配置缓存类型、本地缓存参数、Key 前缀等
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see ConfigurationProperties
 */
@Data
@ConfigurationProperties(prefix = "hivecloud.cache")
public class CacheProperties {

    /**
     * 缓存类型，默认为二级缓存
     */
    private CacheType type = CacheType.TWO_LEVEL;

    /**
     * 本地缓存配置
     */
    private Local local = new Local();

    /**
     * 缓存 Key 前缀，用于隔离不同业务缓存
     */
    private String keyPrefix = "hivecloud";

    /**
     * 本地缓存配置内部类
     */
    @Data
    public static class Local {
        /**
         * 最大缓存条目数
         */
        private long maxSize = 1000;

        /**
         * 写入后过期时间（秒）
         */
        private long expireAfterWrite = 300;
    }

    /**
     * 缓存类型枚举
     */
    public enum CacheType {
        /**
         * 本地缓存
         */
        LOCAL,
        /**
         * 分布式缓存
         */
        REDIS,
        /**
         * 二级缓存（本地 + 分布式）
         */
        TWO_LEVEL
    }
}