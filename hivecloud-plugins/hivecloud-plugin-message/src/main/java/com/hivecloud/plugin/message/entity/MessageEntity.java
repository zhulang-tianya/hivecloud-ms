package com.hivecloud.plugin.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 站内消息实体类
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Data
@TableName("t_message")
public class MessageEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private String title;

    private String content;

    private Integer type;

    private Integer status;

    private LocalDateTime readTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
