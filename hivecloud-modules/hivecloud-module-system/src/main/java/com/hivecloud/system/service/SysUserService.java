package com.hivecloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hivecloud.system.entity.SysUser;
import com.hivecloud.system.vo.UserVO;

import java.util.List;

public interface SysUserService extends IService<SysUser> {

    UserVO getUserInfo(Long userId);

    List<String> getPermissions(Long userId);

    List<String> getRoles(Long userId);
}
