package com.hivecloud.system.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.system.service.SysUserService;
import com.hivecloud.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/v1/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping("/{id}")
    public Result<UserVO> getUserInfo(@PathVariable Long id) {
        UserVO user = sysUserService.getUserInfo(id);
        return Result.success(user);
    }
}
