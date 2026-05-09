package com.hivecloud.system.filter;

import com.hivecloud.system.context.TenantContext;
import com.hivecloud.system.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 * 从请求头提取 Token，验证有效性，设置用户上下文
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        
        // 没有 Token，直接放行（由具体接口的权限控制决定是否需要认证）
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        try {
            // 1. 验证 Token 是否有效
            if (!jwtUtil.validateToken(token)) {
                log.warn("Token 无效或已过期");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"msg\":\"Token 无效或已过期\"}");
                return;
            }

            // 2. 检查 Token 是否在黑名单中
            String blacklistKey = "token:blacklist:" + token;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey))) {
                log.warn("Token 已注销");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"msg\":\"Token 已注销\"}");
                return;
            }

            // 3. 设置用户上下文
            Long userId = jwtUtil.getUserId(token);
            Long tenantId = jwtUtil.getTenantId(token);
            String username = jwtUtil.getUsername(token);

            TenantContext.setUserId(userId);
            TenantContext.setTenantId(tenantId);
            TenantContext.setUsername(username);

            log.debug("Token 验证通过：userId={}, username={}, tenantId={}", userId, username, tenantId);

            // 4. 判断是否需要刷新 Token（在响应头返回新 Token）
            if (jwtUtil.shouldRefresh(token)) {
                String newToken = jwtUtil.refreshToken(token);
                response.setHeader("X-New-Token", newToken);
                log.debug("Token 已自动刷新");
            }

        } catch (Exception e) {
            log.error("Token 验证失败：{}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"Token 验证失败\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
