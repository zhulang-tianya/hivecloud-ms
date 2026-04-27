package com.hivecloud.system.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户视图对象（VO）
 * 用于封装用户信息返回给前端
 * 包含用户基本信息、角色和权限列表
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 */
@Data
public class UserVO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 用户主键 ID
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
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像 URL
     */
    private String avatar;

    /**
     * 性别（0-未知，1-男，2-女）
     */
    private Integer gender;

    /**
     * 状态（0-禁用，1-正常）
     */
    private Integer status;

    /**
     * 角色标识列表
     */
    private List<String> roles;

    /**
     * 权限标识列表
     */
    private List<String> permissions;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
