package com.hivecloud.module.payment.adapter;

import com.hivecloud.module.payment.vo.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 支付宝支付适配器
 * 处理支付宝支付相关逻辑
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Slf4j
@Component
public class AlipayAdapter {

    /**
     * 创建支付宝支付订单
     *
     * @param orderNo 订单号
     * @param amount 金额
     * @param subject 商品标题
     * @param body 商品描述
     * @return 支付响应
     */
    public PaymentResponse createOrder(String orderNo, java.math.BigDecimal amount, String subject, String body) {
        log.info("创建支付宝支付订单，orderNo:{}, amount:{}, subject:{}", orderNo, amount, subject);
        
        try {
            // 构建支付宝订单参数
            java.util.Map<String, String> alipayParams = new java.util.HashMap<>();
            alipayParams.put("out_trade_no", orderNo);
            alipayParams.put("total_amount", amount.toString());
            alipayParams.put("subject", subject);
            alipayParams.put("body", body);
            alipayParams.put("product_code", "FAST_INSTANT_TRADE_PAY");
            
            // 调用支付宝 SDK 创建订单（模拟实现）
            // 生产环境需要：
            // 1. 引入支付宝 SDK 依赖
            // 2. 配置 AppID、私钥、支付宝公钥
            // 3. 调用 AlipayTradePagePayRequest 或 AlipayTradeAppPayRequest
            // 4. 获取 form 表单或支付链接
            
            log.info("支付宝订单创建成功，orderNo:{}", orderNo);
            
            // 返回支付链接（模拟）
            String payUrl = String.format("https://openapi.alipay.com/gateway.do?out_trade_no=%s&total_amount=%s", 
                    orderNo, amount);
            
            return PaymentResponse.builder()
                    .orderNo(orderNo)
                    .amount(amount)
                    .paymentMethod("alipay")
                    .paymentStatus(0)
                    .paymentStatusDesc("待支付")
                    .payUrl(payUrl)
                    .build();
                    
        } catch (Exception e) {
            log.error("创建支付宝订单失败，orderNo:{}", orderNo, e);
            throw new RuntimeException("创建支付宝订单失败：" + e.getMessage(), e);
        }
    }

    /**
     * 查询支付宝订单
     *
     * @param orderNo 订单号
     * @return 支付响应
     */
    public PaymentResponse queryOrder(String orderNo) {
        log.info("查询支付宝订单，orderNo:{}", orderNo);
        
        try {
            // 构建查询参数
            java.util.Map<String, String> queryParams = new java.util.HashMap<>();
            queryParams.put("out_trade_no", orderNo);
            
            // 调用支付宝 SDK 查询订单（模拟实现）
            // 生产环境需要：
            // 1. 调用 AlipayTradeQueryRequest
            // 2. 解析返回的订单状态
            
            log.info("支付宝订单查询成功，orderNo:{}", orderNo);
            
            // 模拟返回支付成功状态
            return PaymentResponse.builder()
                    .orderNo(orderNo)
                    .paymentStatus(2)
                    .paymentStatusDesc("支付成功")
                    .transactionId("ALIPAY_" + System.currentTimeMillis())
                    .build();
                    
        } catch (Exception e) {
            log.error("查询支付宝订单失败，orderNo:{}", orderNo, e);
            throw new RuntimeException("查询支付宝订单失败：" + e.getMessage(), e);
        }
    }

    /**
     * 支付宝退款
     *
     * @param orderNo 订单号
     * @param amount 退款金额
     * @param reason 退款原因
     * @return 支付响应
     */
    public PaymentResponse refund(String orderNo, java.math.BigDecimal amount, String reason) {
        log.info("支付宝退款，orderNo:{}, amount:{}, reason:{}", orderNo, amount, reason);
        
        try {
            // 构建退款参数
            java.util.Map<String, String> refundParams = new java.util.HashMap<>();
            refundParams.put("out_trade_no", orderNo);
            refundParams.put("refund_amount", amount.toString());
            refundParams.put("refund_reason", reason);
            refundParams.put("out_request_no", "REFUND_" + System.currentTimeMillis());
            
            // 调用支付宝 SDK 退款（模拟实现）
            // 生产环境需要：
            // 1. 调用 AlipayTradeRefundRequest
            // 2. 处理退款结果
            
            log.info("支付宝退款成功，orderNo:{}, amount:{}", orderNo, amount);
            
            return PaymentResponse.builder()
                    .orderNo(orderNo)
                    .paymentStatus(5)
                    .paymentStatusDesc("已退款")
                    .refundAmount(amount)
                    .refundReason(reason)
                    .build();
                    
        } catch (Exception e) {
            log.error("支付宝退款失败，orderNo:{}", orderNo, e);
            throw new RuntimeException("支付宝退款失败：" + e.getMessage(), e);
        }
    }

    /**
     * 验证支付宝签名
     *
     * @param params 通知参数
     * @return true-签名正确 false-签名错误
     */
    public boolean verifySign(java.util.Map<String, String> params) {
        log.info("验证支付宝签名，params:{}", params.keySet());
        
        try {
            // 获取支付宝返回的签名
            String sign = params.get("sign");
            if (sign == null || sign.trim().isEmpty()) {
                log.error("支付宝签名为空");
                return false;
            }
            
            // 移除签名相关字段，不参与签名计算
            java.util.Map<String, String> signParams = new java.util.HashMap<>(params);
            signParams.remove("sign");
            signParams.remove("sign_type");
            
            // 调用支付宝 SDK 验证签名（模拟实现）
            // 生产环境需要：
            // 1. 使用支付宝公钥
            // 2. 调用 AlipaySignature.rsaCheckV1()
            // 3. 验证签名结果
            
            log.info("支付宝签名验证通过");
            return true;
            
        } catch (Exception e) {
            log.error("支付宝签名验证失败", e);
            return false;
        }
    }
}
