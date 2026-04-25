package com.hivecloud.plugin.auth.aspect;

import com.hivecloud.common.core.exception.BusinessException;
import com.hivecloud.common.core.exception.ErrorCode;
import com.hivecloud.plugin.auth.annotation.RequirePermission;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class PermissionAspect {

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED.getCode(), "未认证");
        }

        Set<String> userPermissions = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        String[] requiredPermissions = requirePermission.value();
        String logical = requirePermission.logical();

        boolean hasPermission;
        if ("OR".equalsIgnoreCase(logical)) {
            hasPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(userPermissions::contains);
        } else {
            hasPermission = Arrays.stream(requiredPermissions)
                    .allMatch(userPermissions::contains);
        }

        if (!hasPermission) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "权限不足");
        }

        return joinPoint.proceed();
    }
}
