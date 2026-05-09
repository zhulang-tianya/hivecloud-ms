package com.hivecloud.system.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 验证码响应 VO
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Data
@Builder
public class CaptchaVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 验证码 Key（用于提交时验证）
     */
    private String captchaKey;

    /**
     * 验证码图片（Base64 编码，不含前缀）
     */
    private String captchaImage;

    /**
     * 验证码过期时间（秒）
     */
    private Long expiration;
}
