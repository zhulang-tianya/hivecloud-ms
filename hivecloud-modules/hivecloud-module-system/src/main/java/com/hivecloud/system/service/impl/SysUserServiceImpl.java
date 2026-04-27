package com.hivecloud.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hivecloud.system.entity.SysUser;
import com.hivecloud.system.mapper.SysUserMapper;
import com.hivecloud.system.mapper.UserMapper;
import com.hivecloud.system.service.SysUserService;
import com.hivecloud.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 系统用户服务实现类
 * 实现用户查询、权限校验、角色管理等业务逻辑
 * 继承 MyBatisPlus ServiceImpl，提供基础 CRUD 能力
 * 实现接口：SysUserService
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see SysUserService
 * @see SysUser
 * @see UserVO
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    /**
     * 获取用户详细信息
     * 使用 MapStruct 映射器将 SysUser 实体转换为 UserVO 视图对象
     * 只读操作，使用 readOnly = true 优化性能
     *
     * @param userId 用户主键 ID
     * @return 用户视图对象，用户不存在时返回 null
     */
    @Override
    @Transactional(readOnly = true)
    public UserVO getUserInfo(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            return null;
        }
        return UserMapper.INSTANCE.toVO(user);
    }

    /**
     * 获取用户权限列表
     * 当前返回空列表，待后续实现权限管理功能
     *
     * @param userId 用户主键 ID
     * @return 用户权限标识列表
     */
    @Override
    public List<String> getPermissions(Long userId) {
        return Collections.emptyList();
    }

    /**
     * 获取用户角色列表
     * 当前返回空列表，待后续实现角色管理功能
     *
     * @param userId 用户主键 ID
     * @return 用户角色标识列表
     */
    @Override
    public List<String> getRoles(Long userId) {
        return Collections.emptyList();
    }
}
