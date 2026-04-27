package com.hivecloud.common.core.exception;

/**
 * 幂等性异常
 * 当接口幂等性检查失败时抛出此异常
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
public class IdempotentException extends RuntimeException {

    /**
     * 错误码
     */
    private final String code;

    /**
     * 构造函数
     *
     * @param message 错误信息
     */
    public IdempotentException(String message) {
        super(message);
        this.code = "IDEMPOTENT_ERROR";
    }

    /**
     * 构造函数
     *
     * @param code 错误码
     * @param message 错误信息
     */
    public IdempotentException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造函数
     *
     * @param message 错误信息
     * @param cause 异常原因
     */
    public IdempotentException(String message, Throwable cause) {
        super(message, cause);
        this.code = "IDEMPOTENT_ERROR";
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public String getCode() {
        return code;
    }
}
