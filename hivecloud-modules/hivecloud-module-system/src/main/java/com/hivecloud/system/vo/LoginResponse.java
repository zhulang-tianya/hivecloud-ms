package com.hivecloud.system.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应 VO
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Data
@Builder
public class LoginResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * JWT Token
     */
    private String token;

    /**
     * Token 有效期（秒）
     */
    private Long expiresIn;

    /**
     * 用户信息
     */
    private UserInfoVO userInfo;

    /**
     * 用户信息 VO
     */
    @Data
    @Builder
    public static class UserInfoVO implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * 用户 ID
         */
        private Long id;
        
        /**
         * 用户名
         */
        private String username;
        
        /**
         * 昵称
         */
        private String nickname;
        
        /**
         * 头像 URL
         */
        private String avatar;
        
        /**
         * 角色列表
         */
        private java.util.List<String> roles;
        
        /**
         * 权限列表
         */
        private java.util.List<String> permissions;
    }
}
