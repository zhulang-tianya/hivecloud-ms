package com.hivecloud.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "hivecloud.gateway")
public class GatewayRouteConfig {

    private List<RouteDefinition> routes;

    @Data
    public static class RouteDefinition {
        private String id;
        private String uri;
        private String predicates;
        private Map<String, String> metadata;
    }
}
