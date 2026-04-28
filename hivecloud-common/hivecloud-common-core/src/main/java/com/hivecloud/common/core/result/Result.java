package com.hivecloud.common.core.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果封装类
 * <p>
 * 用于封装所有 Controller 层返回的结果，包含状态码、消息和数据
 * 支持泛型数据返回，便于前端统一处理
 * </p>
 *
 * @param <T> 数据类型
 * @author HiveCloud Team
 * @date 2026-04-27
 * @since 1.0.0
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * HTTP 状态码
     * <p>
     * 200: 成功<br>
     * 500: 服务器错误<br>
     * 401: 未授权<br>
     * 403: 禁止访问<br>
     * 400: 参数错误
     * </p>
     */
    private Integer code;

    /**
     * 响应消息
     * <p>
     * 成功时返回"操作成功"，失败时返回具体错误信息
     * </p>
     */
    private String message;

    /**
     * 响应数据
     * <p>
     * 泛型字段，支持任意类型的数据返回
     * </p>
     */
    private T data;

    /**
     * 时间戳
     * <p>
     * 响应生成的时间戳（毫秒）
     * </p>
     */
    private Long timestamp;

    /**
     * 默认构造函数
     * <p>
     * 初始化时间戳为当前系统时间
     * </p>
     */
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 带参数的构造函数
     *
     * @param code 状态码
     * @param message 响应消息
     * @param data 响应数据
     */
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应（无数据）
     *
     * @param <T> 数据类型
     * @return 成功响应结果
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 成功响应（带数据）
     *
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功响应结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功响应（带数据和自定义消息）
     *
     * @param data 响应数据
     * @param message 自定义消息
     * @param <T> 数据类型
     * @return 成功响应结果
     */
    public static <T> Result<T> success(T data, String message) {
        return new Result<>(200, message, data);
    }

    /**
     * 错误响应（默认 500 错误）
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 错误响应结果
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 错误响应（自定义错误码）
     *
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 错误响应结果
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 判断响应是否成功
     *
     * @return 成功返回 true，失败返回 false
     */
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }
}
