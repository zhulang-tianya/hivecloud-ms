package com.hivecloud.module.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hivecloud.module.payment.dto.PaymentRequest;
import com.hivecloud.module.payment.entity.PaymentOrderEntity;
import com.hivecloud.module.payment.mapper.PaymentMapper;
import com.hivecloud.module.payment.service.PaymentService;
import com.hivecloud.module.payment.vo.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 支付服务实现类
 * 处理支付相关核心业务逻辑
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentOrderMapper;

    /**
     * 统一下单
     *
     * @param request 支付请求参数
     * @return 支付响应
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse createOrder(PaymentRequest request) {
        log.info("开始创建支付订单，orderNo:{}, amount:{}, paymentMethod:{}", 
                request.getOrderNo(), request.getAmount(), request.getPaymentMethod());

        // 检查订单是否已存在
        PaymentOrderEntity existingOrder = getOrderByOrderNo(request.getOrderNo());
        if (existingOrder != null) {
            log.warn("订单已存在，orderNo:{}", request.getOrderNo());
            throw new IllegalArgumentException("订单号已存在");
        }

        // 创建支付订单
        PaymentOrderEntity paymentOrder = new PaymentOrderEntity();
        paymentOrder.setOrderNo(request.getOrderNo());
        paymentOrder.setAmount(request.getAmount());
        paymentOrder.setCurrency("CNY");
        paymentOrder.setPaymentMethod(request.getPaymentMethod());
        paymentOrder.setPaymentStatus(0); // 待支付
        paymentOrder.setUserId(request.getUserId());
        paymentOrder.setSubject(request.getSubject());
        paymentOrder.setBody(request.getBody());
        paymentOrder.setNotifyUrl(request.getNotifyUrl());
        paymentOrder.setReturnUrl(request.getReturnUrl());
        paymentOrder.setExtraParams(request.getExtraParams());
        paymentOrder.setCreateTime(LocalDateTime.now());
        paymentOrder.setUpdateTime(LocalDateTime.now());

        // 保存到数据库
        paymentOrderMapper.insert(paymentOrder);

        log.info("支付订单创建成功，orderNo:{}, paymentId:{}", 
                request.getOrderNo(), paymentOrder.getId());

        // 构建支付响应
        return PaymentResponse.builder()
                .paymentId(paymentOrder.getId())
                .orderNo(paymentOrder.getOrderNo())
                .amount(paymentOrder.getAmount())
                .paymentMethod(paymentOrder.getPaymentMethod())
                .paymentStatus(paymentOrder.getPaymentStatus())
                .paymentStatusDesc("待支付")
                .build();
    }

    /**
     * 查询订单
     *
     * @param orderNo 商户订单号
     * @return 支付响应
     */
    @Override
    @Transactional(readOnly = true)
    public PaymentResponse queryOrder(String orderNo) {
        log.info("开始查询支付订单，orderNo:{}", orderNo);

        PaymentOrderEntity paymentOrder = getOrderByOrderNo(orderNo);
        if (paymentOrder == null) {
            log.warn("订单不存在，orderNo:{}", orderNo);
            throw new IllegalArgumentException("订单不存在");
        }

        log.info("订单查询成功，orderNo:{}, status:{}", orderNo, paymentOrder.getPaymentStatus());

        return PaymentResponse.builder()
                .paymentId(paymentOrder.getId())
                .orderNo(paymentOrder.getOrderNo())
                .transactionId(paymentOrder.getTransactionId())
                .amount(paymentOrder.getAmount())
                .paymentMethod(paymentOrder.getPaymentMethod())
                .paymentStatus(paymentOrder.getPaymentStatus())
                .paymentStatusDesc(getPaymentStatusDesc(paymentOrder.getPaymentStatus()))
                .paymentTime(paymentOrder.getPaymentTime())
                .build();
    }

    /**
     * 退款
     *
     * @param orderNo 商户订单号
     * @param amount 退款金额
     * @param reason 退款原因
     * @return 支付响应
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse refund(String orderNo, java.math.BigDecimal amount, String reason) {
        log.info("开始退款，orderNo:{}, amount:{}, reason:{}", orderNo, amount, reason);

        PaymentOrderEntity paymentOrder = getOrderByOrderNo(orderNo);
        if (paymentOrder == null) {
            log.warn("订单不存在，orderNo:{}", orderNo);
            throw new IllegalArgumentException("订单不存在");
        }

        if (paymentOrder.getPaymentStatus() != 2) {
            log.warn("订单状态不允许退款，orderNo:{}, status:{}", orderNo, paymentOrder.getPaymentStatus());
            throw new IllegalArgumentException("订单状态不允许退款");
        }

        // 更新订单状态
        paymentOrder.setPaymentStatus(5); // 已退款
        paymentOrder.setRefundAmount(amount);
        paymentOrder.setRefundReason(reason);
        paymentOrder.setRefundTime(LocalDateTime.now());
        paymentOrder.setUpdateTime(LocalDateTime.now());

        // 更新数据库
        paymentOrderMapper.updateById(paymentOrder);

        log.info("退款成功，orderNo:{}", orderNo);

        return PaymentResponse.builder()
                .paymentId(paymentOrder.getId())
                .orderNo(paymentOrder.getOrderNo())
                .paymentStatus(paymentOrder.getPaymentStatus())
                .paymentStatusDesc("已退款")
                .build();
    }

    /**
     * 关闭订单
     *
     * @param orderNo 商户订单号
     * @return 支付响应
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse closeOrder(String orderNo) {
        log.info("开始关闭订单，orderNo:{}", orderNo);

        PaymentOrderEntity paymentOrder = getOrderByOrderNo(orderNo);
        if (paymentOrder == null) {
            log.warn("订单不存在，orderNo:{}", orderNo);
            throw new IllegalArgumentException("订单不存在");
        }

        if (paymentOrder.getPaymentStatus() != 0) {
            log.warn("订单状态不允许关闭，orderNo:{}, status:{}", orderNo, paymentOrder.getPaymentStatus());
            throw new IllegalArgumentException("订单状态不允许关闭");
        }

        // 更新订单状态
        paymentOrder.setPaymentStatus(4); // 已关闭
        paymentOrder.setCloseTime(LocalDateTime.now());
        paymentOrder.setUpdateTime(LocalDateTime.now());

        // 更新数据库
        paymentOrderMapper.updateById(paymentOrder);

        log.info("订单关闭成功，orderNo:{}", orderNo);

        return PaymentResponse.builder()
                .paymentId(paymentOrder.getId())
                .orderNo(paymentOrder.getOrderNo())
                .paymentStatus(paymentOrder.getPaymentStatus())
                .paymentStatusDesc("已关闭")
                .build();
    }

    /**
     * 处理异步通知
     *
     * @param orderNo 商户订单号
     * @param params 通知参数
     * @return 处理结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handleNotify(String orderNo, Map<String, String> params) {
        log.info("开始处理异步通知，orderNo:{}, params:{}", orderNo, params);

        PaymentOrderEntity paymentOrder = getOrderByOrderNo(orderNo);
        if (paymentOrder == null) {
            log.warn("订单不存在，orderNo:{}", orderNo);
            return "fail";
        }

        if (paymentOrder.getPaymentStatus() != 0) {
            log.warn("订单状态异常，无需处理，orderNo:{}, status:{}", orderNo, paymentOrder.getPaymentStatus());
            return "success";
        }

        // 验证签名（由支付适配器完成）
        // 更新订单状态
        String transactionId = params.get("transaction_id");
        paymentOrder.setTransactionId(transactionId);
        paymentOrder.setPaymentStatus(2); // 支付成功
        paymentOrder.setPaymentTime(LocalDateTime.now());
        paymentOrder.setUpdateTime(LocalDateTime.now());
        paymentOrderMapper.updateById(paymentOrder);

        log.info("异步通知处理成功，orderNo:{}, transactionId:{}", orderNo, transactionId);
        return "success";
    }

    /**
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return 支付订单实体
     */
    private PaymentOrderEntity getOrderByOrderNo(String orderNo) {
        LambdaQueryWrapper<PaymentOrderEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentOrderEntity::getOrderNo, orderNo);
        return paymentOrderMapper.selectOne(wrapper);
    }

    /**
     * 获取支付状态描述
     *
     * @param status 支付状态
     * @return 状态描述
     */
    private String getPaymentStatusDesc(Integer status) {
        switch (status) {
            case 0:
                return "待支付";
            case 1:
                return "支付中";
            case 2:
                return "支付成功";
            case 3:
                return "支付失败";
            case 4:
                return "已关闭";
            case 5:
                return "已退款";
            default:
                return "未知状态";
        }
    }
}
