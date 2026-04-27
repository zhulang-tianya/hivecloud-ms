package com.hivecloud.module.claim.service;

import com.hivecloud.module.claim.entity.ClaimEntity;

import java.util.List;

/**
 * 理赔服务接口
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
public interface ClaimService {

    /**
     * 创建理赔申请
     *
     * @param claim 理赔申请
     * @return 理赔申请号
     */
    String createClaim(ClaimEntity claim);

    /**
     * 查询理赔申请
     *
     * @param claimNo 理赔申请号
     * @return 理赔申请详情
     */
    ClaimEntity getClaim(String claimNo);

    /**
     * 审核理赔申请
     *
     * @param claimNo 理赔申请号
     * @param auditorId 审核人 ID
     * @param opinion 审核意见
     * @param approved 是否通过
     */
    void auditClaim(String claimNo, Long auditorId, String opinion, boolean approved);

    /**
     * 理赔打款
     *
     * @param claimNo 理赔申请号
     */
    void payment(String claimNo);

    /**
     * 查询用户的理赔申请列表
     *
     * @param userId 用户 ID
     * @return 理赔申请列表
     */
    List<ClaimEntity> listByUserId(Long userId);
}
