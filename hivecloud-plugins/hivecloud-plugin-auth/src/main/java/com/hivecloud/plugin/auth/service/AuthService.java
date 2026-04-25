package com.hivecloud.plugin.auth.service;

import com.hivecloud.plugin.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_BLACKLIST_KEY = "hivecloud:auth:token:blacklist:";

    public String login(Long userId, String username, String password) {
        return jwtUtil.generateToken(userId, username, Map.of("roles", "admin"));
    }

    public void logout(String token) {
        long expiration = jwtUtil.parseToken(token).getExpiration().getTime() - System.currentTimeMillis();
        if (expiration > 0) {
            redisTemplate.opsForValue().set(TOKEN_BLACKLIST_KEY + token, "1", expiration, TimeUnit.MILLISECONDS);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(TOKEN_BLACKLIST_KEY + token));
    }
}
