package com.hivecloud.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hivecloud.system.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单关联 Mapper 接口
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 根据角色 ID 查询菜单 ID 列表
     *
     * @param roleId 角色 ID
     * @return 菜单 ID 列表
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户 ID 查询菜单 ID 列表
     *
     * @param userId 用户 ID
     * @return 菜单 ID 列表
     */
    List<Long> selectMenuIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据角色 ID 删除菜单关联
     *
     * @param roleId 角色 ID
     * @return 影响行数
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量保存角色菜单关联
     *
     * @param list 角色菜单关联列表
     * @return 影响行数
     */
    int batchSave(@Param("list") List<SysRoleMenu> list);
}
