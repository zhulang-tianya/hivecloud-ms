package com.hivecloud.module.payment.adapter;

import com.hivecloud.module.payment.vo.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 微信支付适配器
 * 处理微信支付相关逻辑
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Slf4j
@Component
public class WechatPayAdapter {

    /**
     * 创建微信支付订单
     *
     * @param orderNo 订单号
     * @param amount 金额
     * @param subject 商品标题
     * @param body 商品描述
     * @return 支付响应
     */
    public PaymentResponse createOrder(String orderNo, java.math.BigDecimal amount, String subject, String body) {
        log.info("创建微信支付订单，orderNo:{}, amount:{}, subject:{}", orderNo, amount, subject);
        
        try {
            // 构建微信支付订单参数
            java.util.Map<String, String> wechatParams = new java.util.HashMap<>();
            wechatParams.put("out_trade_no", orderNo);
            wechatParams.put("total_fee", amount.multiply(new java.math.BigDecimal("100")).toString());
            wechatParams.put("body", subject);
            wechatParams.put("detail", body);
            wechatParams.put("notify_url", "https://your-domain.com/api/payment/wechat/notify");
            wechatParams.put("trade_type", "NATIVE");
            
            // 调用微信支付 SDK 创建订单（模拟实现）
            // 生产环境需要：
            // 1. 引入微信支付 SDK 依赖
            // 2. 配置 AppID、MchID、API 密钥
            // 3. 调用统一下单接口
            // 4. 获取 code_url 用于生成二维码
            
            log.info("微信支付订单创建成功，orderNo:{}", orderNo);
            
            // 返回二维码链接（模拟）
            String qrCode = String.format("weixin://wxpay/bizpayurl?pr=%s", orderNo);
            
            return PaymentResponse.builder()
                    .orderNo(orderNo)
                    .amount(amount)
                    .paymentMethod("wechat")
                    .paymentStatus(0)
                    .paymentStatusDesc("待支付")
                    .qrCode(qrCode)
                    .build();
                    
        } catch (Exception e) {
            log.error("创建微信支付订单失败，orderNo:{}", orderNo, e);
            throw new RuntimeException("创建微信支付订单失败：" + e.getMessage(), e);
        }
    }

    /**
     * 查询微信订单
     *
     * @param orderNo 订单号
     * @return 支付响应
     */
    public PaymentResponse queryOrder(String orderNo) {
        log.info("查询微信订单，orderNo:{}", orderNo);
        
        try {
            // 构建查询参数
            java.util.Map<String, String> queryParams = new java.util.HashMap<>();
            queryParams.put("out_trade_no", orderNo);
            
            // 调用微信支付 SDK 查询订单（模拟实现）
            // 生产环境需要：
            // 1. 调用订单查询接口
            // 2. 解析返回的订单状态
            
            log.info("微信订单查询成功，orderNo:{}", orderNo);
            
            // 模拟返回支付成功状态
            return PaymentResponse.builder()
                    .orderNo(orderNo)
                    .paymentStatus(2)
                    .paymentStatusDesc("支付成功")
                    .transactionId("WECHAT_" + System.currentTimeMillis())
                    .build();
                    
        } catch (Exception e) {
            log.error("查询微信订单失败，orderNo:{}", orderNo, e);
            throw new RuntimeException("查询微信订单失败：" + e.getMessage(), e);
        }
    }

    /**
     * 微信退款
     *
     * @param orderNo 订单号
     * @param amount 退款金额
     * @param reason 退款原因
     * @return 支付响应
     */
    public PaymentResponse refund(String orderNo, java.math.BigDecimal amount, String reason) {
        log.info("微信退款，orderNo:{}, amount:{}, reason:{}", orderNo, amount, reason);
        
        try {
            // 构建退款参数
            java.util.Map<String, String> refundParams = new java.util.HashMap<>();
            refundParams.put("out_trade_no", orderNo);
            refundParams.put("total_fee", amount.multiply(new java.math.BigDecimal("100")).toString());
            refundParams.put("refund_fee", amount.multiply(new java.math.BigDecimal("100")).toString());
            refundParams.put("refund_desc", reason);
            refundParams.put("out_refund_no", "REFUND_" + System.currentTimeMillis());
            
            // 调用微信支付 SDK 退款（模拟实现）
            // 生产环境需要：
            // 1. 调用退款申请接口
            // 2. 处理退款结果
            
            log.info("微信退款成功，orderNo:{}, amount:{}", orderNo, amount);
            
            return PaymentResponse.builder()
                    .orderNo(orderNo)
                    .paymentStatus(5)
                    .paymentStatusDesc("已退款")
                    .refundAmount(amount)
                    .refundReason(reason)
                    .build();
                    
        } catch (Exception e) {
            log.error("微信退款失败，orderNo:{}", orderNo, e);
            throw new RuntimeException("微信退款失败：" + e.getMessage(), e);
        }
    }

    /**
     * 验证微信签名
     *
     * @param params 通知参数
     * @return true-签名正确 false-签名错误
     */
    public boolean verifySign(java.util.Map<String, String> params) {
        log.info("验证微信签名，params:{}", params.keySet());
        
        try {
            // 获取微信返回的签名
            String sign = params.get("sign");
            if (sign == null || sign.trim().isEmpty()) {
                log.error("微信签名为空");
                return false;
            }
            
            // 移除签名字段，不参与签名计算
            java.util.Map<String, String> signParams = new java.util.HashMap<>(params);
            signParams.remove("sign");
            
            // 调用微信支付 SDK 验证签名（模拟实现）
            // 生产环境需要：
            // 1. 使用 API 密钥
            // 2. 按照微信签名算法计算签名
            // 3. 比对签名结果
            
            log.info("微信签名验证通过");
            return true;
            
        } catch (Exception e) {
            log.error("微信签名验证失败", e);
            return false;
        }
    }
}
