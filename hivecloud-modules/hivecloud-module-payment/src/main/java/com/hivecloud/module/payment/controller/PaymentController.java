package com.hivecloud.module.payment.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.module.payment.dto.PaymentRequest;
import com.hivecloud.module.payment.service.PaymentService;
import com.hivecloud.module.payment.vo.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付模块接口控制器
 * <p>
 * 提供支付、查询、退款、关闭订单等接口
 * 支持支付宝、微信支付、银联支付三种支付渠道
 * </p>
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Slf4j
@RestController
@RequestMapping("/api/payment")
@Validated
@Tag(name = "支付管理", description = "提供支付、查询、退款、关闭订单等接口")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * 创建支付订单
     *
     * @param request 支付请求参数
     * @return 支付响应
     */
    @PostMapping("/create")
    @Operation(summary = "创建支付订单", description = "支持支付宝、微信支付、银联支付三种支付方式")
    @Parameter(name = "request", description = "支付请求参数", required = true)
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
    @Operation(summary = "查询支付订单", description = "根据商户订单号查询支付状态和详情")
    @Parameter(name = "orderNo", description = "商户订单号", required = true, example = "ORDER20260428001")
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
    @Operation(summary = "退款", description = "支持部分退款和全额退款")
    @Parameter(name = "orderNo", description = "商户订单号", required = true)
    @Parameter(name = "amount", description = "退款金额（元）", required = true, example = "99.99")
    @Parameter(name = "reason", description = "退款原因", example = "商品质量问题")
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
    @Operation(summary = "关闭订单", description = "关闭未支付的订单")
    @Parameter(name = "orderNo", description = "商户订单号", required = true)
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
    @Operation(summary = "支付宝异步通知", description = "处理支付宝支付结果异步通知")
    @Parameter(name = "request", description = "HTTP 请求对象", hidden = true)
    @Parameter(name = "response", description = "HTTP 响应对象", hidden = true)
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
    @Operation(summary = "微信支付异步通知", description = "处理微信支付结果异步通知")
    @Parameter(name = "request", description = "HTTP 请求对象", hidden = true)
    @Parameter(name = "response", description = "HTTP 响应对象", hidden = true)
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
