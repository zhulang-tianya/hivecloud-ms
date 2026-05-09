package com.hivecloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hivecloud.system.entity.SysTenant;

import java.util.List;

/**
 * 租户 Service 接口
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
public interface SysTenantService extends IService<SysTenant> {

    /**
     * 查询租户列表
     *
     * @param tenant 租户查询条件
     * @return 租户列表
     */
    List<SysTenant> selectTenantList(SysTenant tenant);

    /**
     * 根据 ID 查询租户
     *
     * @param id 租户 ID
     * @return 租户信息
     */
    SysTenant selectTenantById(Long id);

    /**
     * 根据租户编码查询
     *
     * @param tenantCode 租户编码
     * @return 租户信息
     */
    SysTenant selectTenantByCode(String tenantCode);

    /**
     * 创建租户
     *
     * @param tenant 租户信息
     * @return 是否成功
     */
    boolean createTenant(SysTenant tenant);

    /**
     * 更新租户
     *
     * @param tenant 租户信息
     * @return 是否成功
     */
    boolean updateTenant(SysTenant tenant);

    /**
     * 删除租户
     *
     * @param tenantIds 租户 ID 数组
     * @return 是否成功
     */
    boolean deleteTenantByIds(Long[] tenantIds);
}
