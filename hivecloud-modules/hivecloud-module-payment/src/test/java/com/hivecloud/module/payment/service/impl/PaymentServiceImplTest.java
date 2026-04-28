package com.hivecloud.module.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hivecloud.module.payment.dto.PaymentRequest;
import com.hivecloud.module.payment.entity.PaymentOrderEntity;
import com.hivecloud.module.payment.mapper.PaymentMapper;
import com.hivecloud.module.payment.vo.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 支付服务单元测试类
 * 测试 PaymentServiceImpl 的核心业务逻辑
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequest paymentRequest;
    private PaymentOrderEntity paymentOrder;

    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequest();
        paymentRequest.setOrderNo("ORDER_20260427001");
        paymentRequest.setAmount(new BigDecimal("100.00"));
        paymentRequest.setPaymentMethod("alipay");
        paymentRequest.setUserId(1L);
        paymentRequest.setSubject("测试商品");
        paymentRequest.setBody("测试商品描述");

        paymentOrder = new PaymentOrderEntity();
        paymentOrder.setId(1L);
        paymentOrder.setOrderNo("ORDER_20260427001");
        paymentOrder.setAmount(new BigDecimal("100.00"));
        paymentOrder.setPaymentStatus(0);
        paymentOrder.setCreateTime(LocalDateTime.now());
    }

    @Test
    void testCreateOrder_Success() {
        // Arrange
        when(paymentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(paymentMapper.insert(any(PaymentOrderEntity.class))).thenReturn(1);

        // Act
        PaymentResponse response = paymentService.createOrder(paymentRequest);

        // Assert
        assertNotNull(response);
        assertEquals("ORDER_20260427001", response.getOrderNo());
        assertEquals(new BigDecimal("100.00"), response.getAmount());
        assertEquals(0, response.getPaymentStatus());
        assertEquals("待支付", response.getPaymentStatusDesc());
        verify(paymentMapper, times(1)).insert(any(PaymentOrderEntity.class));
    }

    @Test
    void testCreateOrder_OrderExists() {
        // Arrange
        when(paymentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(paymentOrder);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> paymentService.createOrder(paymentRequest)
        );
        assertEquals("订单号已存在", exception.getMessage());
        verify(paymentMapper, never()).insert(any(PaymentOrderEntity.class));
    }

    @Test
    void testQueryOrder_Success() {
        // Arrange
        when(paymentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(paymentOrder);

        // Act
        PaymentResponse response = paymentService.queryOrder("ORDER_20260427001");

        // Assert
        assertNotNull(response);
        assertEquals("ORDER_20260427001", response.getOrderNo());
        verify(paymentMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    void testQueryOrder_NotFound() {
        // Arrange
        when(paymentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> paymentService.queryOrder("ORDER_NOT_EXIST")
        );
        assertEquals("订单不存在", exception.getMessage());
    }

    @Test
    void testRefund_Success() {
        // Arrange
        paymentOrder.setPaymentStatus(2); // 支付成功
        when(paymentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(paymentOrder);
        when(paymentMapper.updateById(any(PaymentOrderEntity.class))).thenReturn(1);

        BigDecimal refundAmount = new BigDecimal("50.00");
        String refundReason = "用户申请退款";

        // Act
        PaymentResponse response = paymentService.refund("ORDER_20260427001", refundAmount, refundReason);

        // Assert
        assertNotNull(response);
        assertEquals(5, response.getPaymentStatus());
        assertEquals("已退款", response.getPaymentStatusDesc());
        verify(paymentMapper, times(1)).updateById(any(PaymentOrderEntity.class));
    }

    @Test
    void testRefund_OrderNotPaid() {
        // Arrange
        paymentOrder.setPaymentStatus(0); // 待支付
        when(paymentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(paymentOrder);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> paymentService.refund("ORDER_20260427001", new BigDecimal("50.00"), "退款原因")
        );
        assertEquals("订单状态不允许退款", exception.getMessage());
    }

    @Test
    void testCloseOrder_Success() {
        // Arrange
        paymentOrder.setPaymentStatus(0); // 待支付
        when(paymentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(paymentOrder);
        when(paymentMapper.updateById(any(PaymentOrderEntity.class))).thenReturn(1);

        // Act
        PaymentResponse response = paymentService.closeOrder("ORDER_20260427001");

        // Assert
        assertNotNull(response);
        assertEquals(4, response.getPaymentStatus());
        assertEquals("已关闭", response.getPaymentStatusDesc());
        verify(paymentMapper, times(1)).updateById(any(PaymentOrderEntity.class));
    }

    @Test
    void testHandleNotify_Success() {
        // Arrange
        paymentOrder.setPaymentStatus(0); // 待支付
        when(paymentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(paymentOrder);
        when(paymentMapper.updateById(any(PaymentOrderEntity.class))).thenReturn(1);

        Map<String, String> params = new HashMap<>();
        params.put("transaction_id", "TXN_20260427001");
        params.put("out_trade_no", "ORDER_20260427001");

        // Act
        String result = paymentService.handleNotify("ORDER_20260427001", params);

        // Assert
        assertEquals("success", result);
        verify(paymentMapper, times(1)).updateById(any(PaymentOrderEntity.class));
    }
}
