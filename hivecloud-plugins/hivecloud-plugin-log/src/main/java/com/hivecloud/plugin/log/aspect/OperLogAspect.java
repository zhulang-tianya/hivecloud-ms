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

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperLogAspect {

    private final OperLogMapper operLogMapper;

    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, OperLog controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, OperLog controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }

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
