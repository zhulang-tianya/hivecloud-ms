package com.hivecloud.system.util;

import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码工具类
 * 使用 EasyCaptcha 生成算术验证码
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Slf4j
@Component
public class CaptchaUtil {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${captcha.expiration:300}")
    private Integer expiration;

    public CaptchaUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 生成验证码
     *
     * @return 验证码 Key 和 Base64 图片
     */
    public CaptchaData generateCaptcha() {
        // 生成算术验证码
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(120, 40);
        captcha.setLen(2); // 2 位数运算

        // 获取验证码图片（Base64 编码）
        String imageBase64 = captcha.toBase64();
        // 去除 data:image/png;base64, 前缀
        if (imageBase64.contains(",")) {
            imageBase64 = imageBase64.split(",")[1];
        }

        // 生成验证码 Key
        String captchaKey = UUID.randomUUID().toString();

        // 存储验证码到 Redis（5 分钟过期）
        String redisKey = "captcha:" + captchaKey;
        redisTemplate.opsForValue().set(redisKey, captcha.text(), expiration, TimeUnit.SECONDS);

        log.debug("生成验证码：key={}, answer={}", captchaKey, captcha.text());

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
        String correctCode = redisTemplate.opsForValue().get(redisKey);

        if (correctCode == null) {
            log.warn("验证码已过期：key={}", captchaKey);
            return false;
        }

        // 验证码不区分大小写
        boolean valid = correctCode.equalsIgnoreCase(captchaCode.trim());
        
        if (valid) {
            // 验证成功后删除验证码（防止重复使用）
            redisTemplate.delete(redisKey);
            log.debug("验证码验证成功：key={}", captchaKey);
        } else {
            log.warn("验证码验证失败：key={}, input={}, correct={}", captchaKey, captchaCode, correctCode);
        }

        return valid;
    }

    /**
     * 验证码数据类
     */
    @lombok.Builder
    @lombok.Data
    public static class CaptchaData {
        private String captchaKey;
        private String captchaImage;
        private Long expiration;
    }
}
