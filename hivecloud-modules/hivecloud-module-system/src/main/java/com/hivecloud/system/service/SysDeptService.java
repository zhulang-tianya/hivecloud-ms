package com.hivecloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hivecloud.system.entity.SysDept;

import java.util.List;

/**
 * 部门 Service 接口
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
public interface SysDeptService extends IService<SysDept> {

    /**
     * 查询部门树
     *
     * @param tenantId 租户 ID
     * @return 部门树列表
     */
    List<SysDept> getDeptTree(Long tenantId);

    /**
     * 查询部门列表
     *
     * @param dept 部门查询条件
     * @return 部门列表
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 根据 ID 查询部门
     *
     * @param id 部门 ID
     * @return 部门信息
     */
    SysDept selectDeptById(Long id);

    /**
     * 创建部门
     *
     * @param dept 部门信息
     * @return 是否成功
     */
    boolean createDept(SysDept dept);

    /**
     * 更新部门
     *
     * @param dept 部门信息
     * @return 是否成功
     */
    boolean updateDept(SysDept dept);

    /**
     * 删除部门
     *
     * @param deptIds 部门 ID 数组
     * @return 是否成功
     */
    boolean deleteDeptByIds(Long[] deptIds);
}
