package com.hivecloud.plugin.doc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j 在线文档配置
 * 自动生成 API 文档，支持在线调试
 *
 * @author HiveCloud Team
 * @date 2026-04-27
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
                        .title("HiveCloud API 文档")
                        .version("1.0.0")
                        .description("HiveCloud 微服务架构 API 接口文档")
                        .contact(new Contact()
                                .name("HiveCloud Team")
                                .email("support@hivecloud.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
