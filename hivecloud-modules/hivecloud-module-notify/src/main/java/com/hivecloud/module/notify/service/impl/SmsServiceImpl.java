package com.hivecloud.module.notify.service.impl;

import com.hivecloud.module.notify.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 短信服务实现类
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public void send(String phone, String message) {
        log.info("发送短信：phone={}, message={}", phone, message);
        // TODO: 集成真实的短信服务商（阿里云、腾讯云等）
        // 这里仅做日志记录，实际使用时需要调用短信 API
    }

    @Override
    public void sendCode(String phone, String code) {
        log.info("发送验证码：phone={}, code={}", phone, code);
        String message = "您的验证码是：" + code + "，5 分钟内有效。";
        send(phone, message);
    }
}
