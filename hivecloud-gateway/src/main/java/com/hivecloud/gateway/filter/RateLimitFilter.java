package com.hivecloud.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 限流过滤器
 * 基于 Redis 实现滑动窗口限流算法
 * 限制每个客户端 IP 在指定时间内的请求次数
 * 实现接口：GlobalFilter, Ordered
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see GlobalFilter
 * @see Ordered
 */
@Slf4j
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    /**
     * Redis 模板，用于操作 Redis 存储
     */
    private final ReactiveStringRedisTemplate redisTemplate;

    /**
     * 默认限流阈值：100 次请求
     */
    private static final int DEFAULT_LIMIT = 100;

    /**
     * 默认时间窗口：60 秒
     */
    private static final Duration DEFAULT_WINDOW = Duration.ofSeconds(60);

    /**
     * 构造函数
     *
     * @param redisTemplate Redis 模板
     */
    public RateLimitFilter(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取过滤器顺序
     *
     * @return 顺序值，越小优先级越高
     */
    @Override
    public int getOrder() {
        return -200;
    }

    /**
     * 执行过滤逻辑
     * 1. 获取客户端 IP
     * 2. 使用 Redis 计数器限流
     * 3. 超过阈值则返回 429 错误
     *
     * @param exchange 服务器 Web 交换对象
     * @param chain 过滤器链
     * @return Mono 空响应
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientIp = exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";

        String key = "hivecloud:ratelimit:" + clientIp;

        return redisTemplate.opsForValue().increment(key)
                .flatMap(count -> {
                    if (count == 1) {
                        redisTemplate.expire(key, DEFAULT_WINDOW).subscribe();
                    }
                    if (count > DEFAULT_LIMIT) {
                        return tooManyRequestsResponse(exchange);
                    }
                    return chain.filter(exchange);
                })
                .onErrorResume(e -> {
                    log.warn("Rate limiter Redis error, fallback to pass request: {}", e.getMessage());
                    return chain.filter(exchange);
                });
    }

    private Mono<Void> tooManyRequestsResponse(ServerWebExchange exchange) {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String body = "{\"code\":429,\"message\":\"请求过于频繁\",\"data\":null,\"timestamp\":" + System.currentTimeMillis() + "}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8))));
    }
}
