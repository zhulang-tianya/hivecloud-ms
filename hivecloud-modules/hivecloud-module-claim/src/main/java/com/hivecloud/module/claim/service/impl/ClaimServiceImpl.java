package com.hivecloud.module.claim.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hivecloud.module.claim.entity.ClaimEntity;
import com.hivecloud.module.claim.mapper.ClaimMapper;
import com.hivecloud.module.claim.service.ClaimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 理赔服务实现类
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    @Resource
    private ClaimMapper claimMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createClaim(ClaimEntity claim) {
        log.info("创建理赔申请，userId:{}, claimType:{}", claim.getUserId(), claim.getClaimType());
        
        String claimNo = "CLAIM" + System.currentTimeMillis();
        claim.setClaimNo(claimNo);
        claim.setClaimStatus(0);
        claim.setApplyTime(LocalDateTime.now());
        claim.setCreateTime(LocalDateTime.now());
        claim.setUpdateTime(LocalDateTime.now());
        
        // 保存到数据库
        claimMapper.insert(claim);
        
        log.info("理赔申请创建成功，claimNo:{}", claimNo);
        return claimNo;
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimEntity getClaim(String claimNo) {
        log.info("查询理赔申请，claimNo:{}", claimNo);
        LambdaQueryWrapper<ClaimEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClaimEntity::getClaimNo, claimNo);
        return claimMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditClaim(String claimNo, Long auditorId, String opinion, boolean approved) {
        log.info("审核理赔申请，claimNo:{}, approved:{}", claimNo, approved);
        
        ClaimEntity claim = getClaim(claimNo);
        claim.setClaimStatus(approved ? 2 : 3);
        claim.setAuditorId(auditorId);
        claim.setAuditOpinion(opinion);
        claim.setAuditTime(LocalDateTime.now());
        claim.setUpdateTime(LocalDateTime.now());
        
        // 更新数据库
        claimMapper.updateById(claim);
        
        log.info("理赔审核完成，claimNo:{}, approved:{}", claimNo, approved);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payment(String claimNo) {
        log.info("理赔打款，claimNo:{}", claimNo);
        
        ClaimEntity claim = getClaim(claimNo);
        if (claim == null) {
            log.error("理赔申请不存在，claimNo:{}", claimNo);
            throw new IllegalArgumentException("理赔申请不存在");
        }
        
        // 检查理赔状态，只有审核通过才能打款
        if (claim.getClaimStatus() != 2) {
            log.error("理赔状态不正确，无法打款，claimNo:{}, status:{}", claimNo, claim.getClaimStatus());
            throw new IllegalStateException("只有审核通过的理赔申请才能打款");
        }
        
        claim.setClaimStatus(4);
        claim.setPaymentTime(LocalDateTime.now());
        claim.setUpdateTime(LocalDateTime.now());
        
        // 更新数据库
        claimMapper.updateById(claim);
        
        // 调用支付接口打款（模拟实现，实际需对接支付系统）
        // 生产环境需要：
        // 1. 调用支付系统的打款接口
        // 2. 记录打款流水
        // 3. 处理打款结果回调
        log.info("调用支付接口打款，userId:{}, amount:{}", claim.getUserId(), claim.getClaimAmount());
        
        log.info("理赔打款成功，claimNo:{}", claimNo);
    }

    @Override
    public List<ClaimEntity> listByUserId(Long userId) {
        log.info("查询用户理赔列表，userId:{}", userId);
        LambdaQueryWrapper<ClaimEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClaimEntity::getUserId, userId);
        wrapper.orderByDesc(ClaimEntity::getApplyTime);
        return claimMapper.selectList(wrapper);
    }
}
