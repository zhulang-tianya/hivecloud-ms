package com.hivecloud.module.payment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单实体类
 * 用于存储支付订单信息
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Data
@TableName("t_payment_order")
public class PaymentOrderEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付订单主键 ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 商户订单号
     */
    private String orderNo;

    /**
     * 支付流水号（第三方支付平台返回）
     */
    private String transactionId;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付币种：CNY-人民币，USD-美元
     */
    private String currency;

    /**
     * 支付方式：alipay-支付宝，wechat-微信，unionpay-银联
     */
    private String paymentMethod;

    /**
     * 支付状态：0-待支付，1-支付中，2-支付成功，3-支付失败，4-已关闭，5-已退款
     */
    private Integer paymentStatus;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 商品标题
     */
    private String subject;

    /**
     * 商品描述
     */
    private String body;

    /**
     * 支付成功时间
     */
    private LocalDateTime paymentTime;

    /**
     * 订单关闭时间
     */
    private LocalDateTime closeTime;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 返回地址
     */
    private String returnUrl;

    /**
     * 扩展参数（JSON 格式）
     */
    private String extraParams;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
