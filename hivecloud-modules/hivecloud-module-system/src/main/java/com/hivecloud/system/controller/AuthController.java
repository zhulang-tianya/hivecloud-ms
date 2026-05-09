package com.hivecloud.system.controller;

import com.hivecloud.system.service.AuthService;
import com.hivecloud.system.util.CaptchaUtil;
import com.hivecloud.system.util.CaptchaUtil.CaptchaData;
import com.hivecloud.system.vo.LoginRequest;
import com.hivecloud.system.vo.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 提供用户登录、注销、验证码等接口
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录、注销、验证码等认证相关接口")
public class AuthController {

    private final AuthService authService;
    private final CaptchaUtil captchaUtil;

    /**
     * 获取验证码
     *
     * @return 验证码 Key 和 Base64 图片
     */
    @GetMapping("/captcha")
    @Operation(summary = "获取验证码", description = "生成图形验证码，返回 Base64 编码的图片和验证码 Key")
    public ResponseEntity<Map<String, Object>> getCaptcha() {
        CaptchaData captchaData = captchaUtil.generateCaptcha();

        Map<String, Object> result = new HashMap<>();
        result.put("captchaKey", captchaData.getCaptchaKey());
        result.put("captchaImage", "data:image/png;base64," + captchaData.getCaptchaImage());
        result.put("expiration", captchaData.getExpiration());

        return ResponseEntity.ok(result);
    }

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return Token 和用户信息
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录，返回 JWT Token 和用户信息")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 用户注销
     *
     * @param authorization Authorization Header (Bearer {token})
     */
    @PostMapping("/logout")
    @Operation(summary = "用户注销", description = "使当前 Token 失效")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorization) {
        authService.logout(authorization);
        return ResponseEntity.ok().build();
    }

    /**
     * 刷新 Token
     *
     * @param authorization Authorization Header (Bearer {token})
     * @return 新 Token
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新 Token", description = "在 Token 过期前 5 分钟内访问可自动刷新")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7); // 移除 "Bearer " 前缀
        String newToken = authService.refreshToken(token);
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", newToken);
        result.put("expiresIn", 1800); // 30 分钟

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-New-Token", newToken); // 通过响应头返回新 Token
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(result);
    }
}
