package com.hivecloud.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 网关路由配置类
 * 从配置文件读取 hivecloud.gateway 前缀的路由配置
 * 支持动态配置路由规则
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see ConfigurationProperties
 */
@Data
@Component
@ConfigurationProperties(prefix = "hivecloud.gateway")
public class GatewayRouteConfig {

    /**
     * 路由规则列表
     */
    private List<RouteDefinition> routes;

    /**
     * 路由定义内部类
     */
    @Data
    public static class RouteDefinition {
        /**
         * 路由 ID
         */
        private String id;

        /**
         * 路由 URI
         */
        private String uri;

        /**
         * 断言规则
         */
        private String predicates;

        /**
         * 路由元数据
         */
        private Map<String, String> metadata;
    }
}
