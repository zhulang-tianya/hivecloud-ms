package com.hivecloud.system.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.system.service.SysUserService;
import com.hivecloud.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统用户管理接口控制器
 * 提供用户信息查询、更新、删除等 RESTful 接口
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see SysUserService
 * @see UserVO
 */
@RestController
@RequestMapping("/system/v1/user")
@RequiredArgsConstructor
public class SysUserController {

    /**
     * 系统用户服务接口
     */
    private final SysUserService sysUserService;

    /**
     * 获取用户详细信息
     *
     * @param id 用户主键 ID
     * @return 用户信息视图对象
     */
    @GetMapping("/{id}")
    public Result<UserVO> getUserInfo(@PathVariable Long id) {
        UserVO user = sysUserService.getUserInfo(id);
        return Result.success(user);
    }
}
