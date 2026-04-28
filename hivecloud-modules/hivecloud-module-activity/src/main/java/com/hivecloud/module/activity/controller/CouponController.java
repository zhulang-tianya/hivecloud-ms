package com.hivecloud.module.activity.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.module.activity.entity.CouponEntity;
import com.hivecloud.module.activity.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 优惠券控制器
 * <p>
 * 提供优惠券查询、发放、使用、作废等接口
 * </p>
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
@Tag(name = "优惠券管理", description = "优惠券查询、发放、使用、作废等接口")
public class CouponController {

    private final CouponService couponService;

    /**
     * 根据券码查询优惠券
     *
     * @param couponNo 券码
     * @return 优惠券信息
     */
    @GetMapping("/{couponNo}")
    @Operation(summary = "根据券码查询优惠券", description = "根据券码查询优惠券详细信息")
    @Parameter(name = "couponNo", description = "券码", required = true, example = "CPN20260428001")
    public Result<CouponEntity> getByCouponNo(@PathVariable String couponNo) {
        CouponEntity coupon = couponService.getByCouponNo(couponNo);
        return coupon != null ? Result.success(coupon) : Result.error("优惠券不存在");
    }

    /**
     * 查询用户优惠券列表
     *
     * @param userId 用户 ID
     * @param status 状态（可选）
     * @return 优惠券列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "查询用户优惠券列表", description = "查询指定用户的优惠券列表")
    @Parameter(name = "userId", description = "用户 ID", required = true, example = "1")
    @Parameter(name = "status", description = "优惠券状态（0-未使用，1-已使用，2-已过期）", required = false, example = "0")
    public Result<List<CouponEntity>> getUserCoupons(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer status) {
        List<CouponEntity> coupons = couponService.getUserCoupons(userId, status);
        return Result.success(coupons);
    }

    /**
     * 发放优惠券
     *
     * @param userId 用户 ID
     * @param couponId 优惠券 ID
     * @return 结果
     */
    @PostMapping("/issue")
    @Operation(summary = "发放优惠券", description = "向指定用户发放优惠券")
    @Parameter(name = "userId", description = "用户 ID", required = true, example = "1")
    @Parameter(name = "couponId", description = "优惠券 ID", required = true, example = "1")
    public Result<Boolean> issueCoupon(@RequestParam Long userId, @RequestParam Long couponId) {
        boolean success = couponService.issueCoupon(userId, couponId);
        return success ? Result.success(true, "发放成功") : Result.error("发放失败");
    }

    /**
     * 使用优惠券
     *
     * @param couponId 优惠券 ID
     * @param userId 用户 ID
     * @return 结果
     */
    @PostMapping("/use")
    @Operation(summary = "使用优惠券", description = "用户使用优惠券")
    @Parameter(name = "couponId", description = "优惠券 ID", required = true, example = "1")
    @Parameter(name = "userId", description = "用户 ID", required = true, example = "1")
    public Result<Boolean> useCoupon(@RequestParam Long couponId, @RequestParam Long userId) {
        boolean success = couponService.useCoupon(couponId, userId);
        return success ? Result.success(true, "使用成功") : Result.error("使用失败");
    }

    /**
     * 作废优惠券
     *
     * @param couponId 优惠券 ID
     * @return 结果
     */
    @PostMapping("/invalidate")
    @Operation(summary = "作废优惠券", description = "作废指定优惠券")
    @Parameter(name = "couponId", description = "优惠券 ID", required = true, example = "1")
    public Result<Boolean> invalidateCoupon(@RequestParam Long couponId) {
        boolean success = couponService.invalidateCoupon(couponId);
        return success ? Result.success(true, "作废成功") : Result.error("作废失败");
    }
}
