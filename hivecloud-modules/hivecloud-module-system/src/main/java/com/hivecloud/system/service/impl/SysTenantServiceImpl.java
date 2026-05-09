package com.hivecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hivecloud.system.entity.SysTenant;
import com.hivecloud.system.mapper.SysTenantMapper;
import com.hivecloud.system.service.SysTenantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 租户 Service 实现类
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Service
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenant> implements SysTenantService {

    @Override
    public List<SysTenant> selectTenantList(SysTenant tenant) {
        LambdaQueryWrapper<SysTenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(tenant.getTenantCode() != null, SysTenant::getTenantCode, tenant.getTenantCode())
                .eq(tenant.getStatus() != null, SysTenant::getStatus, tenant.getStatus())
                .like(tenant.getName() != null, SysTenant::getName, tenant.getName())
                .orderByDesc(SysTenant::getCreateTime);
        return list(wrapper);
    }

    @Override
    public SysTenant selectTenantById(Long id) {
        return getById(id);
    }

    @Override
    public SysTenant selectTenantByCode(String tenantCode) {
        LambdaQueryWrapper<SysTenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysTenant::getTenantCode, tenantCode);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createTenant(SysTenant tenant) {
        tenant.setCreateTime(LocalDateTime.now());
        tenant.setUpdateTime(LocalDateTime.now());
        return save(tenant);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTenant(SysTenant tenant) {
        tenant.setUpdateTime(LocalDateTime.now());
        return updateById(tenant);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTenantByIds(Long[] tenantIds) {
        List<Long> ids = Arrays.asList(tenantIds);
        return removeByIds(ids);
    }
}
