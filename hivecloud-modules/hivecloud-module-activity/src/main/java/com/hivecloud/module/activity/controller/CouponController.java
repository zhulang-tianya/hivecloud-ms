package com.hivecloud.module.activity.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.module.activity.entity.CouponEntity;
import com.hivecloud.module.activity.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 优惠券控制器
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    /**
     * 根据券码查询优惠券
     *
     * @param couponNo 券码
     * @return 优惠券信息
     */
    @GetMapping("/{couponNo}")
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
    public Result<Boolean> invalidateCoupon(@RequestParam Long couponId) {
        boolean success = couponService.invalidateCoupon(couponId);
        return success ? Result.success(true, "作废成功") : Result.error("作废失败");
    }
}
