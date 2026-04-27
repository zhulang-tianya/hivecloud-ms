package com.hivecloud.module.payment.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.module.payment.dto.PaymentRequest;
import com.hivecloud.module.payment.service.PaymentService;
import com.hivecloud.module.payment.vo.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付模块接口控制器
 * 提供支付、查询、退款、关闭订单等接口
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Slf4j
@RestController
@RequestMapping("/api/payment")
@Validated
@Tag(name = "支付管理", description = "支付订单管理接口")
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    /**
     * 创建支付订单
     *
     * @param request 支付请求参数
     * @return 支付响应
     */
    @PostMapping("/create")
    @Operation(summary = "创建支付订单")
    public Result<PaymentResponse> createOrder(@Validated @RequestBody PaymentRequest request) {
        log.info("创建支付订单接口开始执行，请求参数：{}", request);
        PaymentResponse response = paymentService.createOrder(request);
        log.info("创建支付订单接口执行成功，orderNo:{}", request.getOrderNo());
        return Result.success(response);
    }

    /**
     * 查询支付订单
     *
     * @param orderNo 商户订单号
     * @return 支付响应
     */
    @GetMapping("/query/{orderNo}")
    @Operation(summary = "查询支付订单")
    public Result<PaymentResponse> queryOrder(@PathVariable String orderNo) {
        log.info("查询支付订单接口开始执行，orderNo:{}", orderNo);
        PaymentResponse response = paymentService.queryOrder(orderNo);
        log.info("查询支付订单接口执行成功，orderNo:{}", orderNo);
        return Result.success(response);
    }

    /**
     * 退款
     *
     * @param orderNo 商户订单号
     * @param amount 退款金额
     * @param reason 退款原因
     * @return 支付响应
     */
    @PostMapping("/refund/{orderNo}")
    @Operation(summary = "退款")
    public Result<PaymentResponse> refund(
            @PathVariable String orderNo,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String reason) {
        log.info("退款接口开始执行，orderNo:{}, amount:{}, reason:{}", orderNo, amount, reason);
        PaymentResponse response = paymentService.refund(orderNo, amount, reason);
        log.info("退款接口执行成功，orderNo:{}", orderNo);
        return Result.success(response);
    }

    /**
     * 关闭订单
     *
     * @param orderNo 商户订单号
     * @return 支付响应
     */
    @PostMapping("/close/{orderNo}")
    @Operation(summary = "关闭订单")
    public Result<PaymentResponse> closeOrder(@PathVariable String orderNo) {
        log.info("关闭订单接口开始执行，orderNo:{}", orderNo);
        PaymentResponse response = paymentService.closeOrder(orderNo);
        log.info("关闭订单接口执行成功，orderNo:{}", orderNo);
        return Result.success(response);
    }

    /**
     * 处理异步通知（支付宝）
     *
     * @param request HTTP 请求
     * @param response HTTP 响应
     * @throws IOException IO 异常
     */
    @PostMapping("/notify/alipay")
    @Operation(summary = "支付宝异步通知")
    public void alipayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("收到支付宝异步通知");

        Map<String, String> params = getRequestParams(request);
        String orderNo = params.get("out_trade_no");

        String result = paymentService.handleNotify(orderNo, params);

        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(result);
        log.info("支付宝异步通知处理完成，orderNo:{}, result:{}", orderNo, result);
    }

    /**
     * 处理异步通知（微信）
     *
     * @param request HTTP 请求
     * @param response HTTP 响应
     * @throws IOException IO 异常
     */
    @PostMapping("/notify/wechat")
    @Operation(summary = "微信异步通知")
    public void wechatNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("收到微信异步通知");

        Map<String, String> params = getRequestParams(request);
        String orderNo = params.get("out_trade_no");

        String result = paymentService.handleNotify(orderNo, params);

        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(result);
        log.info("微信异步通知处理完成，orderNo:{}, result:{}", orderNo, result);
    }

    /**
     * 获取请求参数
     *
     * @param request HTTP 请求
     * @return 参数 Map
     */
    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                params.put(entry.getKey(), values[0]);
            }
        }
        return params;
    }
}
