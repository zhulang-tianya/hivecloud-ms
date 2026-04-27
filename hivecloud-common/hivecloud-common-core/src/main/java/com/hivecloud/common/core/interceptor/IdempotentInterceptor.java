package com.hivecloud.common.core.interceptor;

import com.hivecloud.common.core.annotation.Idempotent;
import com.hivecloud.common.core.exception.IdempotentException;
import com.hivecloud.common.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 幂等性拦截器
 * 基于 AOP 实现接口幂等性检查，使用 Redis SETNX 保证分布式环境下的原子性
 *
 * <p>工作原理：</p>
 * <ol>
 *     <li>解析 {@link Idempotent} 注解，获取幂等键表达式</li>
 *     <li>使用 SpEL 表达式解析方法参数，生成实际幂等键</li>
 *     <li>尝试使用 Redis SETNX 设置键，成功则执行业务逻辑</li>
 *     <li>业务执行成功后，根据 allowRetry 配置决定是否删除幂等键</li>
 *     <li>业务执行失败时，删除幂等键，允许重试</li>
 * </ol>
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 * @see Idempotent
 * @see RedisUtil
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotentInterceptor {

    /**
     * Redis 工具类
     */
    @Resource
    private RedisUtil redisUtil;

    /**
     * SpEL 表达式解析器
     */
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 幂等键前缀
     */
    private static final String IDEMPOTENT_PREFIX = "hivecloud:idempotent:";

    /**
     * 环绕通知
     *
     * @param joinPoint 切面连接点
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("@annotation(com.hivecloud.common.core.annotation.Idempotent)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取注解
        Idempotent idempotent = method.getAnnotation(Idempotent.class);
        if (idempotent == null) {
            return joinPoint.proceed();
        }

        // 解析幂等键
        String key = generateIdempotentKey(idempotent.key(), joinPoint);
        key = IDEMPOTENT_PREFIX + key;

        // 检查是否允许重试
        boolean allowRetry = idempotent.allowRetry();
        long expire = idempotent.expire();

        // 尝试获取分布式锁
        Boolean success = redisUtil.setIfAbsent(key, UUID.randomUUID().toString(), expire);
        if (Boolean.FALSE.equals(success)) {
            String message = idempotent.message();
            log.warn("幂等性检查失败，重复请求，key:{}, message:{}", key, message);
            throw new IdempotentException(message);
        }

        try {
            // 执行业务逻辑
            Object result = joinPoint.proceed();
            log.debug("幂等性方法执行成功，key:{}", key);

            // 如果不允许重试，立即删除幂等键
            if (!allowRetry) {
                redisUtil.delete(key);
                log.debug("幂等键已删除，key:{}", key);
            }
            // 如果允许重试，保留幂等键直到过期

            return result;
        } catch (Throwable e) {
            // 业务执行失败，删除幂等键，允许重试
            redisUtil.delete(key);
            log.warn("幂等性方法执行失败，已删除幂等键，key:{}", key, e);
            throw e;
        }
    }

    /**
     * 生成幂等键
     * 使用 SpEL 表达式解析方法参数，生成唯一的幂等键
     *
     * @param keyExpression 幂等键表达式
     * @param joinPoint 切面连接点
     * @return 生成的幂等键
     */
    private String generateIdempotentKey(String keyExpression, ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = signature.getParameterNames();

        // 创建 SpEL 上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        // 添加 request 对象到上下文
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                context.setVariable("request", request);
            }
        } catch (Exception e) {
            log.warn("获取 request 对象失败，将忽略该变量", e);
        }

        // 解析表达式
        Expression expression = parser.parseExpression(keyExpression);
        String key = expression.getValue(context, String.class);

        if (!StringUtils.hasText(key)) {
            throw new IdempotentException("幂等键不能为空，请检查 SpEL 表达式");
        }

        return key;
    }
}
