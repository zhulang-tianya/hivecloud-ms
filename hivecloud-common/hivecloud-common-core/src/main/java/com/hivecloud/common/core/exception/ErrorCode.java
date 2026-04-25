package com.hivecloud.common.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    TOKEN_EXPIRED(1004, "Token已过期"),
    TOKEN_INVALID(1005, "Token无效"),

    SERVICE_NOT_FOUND(2001, "服务不存在"),
    SERVICE_UNHEALTHY(2002, "服务不健康"),
    SERVICE_REMOVED(2003, "服务已被剔除"),

    PLUGIN_NOT_FOUND(3001, "插件不存在"),
    PLUGIN_LOAD_FAILED(3002, "插件加载失败"),
    PLUGIN_INIT_FAILED(3003, "插件初始化失败");

    private final Integer code;
    private final String message;
}
