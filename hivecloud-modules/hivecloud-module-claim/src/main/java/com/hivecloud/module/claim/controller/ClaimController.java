package com.hivecloud.module.claim.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.module.claim.entity.ClaimEntity;
import com.hivecloud.module.claim.service.ClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 理赔管理接口
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Slf4j
@RestController
@RequestMapping("/api/claim")
@Validated
@Tag(name = "理赔管理", description = "理赔申请管理接口")
public class ClaimController {

    @Resource
    private ClaimService claimService;

    /**
     * 创建理赔申请
     */
    @PostMapping("/create")
    @Operation(summary = "创建理赔申请")
    public Result<String> createClaim(@Validated @RequestBody ClaimEntity claim) {
        log.info("创建理赔申请，userId:{}", claim.getUserId());
        String claimNo = claimService.createClaim(claim);
        log.info("理赔申请创建成功，claimNo:{}", claimNo);
        return Result.success(claimNo);
    }

    /**
     * 查询理赔申请
     */
    @GetMapping("/query/{claimNo}")
    @Operation(summary = "查询理赔申请")
    public Result<ClaimEntity> getClaim(@PathVariable String claimNo) {
        log.info("查询理赔申请，claimNo:{}", claimNo);
        ClaimEntity claim = claimService.getClaim(claimNo);
        return Result.success(claim);
    }

    /**
     * 审核理赔申请
     */
    @PostMapping("/audit/{claimNo}")
    @Operation(summary = "审核理赔申请")
    public Result<Void> auditClaim(
            @PathVariable String claimNo,
            @RequestParam Long auditorId,
            @RequestParam String opinion,
            @RequestParam boolean approved) {
        log.info("审核理赔申请，claimNo:{}, approved:{}", claimNo, approved);
        claimService.auditClaim(claimNo, auditorId, opinion, approved);
        return Result.success();
    }

    /**
     * 理赔打款
     */
    @PostMapping("/payment/{claimNo}")
    @Operation(summary = "理赔打款")
    public Result<Void> payment(@PathVariable String claimNo) {
        log.info("理赔打款，claimNo:{}", claimNo);
        claimService.payment(claimNo);
        return Result.success();
    }

    /**
     * 查询用户理赔列表
     */
    @GetMapping("/list/{userId}")
    @Operation(summary = "查询用户理赔列表")
    public Result<List<ClaimEntity>> listByUserId(@PathVariable Long userId) {
        log.info("查询用户理赔列表，userId:{}", userId);
        List<ClaimEntity> list = claimService.listByUserId(userId);
        return Result.success(list);
    }
}
