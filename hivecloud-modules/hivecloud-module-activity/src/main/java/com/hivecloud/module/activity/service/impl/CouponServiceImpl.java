package com.hivecloud.module.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hivecloud.module.activity.entity.CouponEntity;
import com.hivecloud.module.activity.mapper.CouponMapper;
import com.hivecloud.module.activity.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券服务实现类
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl extends ServiceImpl<CouponMapper, CouponEntity> implements CouponService {

    @Override
    public CouponEntity getByCouponNo(String couponNo) {
        LambdaQueryWrapper<CouponEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CouponEntity::getCouponNo, couponNo);
        return getOne(wrapper);
    }

    @Override
    public List<CouponEntity> getUserCoupons(Long userId, Integer status) {
        LambdaQueryWrapper<CouponEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CouponEntity::getStatus, status != null ? status : 0);
        wrapper.orderByDesc(CouponEntity::getCreateTime);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean issueCoupon(Long userId, Long couponId) {
        CouponEntity coupon = getById(couponId);
        if (coupon == null) {
            log.warn("优惠券不存在，couponId: {}", couponId);
            return false;
        }

        CouponEntity userCoupon = new CouponEntity();
        userCoupon.setCouponNo(coupon.getCouponNo());
        userCoupon.setCouponName(coupon.getCouponName());
        userCoupon.setAmount(coupon.getAmount());
        userCoupon.setType(coupon.getType());
        userCoupon.setValidFrom(coupon.getValidFrom());
        userCoupon.setValidTo(coupon.getValidTo());
        userCoupon.setStatus(0);
        userCoupon.setCreateTime(LocalDateTime.now());
        userCoupon.setUpdateTime(LocalDateTime.now());

        return save(userCoupon);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean useCoupon(Long couponId, Long userId) {
        CouponEntity coupon = getById(couponId);
        if (coupon == null || coupon.getStatus() != 0) {
            log.warn("优惠券不可用，couponId: {}, status: {}", couponId, coupon != null ? coupon.getStatus() : "null");
            return false;
        }

        coupon.setStatus(1);
        coupon.setUpdateTime(LocalDateTime.now());
        return updateById(coupon);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean invalidateCoupon(Long couponId) {
        CouponEntity coupon = getById(couponId);
        if (coupon == null) {
            log.warn("优惠券不存在，couponId: {}", couponId);
            return false;
        }

        coupon.setStatus(2);
        coupon.setUpdateTime(LocalDateTime.now());
        return updateById(coupon);
    }
}
