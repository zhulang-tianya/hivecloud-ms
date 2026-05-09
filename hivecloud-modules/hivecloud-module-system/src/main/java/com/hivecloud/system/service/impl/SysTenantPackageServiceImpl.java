package com.hivecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hivecloud.system.entity.SysTenantPackage;
import com.hivecloud.system.mapper.SysTenantPackageMapper;
import com.hivecloud.system.service.SysTenantPackageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 租户套餐 Service 实现类
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Service
public class SysTenantPackageServiceImpl extends ServiceImpl<SysTenantPackageMapper, SysTenantPackage> implements SysTenantPackageService {

    @Override
    public List<SysTenantPackage> selectPackageList(SysTenantPackage pkg) {
        LambdaQueryWrapper<SysTenantPackage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(pkg.getStatus() != null, SysTenantPackage::getStatus, pkg.getStatus())
                .like(pkg.getName() != null, SysTenantPackage::getName, pkg.getName())
                .orderByDesc(SysTenantPackage::getCreateTime);
        return list(wrapper);
    }

    @Override
    public SysTenantPackage selectPackageById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createPackage(SysTenantPackage pkg) {
        pkg.setCreateTime(LocalDateTime.now());
        pkg.setUpdateTime(LocalDateTime.now());
        return save(pkg);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePackage(SysTenantPackage pkg) {
        pkg.setUpdateTime(LocalDateTime.now());
        return updateById(pkg);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePackageByIds(Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        return removeByIds(idList);
    }
}
