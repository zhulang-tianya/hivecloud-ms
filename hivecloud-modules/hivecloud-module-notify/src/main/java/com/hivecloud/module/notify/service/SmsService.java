package com.hivecloud.module.notify.service;

/**
 * 短信服务接口
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
public interface SmsService {

    /**
     * 发送短信
     *
     * @param phone 手机号
     * @param message 短信内容
     */
    void send(String phone, String message);

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @param code 验证码
     */
    void sendCode(String phone, String code);
}
