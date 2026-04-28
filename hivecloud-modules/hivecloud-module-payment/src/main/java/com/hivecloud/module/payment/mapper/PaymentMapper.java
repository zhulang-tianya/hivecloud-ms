package com.hivecloud.module.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hivecloud.module.payment.entity.PaymentOrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付订单 Mapper 接口
 * 基于 MyBatis-Plus 实现，提供 CRUD 操作
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 * @see PaymentOrderEntity
 */
@Mapper
public interface PaymentMapper extends BaseMapper<PaymentOrderEntity> {

}
