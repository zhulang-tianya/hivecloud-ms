package com.hivecloud.system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j 在线文档配置
 * 自动生成 API 文档，支持在线调试
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Configuration
public class Knife4jConfig {

    /**
     * 配置 OpenAPI 文档信息
     *
     * @return OpenAPI 对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HiveCloud 系统管理 API")
                        .version("1.0.0")
                        .description("多租户 RBAC 权限管理系统 - 系统管理模块 API 文档")
                        .contact(new Contact()
                                .name("HiveCloud Team")
                                .email("support@hivecloud.com")
                                .url("https://www.hivecloud.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .schemaRequirement("BearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT Token 认证，格式：Bearer {token}"))
                .security(java.util.Arrays.asList(new SecurityRequirement().addList("BearerAuth")));
    }
}
