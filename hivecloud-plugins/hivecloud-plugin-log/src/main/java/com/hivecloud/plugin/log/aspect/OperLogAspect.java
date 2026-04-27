package com.hivecloud.plugin.log.aspect;

import com.hivecloud.plugin.log.annotation.OperLog;
import com.hivecloud.plugin.log.entity.SysOperLog;
import com.hivecloud.plugin.log.mapper.OperLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collection;
import java.util.Map;

/**
 * 操作日志切面处理器
 * 拦截标注@OperLog 注解的方法，记录操作日志到数据库
 * 支持正常返回和异常返回两种场景
 * 使用 AOP 切面编程实现日志记录
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see OperLog
 * @see SysOperLog
 * @see OperLogMapper
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperLogAspect {

    /**
     * 操作日志 Mapper 接口，用于数据库操作
     */
    private final OperLogMapper operLogMapper;

    /**
     * 后置返回通知
     * 在方法正常返回后执行，记录操作日志
     *
     * @param joinPoint 切点信息
     * @param controllerLog 操作日志注解
     * @param jsonResult 方法返回值
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, OperLog controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    /**
     * 后置异常通知
     * 在方法抛出异常后执行，记录异常日志
     *
     * @param joinPoint 切点信息
     * @param controllerLog 操作日志注解
     * @param e 异常对象
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, OperLog controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }

    /**
     * 处理操作日志
     * 封装操作日志信息并保存到数据库
     *
     * @param joinPoint 切点信息
     * @param controllerLog 操作日志注解
     * @param e 异常对象，正常返回时为 null
     * @param jsonResult 方法返回值，异常时为 null
     */
    protected void handleLog(final JoinPoint joinPoint, OperLog controllerLog, final Exception e, Object jsonResult) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            HttpServletRequest request = attributes.getRequest();

            SysOperLog operLog = new SysOperLog();
            operLog.setTitle(controllerLog.title());
            operLog.setBusinessType(controllerLog.businessType());
            operLog.setMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
            operLog.setRequestMethod(request.getMethod());
            operLog.setOperUrl(request.getRequestURI());
            operLog.setOperIp(request.getRemoteAddr());

            if (controllerLog.isSaveRequestData()) {
                String params = getRequestParams(joinPoint, request);
                operLog.setOperParam(truncate(params, 2000));
            }

            if (controllerLog.isSaveResponseData() && jsonResult != null) {
                operLog.setJsonResult(truncate(jsonResult.toString(), 2000));
            }

            if (e != null) {
                operLog.setStatus(1);
                operLog.setErrorMsg(truncate(e.getMessage(), 2000));
            } else {
                operLog.setStatus(0);
            }

            operLog.setOperTime(System.currentTimeMillis());
            operLogMapper.insert(operLog);
        } catch (Exception exp) {
            log.error("Failed to save operation log", exp);
        }
    }

    private String getRequestParams(JoinPoint joinPoint, HttpServletRequest request) {
        if (HttpMethod.GET.name().equals(request.getMethod()) || HttpMethod.DELETE.name().equals(request.getMethod())) {
            return request.getQueryString();
        } else {
            Object[] args = joinPoint.getArgs();
            StringBuilder params = new StringBuilder();
            for (Object arg : args) {
                if (arg != null && !isFilterObject(arg)) {
                    params.append(arg.toString()).append(" ");
                }
            }
            return params.toString();
        }
    }

    private boolean isFilterObject(final Object o) {
        return o instanceof HttpServletRequest || o instanceof HttpServletResponse || o instanceof BindingResult;
    }

    private String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
}
