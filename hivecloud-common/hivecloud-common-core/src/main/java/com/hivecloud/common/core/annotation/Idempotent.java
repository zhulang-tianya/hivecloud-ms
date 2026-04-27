package com.hivecloud.common.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 幂等性注解
 * 用于标记需要保证幂等性的接口方法
 * 基于 Redis SETNX 实现分布式锁机制，确保同一请求只会被执行一次
 *
 * <p>使用示例：</p>
 * <pre>
 * {@code
 * @Idempotent(key = "'order:create:' + #userId + ':' + #timestamp", expire = 86400)
 * @PostMapping("/orders")
 * public Result<Long> createOrder(@RequestBody OrderRequest request) {
 *     // 业务逻辑
 * }
 * }
 * </pre>
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 * @see com.hivecloud.common.core.interceptor.IdempotentInterceptor
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * 幂等键的 SpEL 表达式
     * <p>支持 Spring EL 表达式，可引用方法参数</p>
     * <p>示例：</p>
     * <ul>
     *     <li>"'user:login:' + #username"</li>
     *     <li>"'order:create:' + #userId + ':' + #request.orderNo"</li>
     *     <li>"'dict:refresh:' + #dictType"</li>
     * </ul>
     *
     * @return 幂等键表达式
     */
    String key();

    /**
     * 幂等键过期时间（秒）
     * <p>默认 24 小时（86400 秒）</p>
     * <p>根据业务场景调整，防止 Redis 内存溢出</p>
     *
     * @return 过期时间
     */
    long expire() default 86400;

    /**
     * 错误提示信息
     * <p>当幂等性检查失败时返回的错误信息</p>
     *
     * @return 错误信息
     */
    String message() default "请求已处理，请勿重复提交";

    /**
     * 是否允许重试
     * <p>true: 允许重试，首次执行成功后，后续相同请求直接返回成功</p>
     * <p>false: 不允许重试，首次执行成功后，后续相同请求抛出异常</p>
     * <p>默认 false，严格幂等模式</p>
     *
     * @return 是否允许重试
     */
    boolean allowRetry() default false;
}
