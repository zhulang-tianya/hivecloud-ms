package com.hivecloud.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hivecloud.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联 Mapper 接口
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 根据用户 ID 查询角色 ID 列表
     *
     * @param userId 用户 ID
     * @return 角色 ID 列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据角色 ID 查询用户 ID 列表
     *
     * @param roleId 角色 ID
     * @return 用户 ID 列表
     */
    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户 ID 删除角色关联
     *
     * @param userId 用户 ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据角色 ID 删除用户关联
     *
     * @param roleId 角色 ID
     * @return 影响行数
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量保存用户角色关联
     *
     * @param list 用户角色关联列表
     * @return 影响行数
     */
    int batchSave(@Param("list") List<SysUserRole> list);
}
