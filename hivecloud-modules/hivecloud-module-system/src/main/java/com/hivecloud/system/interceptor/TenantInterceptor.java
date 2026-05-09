package com.hivecloud.system.interceptor;

import com.hivecloud.system.context.TenantContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 租户拦截器
 * 从请求头或 Token 中解析租户 ID，并设置到上下文中
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {

    /**
     * 租户 ID 请求头名称
     */
    private static final String TENANT_ID_HEADER = "X-Tenant-ID";

    /**
     * 在请求处理之前执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {
        // 从请求头获取租户 ID
        String tenantId = request.getHeader(TENANT_ID_HEADER);

        if (!StringUtils.hasText(tenantId)) {
            // 从 Token 中解析租户 ID（如果有 JWT 认证）
            String token = request.getHeader("Authorization");
            if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
                // TODO: 从 JWT Token 中解析租户 ID
                // tenantId = JwtUtil.getTenantId(token.substring(7));
            }
        }

        // 设置租户 ID 到上下文
        if (StringUtils.hasText(tenantId)) {
            TenantContext.setTenantId(Long.parseLong(tenantId));
        } else {
            // 设置默认租户 ID
            TenantContext.setTenantId(1L);
        }

        return true;
    }

    /**
     * 在请求完成之后执行
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                              HttpServletResponse response,
                              Object handler,
                              Exception ex) throws Exception {
        // 清理租户上下文，防止内存泄漏
        TenantContext.clear();
    }
}
