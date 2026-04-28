package com.hivecloud.module.activity.service;

import com.hivecloud.module.activity.entity.CouponEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 优惠券服务接口
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
public interface CouponService extends IService<CouponEntity> {

    /**
     * 根据券码查询优惠券
     *
     * @param couponNo 券码
     * @return 优惠券信息
     */
    CouponEntity getByCouponNo(String couponNo);

    /**
     * 查询用户优惠券列表
     *
     * @param userId 用户 ID
     * @param status 状态（可选）
     * @return 优惠券列表
     */
    List<CouponEntity> getUserCoupons(Long userId, Integer status);

    /**
     * 发放优惠券
     *
     * @param userId 用户 ID
     * @param couponId 优惠券 ID
     * @return 是否成功
     */
    boolean issueCoupon(Long userId, Long couponId);

    /**
     * 使用优惠券
     *
     * @param couponId 优惠券 ID
     * @param userId 用户 ID
     * @return 是否成功
     */
    boolean useCoupon(Long couponId, Long userId);

    /**
     * 作废优惠券
     *
     * @param couponId 优惠券 ID
     * @return 是否成功
     */
    boolean invalidateCoupon(Long couponId);
}
