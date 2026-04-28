package com.hivecloud.module.notify.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.module.notify.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信控制器
 * <p>
 * 提供短信发送、验证码发送等接口
 * </p>
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
@Tag(name = "短信管理", description = "短信发送、验证码发送等接口")
public class SmsController {

    private final SmsService smsService;

    /**
     * 发送短信
     *
     * @param phone 手机号
     * @param message 短信内容
     * @return 结果
     */
    @PostMapping("/send")
    @Operation(summary = "发送短信", description = "发送指定内容的短信")
    @Parameter(name = "phone", description = "手机号", required = true, example = "13800138000")
    @Parameter(name = "message", description = "短信内容", required = true, example = "您的验证码是 123456")
    public Result<Void> send(@RequestParam String phone, @RequestParam String message) {
        validatePhone(phone);
        smsService.send(phone, message);
        return Result.success(null, "发送成功");
    }

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @return 结果（包含生成的验证码）
     */
    @PostMapping("/code")
    @Operation(summary = "发送验证码", description = "发送 6 位数字验证码到指定手机号")
    @Parameter(name = "phone", description = "手机号", required = true, example = "13800138000")
    public Result<Map<String, Object>> sendCode(@RequestParam String phone) {
        validatePhone(phone);
        String code = generateCode();
        smsService.sendCode(phone, code);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        return Result.success(result, "验证码发送成功");
    }

    /**
     * 验证手机号格式
     */
    private void validatePhone(String phone) {
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            throw new IllegalArgumentException("手机号格式不正确");
        }
    }

    /**
     * 生成 6 位数字验证码
     */
    private String generateCode() {
        return String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
    }
}
