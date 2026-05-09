package com.hivecloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hivecloud.system.entity.SysTenantPackage;

import java.util.List;

/**
 * 租户套餐 Service 接口
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
public interface SysTenantPackageService extends IService<SysTenantPackage> {

    /**
     * 查询套餐列表
     *
     * @param pkg 套餐查询条件
     * @return 套餐列表
     */
    List<SysTenantPackage> selectPackageList(SysTenantPackage pkg);

    /**
     * 根据 ID 查询套餐
     *
     * @param id 套餐 ID
     * @return 套餐信息
     */
    SysTenantPackage selectPackageById(Long id);

    /**
     * 创建套餐
     *
     * @param pkg 套餐信息
     * @return 是否成功
     */
    boolean createPackage(SysTenantPackage pkg);

    /**
     * 更新套餐
     *
     * @param pkg 套餐信息
     * @return 是否成功
     */
    boolean updatePackage(SysTenantPackage pkg);

    /**
     * 删除套餐
     *
     * @param ids 套餐 ID 数组
     * @return 是否成功
     */
    boolean deletePackageByIds(Long[] ids);
}
