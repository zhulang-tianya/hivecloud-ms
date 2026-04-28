package com.hivecloud.system.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.system.service.SysUserService;
import com.hivecloud.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Min;

/**
 * 系统用户管理接口控制器
 * 提供用户信息查询、更新、删除等 RESTful 接口
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see SysUserService
 * @see UserVO
 */
@Slf4j
@RestController
@RequestMapping("/system/v1/user")
@Validated
@RequiredArgsConstructor
public class SysUserController {

    /**
     * 系统用户服务接口
     */
    private final SysUserService sysUserService;

    /**
     * 获取用户详细信息
     *
     * @param id 用户主键 ID，必须大于 0
     * @return 用户信息视图对象
     */
    @GetMapping("/{id}")
    public Result<UserVO> getUserInfo(@PathVariable("id") @Min(value = 1, message = "用户 ID 必须大于 0") Long id) {
        log.info("查询用户信息开始，userId:{}", id);
        UserVO user = sysUserService.getUserInfo(id);
        log.info("查询用户信息成功，userId:{}, username:{}", id, user.getUsername());
        return Result.success(user);
    }
}
