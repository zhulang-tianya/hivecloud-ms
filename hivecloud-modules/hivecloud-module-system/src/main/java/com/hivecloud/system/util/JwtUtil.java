package com.hivecloud.system.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 提供 Token 生成、解析、验证等功能
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret:HiveCloud-Secret-Key-2026-For-JWT-Token-Signing}")
    private String secret;

    @Value("${jwt.expiration:1800000}")
    private Long expiration;

    @Value("${jwt.refresh-expiration:300000}")
    private Long refreshExpiration;

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 JWT Token
     *
     * @param userId 用户 ID
     * @param username 用户名
     * @param tenantId 租户 ID
     * @return JWT Token
     */
    public String generateToken(Long userId, String username, Long tenantId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("tenantId", tenantId);

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 JWT Token
     *
     * @param token JWT Token
     * @return Claims 对象
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 Token 中获取用户 ID
     *
     * @param token JWT Token
     * @return 用户 ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从 Token 中获取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 从 Token 中获取租户 ID
     *
     * @param token JWT Token
     * @return 租户 ID
     */
    public Long getTenantId(String token) {
        Claims claims = parseToken(token);
        return claims.get("tenantId", Long.class);
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT Token
     * @return true-有效，false-无效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            Date expireDate = claims.getExpiration();
            return !expireDate.before(new Date());
        } catch (Exception e) {
            log.error("验证 Token 失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查 Token 是否已过期
     *
     * @param token JWT Token
     * @return true-已过期，false-未过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            Date expireDate = claims.getExpiration();
            return expireDate.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 判断是否需要刷新 Token（在过期前 5 分钟内）
     *
     * @param token JWT Token
     * @return true-需要刷新，false-不需要
     */
    public boolean shouldRefresh(String token) {
        try {
            Claims claims = parseToken(token);
            Date expireDate = claims.getExpiration();
            long remainingTime = expireDate.getTime() - System.currentTimeMillis();
            return remainingTime < refreshExpiration;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 刷新 Token
     *
     * @param token 原 Token
     * @return 新 Token
     */
    public String refreshToken(String token) {
        Claims claims = parseToken(token);
        
        Long userId = claims.get("userId", Long.class);
        String username = claims.getSubject();
        Long tenantId = claims.get("tenantId", Long.class);

        return generateToken(userId, username, tenantId);
    }

    /**
     * 获取 Token 剩余有效期（秒）
     *
     * @param token JWT Token
     * @return 剩余秒数
     */
    public long getRemainingTime(String token) {
        try {
            Claims claims = parseToken(token);
            Date expireDate = claims.getExpiration();
            return (expireDate.getTime() - System.currentTimeMillis()) / 1000;
        } catch (Exception e) {
            return 0;
        }
    }
}
