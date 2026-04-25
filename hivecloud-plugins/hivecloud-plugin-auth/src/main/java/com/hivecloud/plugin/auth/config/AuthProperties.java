package com.hivecloud.plugin.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hivecloud.auth")
public class AuthProperties {

    private String secret = "hivecloud-secret-key-must-be-at-least-256-bits-long-for-hs256";

    private long expiration = 86400000;

    private String tokenHeader = "Authorization";

    private String tokenPrefix = "Bearer ";

    private String[] permitAllUrls = {
            "/api/system/v1/login",
            "/api/system/v1/register",
            "/doc.html",
            "/webjars/**",
            "/swagger-resources/**",
            "/v3/api-docs/**"
    };
}
