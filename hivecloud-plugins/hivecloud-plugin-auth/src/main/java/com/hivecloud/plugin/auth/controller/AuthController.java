package com.hivecloud.plugin.auth.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.plugin.auth.service.AuthService;
import com.hivecloud.plugin.auth.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/system/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginVO loginVO) {
        String token = authService.login(1L, loginVO.getUsername(), loginVO.getPassword());
        return Result.success(Map.of("token", token));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}
