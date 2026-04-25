package com.hivecloud.plugin.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hivecloud.cache")
public class CacheProperties {

    private CacheType type = CacheType.TWO_LEVEL;

    private Local local = new Local();

    private String keyPrefix = "hivecloud";

    @Data
    public static class Local {
        private long maxSize = 1000;
        private long expireAfterWrite = 300;
    }

    public enum CacheType {
        LOCAL,
        REDIS,
        TWO_LEVEL
    }
}