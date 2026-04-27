package com.hivecloud.gateway.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 网关全局异常处理器
 * 统一处理网关层的各类异常，返回标准化 JSON 错误响应
 * 支持 ResponseStatusException 和普通异常的处理
 * 实现接口：ErrorWebExceptionHandler
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see ErrorWebExceptionHandler
 * @see ResponseStatusException
 */
@Slf4j
@Order(-1)
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    /**
     * 处理网关异常
     * 1. 检查响应是否已提交
     * 2. 根据异常类型设置状态码和消息
     * 3. 返回统一的 JSON 格式错误响应
     *
     * @param exchange 服务器 Web 交换对象
     * @param ex 异常对象
     * @return Mono 空响应
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus status;
        String message;

        if (ex instanceof ResponseStatusException) {
            status = (HttpStatus) ((ResponseStatusException) ex).getStatusCode();
            message = ex.getMessage();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "服务器内部错误";
            log.error("Gateway exception: ", ex);
        }

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"code\":%d,\"message\":\"%s\",\"data\":null,\"timestamp\":%d}",
                status.value(), message, System.currentTimeMillis()
        );

        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            return bufferFactory.wrap(body.getBytes(StandardCharsets.UTF_8));
        }));
    }
}
