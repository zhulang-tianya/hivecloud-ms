package com.hivecloud.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hivecloud.system.entity.SysDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门 Mapper 接口
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 查询部门树
     *
     * @param tenantId 租户 ID
     * @return 部门树列表
     */
    List<SysDept> selectDeptTree(@Param("tenantId") Long tenantId);

    /**
     * 查询子部门
     *
     * @param tenantId 租户 ID
     * @param parentId 父部门 ID
     * @return 子部门列表
     */
    List<SysDept> selectChildDepts(@Param("tenantId") Long tenantId, @Param("parentId") Long parentId);
}
