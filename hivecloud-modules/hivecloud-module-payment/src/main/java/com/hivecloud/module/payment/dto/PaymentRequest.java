package com.hivecloud.module.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 支付请求 DTO
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Data
@Schema(description = "支付请求")
public class PaymentRequest {

    /**
     * 商户订单号
     */
    @Schema(description = "商户订单号", example = "ORDER20260427001")
    @NotBlank(message = "商户订单号不能为空")
    private String orderNo;

    /**
     * 支付金额
     */
    @Schema(description = "支付金额", example = "100.00")
    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额最小为 0.01 元")
    private BigDecimal amount;

    /**
     * 商品标题
     */
    @Schema(description = "商品标题", example = "测试商品")
    @NotBlank(message = "商品标题不能为空")
    private String subject;

    /**
     * 商品描述
     */
    @Schema(description = "商品描述", example = "这是一个测试商品")
    private String body;

    /**
     * 支付方式
     */
    @Schema(description = "支付方式", example = "alipay")
    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;

    /**
     * 异步通知地址
     */
    @Schema(description = "异步通知地址", example = "http://example.com/notify")
    private String notifyUrl;

    /**
     * 返回地址
     */
    @Schema(description = "返回地址", example = "http://example.com/return")
    private String returnUrl;

    /**
     * 用户 ID
     */
    @Schema(description = "用户 ID", example = "1001")
    private Long userId;

    /**
     * 扩展参数
     */
    @Schema(description = "扩展参数", example = "{\"key\":\"value\"}")
    private String extraParams;
}
