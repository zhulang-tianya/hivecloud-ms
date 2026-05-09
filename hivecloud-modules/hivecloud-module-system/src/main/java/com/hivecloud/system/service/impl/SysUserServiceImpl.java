package com.hivecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hivecloud.system.entity.SysMenu;
import com.hivecloud.system.entity.SysRole;
import com.hivecloud.system.entity.SysUser;
import com.hivecloud.system.entity.SysUserRole;
import com.hivecloud.system.mapper.SysMenuMapper;
import com.hivecloud.system.mapper.SysRoleMapper;
import com.hivecloud.system.mapper.SysUserMapper;
import com.hivecloud.system.mapper.SysUserRoleMapper;
import com.hivecloud.system.mapper.UserMapper;
import com.hivecloud.system.context.TenantContext;
import com.hivecloud.system.service.SysUserService;
import com.hivecloud.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;
    private final com.hivecloud.system.mapper.SysRoleMenuMapper sysRoleMenuMapper;

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
        
        UserVO userVO = UserMapper.INSTANCE.toVO(user);
        userVO.setRoles(getRoles(userId));
        userVO.setPermissions(getPermissions(userId));
        
        return userVO;
    }

    /**
     * 获取用户权限列表
     * 通过用户 -> 角色 -> 菜单的关联关系获取权限标识
     *
     * @param userId 用户主键 ID
     * @return 用户权限标识列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getPermissions(Long userId) {
        // 1. 查询用户关联的角色 ID 列表
        List<Long> roleIds = sysUserRoleMapper.selectRoleIdsByUserId(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }

        // 2. 查询角色关联的菜单 ID 列表
        Set<Long> menuIdSet = roleIds.stream()
                .flatMap(roleId -> sysRoleMenuMapper.selectMenuIdsByRoleId(roleId).stream())
                .collect(Collectors.toSet());

        if (menuIdSet.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 查询菜单的权限标识
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysMenu::getId, menuIdSet)
                .isNotNull(SysMenu::getPerms)
                .ne(SysMenu::getPerms, "")
                .eq(SysMenu::getStatus, 1);
        
        List<SysMenu> menus = sysMenuMapper.selectList(wrapper);
        return menus.stream()
                .map(SysMenu::getPerms)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 获取用户角色列表
     * 通过用户 -> 角色关联获取角色编码
     *
     * @param userId 用户主键 ID
     * @return 用户角色编码列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getRoles(Long userId) {
        // 1. 查询用户关联的角色 ID 列表
        List<Long> roleIds = sysUserRoleMapper.selectRoleIdsByUserId(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }

        // 2. 查询角色详情
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysRole::getId, roleIds)
                .eq(SysRole::getStatus, 1);
        
        List<SysRole> roles = sysRoleMapper.selectList(wrapper);
        return roles.stream()
                .map(SysRole::getRoleCode)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 1. 删除旧的角色关联
        sysUserRoleMapper.deleteByUserId(userId);

        // 2. 添加新的角色关联
        if (!CollectionUtils.isEmpty(roleIds)) {
            Long tenantId = TenantContext.getTenantId();
            List<SysUserRole> userRoles = roleIds.stream()
                    .map(roleId -> {
                        SysUserRole userRole = new SysUserRole();
                        userRole.setTenantId(tenantId);
                        userRole.setUserId(userId);
                        userRole.setRoleId(roleId);
                        return userRole;
                    })
                    .collect(Collectors.toList());
            
            sysUserRoleMapper.batchSave(userRoles);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserRoles(Long userId) {
        sysUserRoleMapper.deleteByUserId(userId);
    }
}
