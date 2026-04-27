package com.hivecloud.system.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录视图对象（VO）
 * 用于封装用户登录请求参数
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 */
@Data
public class LoginVO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
