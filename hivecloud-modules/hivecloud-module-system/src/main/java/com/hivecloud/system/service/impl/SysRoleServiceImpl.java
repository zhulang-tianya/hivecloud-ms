package com.hivecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hivecloud.system.context.TenantContext;
import com.hivecloud.system.entity.SysRole;
import com.hivecloud.system.entity.SysRoleMenu;
import com.hivecloud.system.entity.SysUser;
import com.hivecloud.system.mapper.SysRoleMapper;
import com.hivecloud.system.mapper.SysRoleMenuMapper;
import com.hivecloud.system.mapper.SysUserMapper;
import com.hivecloud.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色 Service 实现类
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(role.getTenantId() != null, SysRole::getTenantId, role.getTenantId())
                .eq(role.getStatus() != null, SysRole::getStatus, role.getStatus())
                .like(role.getRoleName() != null, SysRole::getRoleName, role.getRoleName())
                .like(role.getRoleCode() != null, SysRole::getRoleCode, role.getRoleCode())
                .orderByDesc(SysRole::getCreateTime);
        return list(wrapper);
    }

    @Override
    public SysRole selectRoleById(Long id) {
        return getById(id);
    }

    @Override
    public SysRole selectRoleByCode(String roleCode) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, roleCode);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRole(SysRole role) {
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        return save(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(SysRole role) {
        role.setUpdateTime(LocalDateTime.now());
        return updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRoleByIds(Long[] roleIds) {
        List<Long> ids = Arrays.asList(roleIds);
        return removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        // 1. 删除旧的菜单关联
        sysRoleMenuMapper.deleteByRoleId(roleId);

        // 2. 添加新的菜单关联
        if (!CollectionUtils.isEmpty(menuIds)) {
            Long tenantId = TenantContext.getTenantId();
            List<SysRoleMenu> roleMenus = menuIds.stream()
                    .map(menuId -> {
                        SysRoleMenu roleMenu = new SysRoleMenu();
                        roleMenu.setTenantId(tenantId);
                        roleMenu.setRoleId(roleId);
                        roleMenu.setMenuId(menuId);
                        return roleMenu;
                    })
                    .collect(Collectors.toList());
            
            sysRoleMenuMapper.batchSave(roleMenus);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRoleMenus(Long roleId) {
        sysRoleMenuMapper.deleteByRoleId(roleId);
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        return sysRoleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    /**
     * 根据数据范围类型获取部门 ID 过滤条件
     * 在 Service 层调用，为查询添加数据范围过滤
     *
     * @param userId 用户 ID
     * @param dataScope 数据范围类型
     * @param customDeptIds 自定义部门 ID
     * @return 部门 ID 列表（空列表表示全部数据权限）
     */
    public List<Long> getDataScopeDeptIds(Long userId, Integer dataScope, String customDeptIds) {
        if (userId == null || dataScope == null) {
            return new ArrayList<>();
        }

        switch (dataScope) {
            case 1: // 全部数据
                return new ArrayList<>();
            case 2: // 本部门及以下
            case 3: // 仅本部门
                SysUser user = sysUserMapper.selectById(userId);
                if (user == null || user.getDeptId() == null) {
                    return new ArrayList<>();
                }
                List<Long> deptIds = new ArrayList<>();
                deptIds.add(user.getDeptId());
                return deptIds;
            case 4: // 仅本人
                return new ArrayList<>(); // 特殊处理，在查询时过滤用户 ID
            case 5: // 自定义
                if (customDeptIds != null && !customDeptIds.isEmpty()) {
                    String[] ids = customDeptIds.split(",");
                    List<Long> result = new ArrayList<>();
                    for (String id : ids) {
                        try {
                            result.add(Long.parseLong(id.trim()));
                        } catch (NumberFormatException e) {
                            // 忽略无效 ID
                        }
                    }
                    return result;
                }
                return new ArrayList<>();
            default:
                return new ArrayList<>();
        }
    }
}
