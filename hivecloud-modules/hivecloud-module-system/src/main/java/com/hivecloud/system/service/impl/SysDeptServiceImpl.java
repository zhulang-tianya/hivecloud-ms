package com.hivecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hivecloud.system.entity.SysDept;
import com.hivecloud.system.mapper.SysDeptMapper;
import com.hivecloud.system.service.SysDeptService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 部门 Service 实现类
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    @Override
    public List<SysDept> getDeptTree(Long tenantId) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getTenantId, tenantId)
                .eq(SysDept::getStatus, 1)
                .orderByAsc(SysDept::getAncestors);
        return list(wrapper);
    }

    @Override
    public List<SysDept> selectDeptList(SysDept dept) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dept.getTenantId() != null, SysDept::getTenantId, dept.getTenantId())
                .eq(dept.getParentId() != null, SysDept::getParentId, dept.getParentId())
                .eq(dept.getStatus() != null, SysDept::getStatus, dept.getStatus())
                .like(dept.getDeptName() != null, SysDept::getDeptName, dept.getDeptName())
                .orderByAsc(SysDept::getAncestors);
        return list(wrapper);
    }

    @Override
    public SysDept selectDeptById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDept(SysDept dept) {
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        return save(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDept(SysDept dept) {
        dept.setUpdateTime(LocalDateTime.now());
        return updateById(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDeptByIds(Long[] deptIds) {
        List<Long> ids = Arrays.asList(deptIds);
        return removeByIds(ids);
    }
}
