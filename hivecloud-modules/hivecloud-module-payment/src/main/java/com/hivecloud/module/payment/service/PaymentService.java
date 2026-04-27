package com.hivecloud.module.payment.service;

import com.hivecloud.module.payment.dto.PaymentRequest;
import com.hivecloud.module.payment.vo.PaymentResponse;

/**
 * 支付服务接口
 * 提供统一下单、查询、退款、关闭订单等功能
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
public interface PaymentService {

    /**
     * 统一下单
     *
     * @param request 支付请求参数
     * @return 支付响应
     */
    PaymentResponse createOrder(PaymentRequest request);

    /**
     * 查询订单
     *
     * @param orderNo 商户订单号
     * @return 支付响应
     */
    PaymentResponse queryOrder(String orderNo);

    /**
     * 退款
     *
     * @param orderNo 商户订单号
     * @param amount 退款金额
     * @param reason 退款原因
     * @return 支付响应
     */
    PaymentResponse refund(String orderNo, java.math.BigDecimal amount, String reason);

    /**
     * 关闭订单
     *
     * @param orderNo 商户订单号
     * @return 支付响应
     */
    PaymentResponse closeOrder(String orderNo);

    /**
     * 处理异步通知
     *
     * @param orderNo 商户订单号
     * @param params 通知参数
     * @return 处理结果
     */
    String handleNotify(String orderNo, java.util.Map<String, String> params);
}
