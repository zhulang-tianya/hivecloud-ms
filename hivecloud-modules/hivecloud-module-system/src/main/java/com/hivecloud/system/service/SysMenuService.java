package com.hivecloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hivecloud.system.entity.SysMenu;

import java.util.List;

/**
 * 菜单 Service 接口
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 查询菜单列表
     *
     * @param menu 菜单查询条件
     * @return 菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 查询菜单树
     *
     * @param tenantId 租户 ID
     * @return 菜单树列表
     */
    List<SysMenu> getMenuTree(Long tenantId);

    /**
     * 根据 ID 查询菜单
     *
     * @param id 菜单 ID
     * @return 菜单信息
     */
    SysMenu selectMenuById(Long id);

    /**
     * 创建菜单
     *
     * @param menu 菜单信息
     * @return 是否成功
     */
    boolean createMenu(SysMenu menu);

    /**
     * 更新菜单
     *
     * @param menu 菜单信息
     * @return 是否成功
     */
    boolean updateMenu(SysMenu menu);

    /**
     * 删除菜单
     *
     * @param menuIds 菜单 ID 数组
     * @return 是否成功
     */
    boolean deleteMenuByIds(Long[] menuIds);
}
