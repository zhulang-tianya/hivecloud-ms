package com.hivecloud.plugin.auth.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.plugin.auth.service.AuthService;
import com.hivecloud.plugin.auth.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

/**
 * 认证授权控制器
 * 提供用户登录、登出等认证接口
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
    public Result<Map<String, String>> login(@RequestBody @Valid LoginVO loginVO) {
        log.info("用户登录请求，username:{}", loginVO.getUsername());
        String token = authService.login(1L, loginVO.getUsername(), loginVO.getPassword());
        log.info("用户登录成功，username:{}, token:{}", loginVO.getUsername(), token);
        return Result.success(Map.of("token", token));
    }

    /**
     * 用户登出
     *
     * @return 操作结果
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        log.info("用户登出请求");
        return Result.success();
    }
}
