package com.hivecloud.common.core.exception;

import lombok.Getter;

/**
 * 业务异常类
 * <p>
 * 用于封装业务逻辑中的异常信息，包含错误码和错误消息
 * 继承自 RuntimeException，支持 unchecked 异常处理
 * </p>
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 * @since 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 构造函数（默认 500 错误）
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 构造函数（自定义错误码）
     *
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造函数（使用 ErrorCode 枚举）
     *
     * @param errorCode 错误码枚举
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 构造函数（使用 ErrorCode 枚举和自定义消息）
     *
     * @param errorCode 错误码枚举
     * @param message 自定义错误消息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}
