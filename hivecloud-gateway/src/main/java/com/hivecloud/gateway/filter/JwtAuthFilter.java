package com.hivecloud.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * JWT 认证过滤器
 * 作为网关全局过滤器，拦截所有请求进行 JWT 令牌验证
 * 支持白名单配置，验证通过则传递用户信息到下游服务
 * 实现接口：GlobalFilter, Ordered
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see GlobalFilter
 * @see Ordered
 */
@Slf4j
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    /**
     * JWT 密钥最小长度要求（HS256 算法）
     */
    private static final int MIN_SECRET_LENGTH = 32;

    /**
     * JWT 密钥，从配置文件读取
     */
    @Value("${hivecloud.gateway.jwt.secret:hivecloud-secret-key-must-be-at-least-32-chars}")
    private String jwtSecret;

    /**
     * 免认证 URL 列表，支持 Ant 风格路径匹配
     */
    @Value("${hivecloud.gateway.jwt.permit-all:/api/system/v1/login,/api/system/v1/register}")
    private List<String> permitAllUrls;

    /**
     * Ant 路径匹配器
     */
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * JWT 签名密钥
     */
    private SecretKey signingKey;

    /**
     * 初始化方法，校验 JWT 密钥长度并生成签名密钥
     */
    @jakarta.annotation.PostConstruct
    public void init() {
        if (jwtSecret.length() < MIN_SECRET_LENGTH) {
            throw new IllegalArgumentException(
                    "JWT secret must be at least " + MIN_SECRET_LENGTH + " characters for HS256 algorithm"
            );
        }
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 执行过滤逻辑
     * 1. 检查是否为白名单 URL
     * 2. 提取并验证 JWT Token
     * 3. 验证通过则传递用户信息到下游服务
     *
     * @param exchange 服务器 Web 交换对象
     * @param chain 过滤器链
     * @return Mono 空响应
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (isPermitAll(path)) {
            return chain.filter(exchange);
        }

        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (token == null || !token.startsWith("Bearer ")) {
            return unauthorizedResponse(exchange, "未认证");
        }

        token = token.substring(7);
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String userId = claims.get("userId", String.class);
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Name", claims.getSubject())
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return unauthorizedResponse(exchange, "Token无效或已过期");
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private boolean isPermitAll(String path) {
        return permitAllUrls.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String body = "{\"code\":401,\"message\":\"" + message + "\",\"data\":null,\"timestamp\":" + System.currentTimeMillis() + "}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8))));
    }
}
