package com.hivecloud.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

/**
 * 访问日志过滤器
 * 记录所有通过网关的请求信息，包括请求方法、URL、IP、耗时等
 * 作为网关全局过滤器，在认证和限流之后执行
 * 实现接口：GlobalFilter, Ordered
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see GlobalFilter
 * @see Ordered
 */
@Slf4j
@Component
public class AccessLogFilter implements GlobalFilter, Ordered {

    /**
     * 执行过滤逻辑
     * 1. 记录请求开始时间
     * 2. 执行请求
     * 3. 记录请求耗时和响应状态
     *
     * @param exchange 服务器 Web 交换对象
     * @param chain 过滤器链
     * @return Mono 空响应
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Instant startTime = Instant.now();
        ServerHttpRequest request = exchange.getRequest();
        
        String method = request.getMethod().name();
        String url = request.getURI().getPath();
        String ip = getClientIp(request);
        String userId = request.getHeaders().getFirst("X-User-Id");
        String userName = request.getHeaders().getFirst("X-User-Name");
        
        log.info("【访问日志】{} {} | IP: {} | User: {}({})", 
                method, url, ip, 
                userName != null ? userName : "anonymous",
                userId != null ? userId : "-");
        
        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    Duration duration = Duration.between(startTime, Instant.now());
                    log.info("【访问日志完成】{} {} | 耗时：{}ms | 状态：{}", 
                            method, url, 
                            duration.toMillis(),
                            exchange.getResponse().getStatusCode());
                })
                .doOnError(throwable -> {
                    Duration duration = Duration.between(startTime, Instant.now());
                    log.error("【访问日志异常】{} {} | 耗时：{}ms | 错误：{}", 
                            method, url, 
                            duration.toMillis(),
                            throwable.getMessage());
                });
    }

    /**
     * 获取客户端 IP 地址
     * 优先从 X-Forwarded-For 头获取，其次从 X-Real-IP 获取，最后从 RemoteAddress 获取
     *
     * @param request 服务器 HTTP 请求对象
     * @return 客户端 IP 地址
     */
    private String getClientIp(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        
        String ip = headers.getFirst("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            }
            return ip;
        }
        
        ip = headers.getFirst("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        
        if (request.getRemoteAddress() != null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }
        
        return "unknown";
    }

    /**
     * 获取过滤器顺序
     * 在认证过滤器（-100）和限流过滤器（-200）之后执行
     *
     * @return 顺序值，越小优先级越高
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
