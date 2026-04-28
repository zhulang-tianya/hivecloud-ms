package com.hivecloud.module.payment.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付响应 VO
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Data
@Builder
public class PaymentResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付订单 ID
     */
    private Long paymentId;

    /**
     * 商户订单号
     */
    private String orderNo;

    /**
     * 支付流水号
     */
    private String transactionId;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 支付状态
     */
    private Integer paymentStatus;

    /**
     * 支付状态描述
     */
    private String paymentStatusDesc;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 支付链接（用于跳转第三方支付）
     */
    private String payUrl;

    /**
     * 二维码链接（用于扫码支付）
     */
    private String qrCode;
}
