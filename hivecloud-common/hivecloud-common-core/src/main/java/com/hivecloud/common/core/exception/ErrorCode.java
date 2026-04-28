package com.hivecloud.common.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举类
 * <p>
 * 定义系统中所有可能的错误码，便于统一管理和使用
 * 错误码分类：
 * <ul>
 *   <li>1xxx：用户相关错误</li>
 *   <li>2xxx：服务相关错误</li>
 *   <li>3xxx：插件相关错误</li>
 *   <li>4xxx：业务相关错误</li>
 * </ul>
 * </p>
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    /** 通用错误码（200-503） */
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    /** 用户相关错误码（1001-1005） */
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    TOKEN_EXPIRED(1004, "Token 已过期"),
    TOKEN_INVALID(1005, "Token 无效"),

    /** 服务相关错误码（2001-2003） */
    SERVICE_NOT_FOUND(2001, "服务不存在"),
    SERVICE_UNHEALTHY(2002, "服务不健康"),
    SERVICE_REMOVED(2003, "服务已被剔除"),

    /** 插件相关错误码（3001-3003） */
    PLUGIN_NOT_FOUND(3001, "插件不存在"),
    PLUGIN_LOAD_FAILED(3002, "插件加载失败"),
    PLUGIN_INIT_FAILED(3003, "插件初始化失败");

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;
}
