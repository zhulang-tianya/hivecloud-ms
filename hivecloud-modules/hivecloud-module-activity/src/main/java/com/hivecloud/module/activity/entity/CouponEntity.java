package com.hivecloud.module.activity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券实体类
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Data
@TableName("t_coupon")
public class CouponEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String couponNo;

    private String couponName;

    private BigDecimal amount;

    private Integer type;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
