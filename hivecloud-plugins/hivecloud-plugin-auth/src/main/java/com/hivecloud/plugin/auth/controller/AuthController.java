package com.hivecloud.plugin.auth.controller;

import com.hivecloud.common.core.annotation.Idempotent;
import com.hivecloud.common.core.result.Result;
import com.hivecloud.plugin.auth.service.AuthService;
import com.hivecloud.plugin.auth.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 认证授权控制器
 * <p>
 * 提供用户登录、登出等认证接口
 * </p>
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see AuthService
 * @see LoginVO
 */
@Slf4j
@RestController
@RequestMapping("/system/v1")
@Validated
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录、登出等认证接口")
public class AuthController {

    /**
     * 认证服务接口
     */
    private final AuthService authService;

    /**
     * 用户登录
     *
     * @param loginVO 登录请求参数
     * @return 包含 token 的响应结果
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录获取访问令牌")
    @Parameter(name = "loginVO", description = "登录请求参数", required = true)
    public Result<Map<String, String>> login(@RequestBody @Valid LoginVO loginVO) {
        log.info("用户登录请求，username:{}", loginVO.getUsername());
        String token = authService.login(1L, loginVO.getUsername(), loginVO.getPassword());
        log.info("用户登录成功，username:{}, token:{}", loginVO.getUsername(), token);
        return Result.success(Map.of("token", token));
    }

    /**
     * 用户登出
     * 幂等接口，防止重复登出导致的问题
     *
     * @return 操作结果
     */
    @Idempotent(key = "'auth:logout:' + #request.remoteAddr", expire = 60)
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出清除安全上下文")
    public Result<Void> logout() {
        log.info("用户登出请求");
        // 清除安全上下文
        SecurityContextHolder.clearContext();
        return Result.success();
    }
}
