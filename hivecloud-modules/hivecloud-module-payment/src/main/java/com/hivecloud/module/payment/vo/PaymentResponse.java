package com.hivecloud.module.payment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "支付响应")
public class PaymentResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付订单 ID
     */
    @Schema(description = "支付订单 ID", example = "1234567890")
    private Long paymentId;

    /**
     * 商户订单号
     */
    @Schema(description = "商户订单号", example = "ORDER20260427001")
    private String orderNo;

    /**
     * 支付流水号
     */
    @Schema(description = "支付流水号", example = "202604270001")
    private String transactionId;

    /**
     * 支付金额
     */
    @Schema(description = "支付金额", example = "100.00")
    private BigDecimal amount;

    /**
     * 支付方式
     */
    @Schema(description = "支付方式", example = "alipay")
    private String paymentMethod;

    /**
     * 支付状态
     */
    @Schema(description = "支付状态", example = "2")
    private Integer paymentStatus;

    /**
     * 支付状态描述
     */
    @Schema(description = "支付状态描述", example = "支付成功")
    private String paymentStatusDesc;

    /**
     * 支付时间
     */
    @Schema(description = "支付时间", example = "2026-04-27 10:00:00")
    private LocalDateTime paymentTime;

    /**
     * 支付链接（用于跳转第三方支付）
     */
    @Schema(description = "支付链接", example = "https://openapi.alipay.com/gateway.do?xxx")
    private String payUrl;

    /**
     * 二维码链接（用于扫码支付）
     */
    @Schema(description = "二维码链接", example = "https://qr.alipay.com/xxx")
    private String qrCode;
}
