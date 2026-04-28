package com.hivecloud.module.payment.adapter;

import com.hivecloud.module.payment.vo.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 银联支付适配器
 * 处理银联支付相关逻辑
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Slf4j
@Component
public class UnionPayAdapter {

    /**
     * 创建银联支付订单
     *
     * @param orderNo 订单号
     * @param amount 金额
     * @param subject 商品标题
     * @return 支付响应
     */
    public PaymentResponse createOrder(String orderNo, java.math.BigDecimal amount, String subject) {
        log.info("创建银联支付订单，orderNo:{}, amount:{}, subject:{}", orderNo, amount, subject);
        
        try {
            // 构建银联订单参数
            java.util.Map<String, String> unionPayParams = new java.util.HashMap<>();
            unionPayParams.put("orderId", orderNo);
            unionPayParams.put("txnAmt", amount.multiply(new java.math.BigDecimal("100")).toString());
            unionPayParams.put("txnSubAmt", subject);
            unionPayParams.put("frontTransUrl", "https://gateway.95516.com/gateway/api/frontTransReq.do");
            unionPayParams.put("backTransUrl", "https://your-domain.com/api/payment/unionpay/notify");
            
            // 调用银联 SDK 创建订单（模拟实现）
            // 生产环境需要：
            // 1. 引入银联 SDK 依赖
            // 2. 配置 MerId、证书路径
            // 3. 调用前台交易接口
            // 4. 获取 HTML form 表单
            
            log.info("银联订单创建成功，orderNo:{}", orderNo);
            
            // 返回支付链接（模拟）
            String payUrl = "https://gateway.95516.com/gateway/api/frontTransReq.do?orderId=" + orderNo;
            
            return PaymentResponse.builder()
                    .orderNo(orderNo)
                    .amount(amount)
                    .paymentMethod("unionpay")
                    .paymentStatus(0)
                    .paymentStatusDesc("待支付")
                    .payUrl(payUrl)
                    .build();
                    
        } catch (Exception e) {
            log.error("创建银联订单失败，orderNo:{}", orderNo, e);
            throw new RuntimeException("创建银联订单失败：" + e.getMessage(), e);
        }
    }

    /**
     * 查询银联订单
     *
     * @param orderNo 订单号
     * @return 支付响应
     */
    public PaymentResponse queryOrder(String orderNo) {
        log.info("查询银联订单，orderNo:{}", orderNo);
        
        try {
            // 构建查询参数
            java.util.Map<String, String> queryParams = new java.util.HashMap<>();
            queryParams.put("orderId", orderNo);
            
            // 调用银联 SDK 查询订单（模拟实现）
            // 生产环境需要：
            // 1. 调用订单查询接口
            // 2. 解析返回的订单状态
            
            log.info("银联订单查询成功，orderNo:{}", orderNo);
            
            // 模拟返回支付成功状态
            return PaymentResponse.builder()
                    .orderNo(orderNo)
                    .paymentStatus(2)
                    .paymentStatusDesc("支付成功")
                    .transactionId("UNIONPAY_" + System.currentTimeMillis())
                    .build();
                    
        } catch (Exception e) {
            log.error("查询银联订单失败，orderNo:{}", orderNo, e);
            throw new RuntimeException("查询银联订单失败：" + e.getMessage(), e);
        }
    }

    /**
     * 银联退款
     *
     * @param orderNo 订单号
     * @param amount 退款金额
     * @param reason 退款原因
     * @return 支付响应
     */
    public PaymentResponse refund(String orderNo, java.math.BigDecimal amount, String reason) {
        log.info("银联退款，orderNo:{}, amount:{}, reason:{}", orderNo, amount, reason);
        
        try {
            // 构建退款参数
            java.util.Map<String, String> refundParams = new java.util.HashMap<>();
            refundParams.put("orderId", orderNo);
            refundParams.put("txnAmt", amount.multiply(new java.math.BigDecimal("100")).toString());
            refundParams.put("refundAmt", amount.multiply(new java.math.BigDecimal("100")).toString());
            refundParams.put("refundReason", reason);
            refundParams.put("refundOrderId", "REFUND_" + System.currentTimeMillis());
            
            // 调用银联 SDK 退款（模拟实现）
            // 生产环境需要：
            // 1. 调用退款申请接口
            // 2. 处理退款结果
            
            log.info("银联退款成功，orderNo:{}, amount:{}", orderNo, amount);
            
            return PaymentResponse.builder()
                    .orderNo(orderNo)
                    .paymentStatus(5)
                    .paymentStatusDesc("已退款")
                    .refundAmount(amount)
                    .refundReason(reason)
                    .build();
                    
        } catch (Exception e) {
            log.error("银联退款失败，orderNo:{}", orderNo, e);
            throw new RuntimeException("银联退款失败：" + e.getMessage(), e);
        }
    }

    /**
     * 验证银联签名
     *
     * @param params 通知参数
     * @return true-签名正确 false-签名错误
     */
    public boolean verifySign(java.util.Map<String, String> params) {
        log.info("验证银联签名，params:{}", params.keySet());
        
        try {
            // 获取银联返回的签名
            String sign = params.get("signature");
            if (sign == null || sign.trim().isEmpty()) {
                log.error("银联签名为空");
                return false;
            }
            
            // 移除签名字段，不参与签名计算
            java.util.Map<String, String> signParams = new java.util.HashMap<>(params);
            signParams.remove("signature");
            signParams.remove("signMethod");
            
            // 调用银联 SDK 验证签名（模拟实现）
            // 生产环境需要：
            // 1. 使用银联公钥证书
            // 2. 按照银联签名算法计算签名
            // 3. 比对签名结果
            
            log.info("银联签名验证通过");
            return true;
            
        } catch (Exception e) {
            log.error("银联签名验证失败", e);
            return false;
        }
    }
}
