package com.hivecloud.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统角色实体类
 * 对应数据库表 sys_role
 * 存储角色基本信息
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see TableName
 */
@Data
@TableName("sys_role")
public class SysRole implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID，使用雪花算法生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 角色状态（0-禁用，1-正常）
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer deleted;
}
