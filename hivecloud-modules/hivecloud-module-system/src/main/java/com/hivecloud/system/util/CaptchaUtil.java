package com.hivecloud.system.util;

import cn.hutool.captcha.LineCaptcha;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码工具类
 * 使用 Hutool Captcha 生成线性干扰验证码
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Slf4j
@Component
public class CaptchaUtil {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${captcha.expiration:300}")
    private Integer expiration;

    public CaptchaUtil(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 生成验证码
     *
     * @return 验证码 Key 和 Base64 图片
     */
    public CaptchaData generateCaptcha() {
        // 定义图形验证码的长、宽、验证码字符的个数、干扰线数量
        // Hutool LineCaptcha: 120x40, 4 个字符，2 条干扰线
        LineCaptcha captcha = new LineCaptcha(120, 40, 4, 2);
        
        // 设置字体（Hutool 通过 setFont 设置字体大小）
        captcha.setFont(new Font("Arial", Font.PLAIN, 28));
        
        // 获取验证码图片（Base64 编码）
        String imageBase64 = captcha.getImageBase64();
        // 去除 data:image/png;base64, 前缀
        if (imageBase64.contains(",")) {
            imageBase64 = imageBase64.split(",")[1];
        }

        // 获取验证码文本
        String code = captcha.getCode();

        // 生成验证码 Key
        String captchaKey = UUID.randomUUID().toString();

        // 存储验证码到 Redis（5 分钟过期）
        String redisKey = "captcha:" + captchaKey;
        stringRedisTemplate.opsForValue().set(redisKey, code, expiration, TimeUnit.SECONDS);

        log.debug("生成验证码：key={}, answer={}", captchaKey, code);

        return CaptchaData.builder()
                .captchaKey(captchaKey)
                .captchaImage(imageBase64)
                .expiration((long) expiration)
                .build();
    }

    /**
     * 验证验证码
     *
     * @param captchaKey 验证码 Key
     * @param captchaCode 用户输入的验证码
     * @return true-正确，false-错误
     */
    public boolean validateCaptcha(String captchaKey, String captchaCode) {
        if (captchaKey == null || captchaCode == null) {
            return false;
        }

        String redisKey = "captcha:" + captchaKey;
        String correctCode = stringRedisTemplate.opsForValue().get(redisKey);

        if (correctCode == null) {
            log.warn("验证码已过期：key={}", captchaKey);
            return false;
        }

        // 验证码不区分大小写
        boolean valid = correctCode.equalsIgnoreCase(captchaCode.trim());
        
        if (valid) {
            // 验证成功后删除验证码（防止重复使用）
            stringRedisTemplate.delete(redisKey);
            log.debug("验证码验证成功：key={}", captchaKey);
        } else {
            log.warn("验证码验证失败：key={}, input={}, correct={}", captchaKey, captchaCode, correctCode);
        }

        return valid;
    }

    /**
     * 删除验证码（用于验证失败或注销时）
     *
     * @param captchaKey 验证码 Key
     */
    public void removeCaptcha(String captchaKey) {
        if (captchaKey != null) {
            String redisKey = "captcha:" + captchaKey;
            stringRedisTemplate.delete(redisKey);
            log.debug("删除验证码：key={}", captchaKey);
        }
    }

    /**
     * 验证码数据内部类
     */
    @Data
    @Builder
    public static class CaptchaData {
        private String captchaKey;
        private String captchaImage;
        private Long expiration;
    }
}
