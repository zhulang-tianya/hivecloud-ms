package com.hivecloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hivecloud.system.entity.SysUser;
import com.hivecloud.system.vo.UserVO;

import java.util.List;

/**
 * 系统用户服务接口
 * 提供用户查询、权限校验、角色管理等业务方法
 * 继承 MyBatisPlus IService，获得基础 CRUD 能力
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see IService
 * @see SysUser
 * @see UserVO
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 获取用户详细信息
     *
     * @param userId 用户主键 ID
     * @return 用户视图对象
     */
    UserVO getUserInfo(Long userId);

    /**
     * 获取用户权限列表
     *
     * @param userId 用户主键 ID
     * @return 用户权限标识列表
     */
    List<String> getPermissions(Long userId);

    /**
     * 获取用户角色列表
     *
     * @param userId 用户主键 ID
     * @return 用户角色标识列表
     */
    List<String> getRoles(Long userId);

    /**
     * 为用户分配角色
     *
     * @param userId 用户 ID
     * @param roleIds 角色 ID 列表
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 删除用户的角色关联
     *
     * @param userId 用户 ID
     */
    void removeUserRoles(Long userId);
}
