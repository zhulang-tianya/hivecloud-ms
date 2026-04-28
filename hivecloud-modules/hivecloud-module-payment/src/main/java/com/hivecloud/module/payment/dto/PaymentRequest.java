package com.hivecloud.module.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付请求 DTO
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Data
public class PaymentRequest {

    /**
     * 商户订单号
     */
    private String orderNo;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 商品标题
     */
    private String subject;

    /**
     * 商品描述
     */
    private String body;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 返回地址
     */
    private String returnUrl;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 扩展参数
     */
    private String extraParams;
}
