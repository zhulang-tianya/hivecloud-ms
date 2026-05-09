package com.hivecloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hivecloud.system.entity.SysRole;

import java.util.List;

/**
 * 角色 Service 接口
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 查询角色列表
     *
     * @param role 角色查询条件
     * @return 角色列表
     */
    List<SysRole> selectRoleList(SysRole role);

    /**
     * 根据 ID 查询角色
     *
     * @param id 角色 ID
     * @return 角色信息
     */
    SysRole selectRoleById(Long id);

    /**
     * 根据角色编码查询
     *
     * @param roleCode 角色编码
     * @return 角色信息
     */
    SysRole selectRoleByCode(String roleCode);

    /**
     * 创建角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    boolean createRole(SysRole role);

    /**
     * 更新角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    boolean updateRole(SysRole role);

    /**
     * 删除角色
     *
     * @param roleIds 角色 ID 数组
     * @return 是否成功
     */
    boolean deleteRoleByIds(Long[] roleIds);

    /**
     * 为角色分配菜单权限
     *
     * @param roleId 角色 ID
     * @param menuIds 菜单 ID 列表
     */
    void assignMenus(Long roleId, List<Long> menuIds);

    /**
     * 删除角色的菜单关联
     *
     * @param roleId 角色 ID
     */
    void removeRoleMenus(Long roleId);

    /**
     * 查询角色拥有的菜单 ID 列表
     *
     * @param roleId 角色 ID
     * @return 菜单 ID 列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);
}
