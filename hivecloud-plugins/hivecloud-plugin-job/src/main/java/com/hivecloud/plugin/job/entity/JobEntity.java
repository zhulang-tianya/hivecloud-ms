package com.hivecloud.plugin.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 定时任务实体类
 * 用于存储定时任务配置和执行记录
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Data
@TableName("t_job")
public class JobEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务主键 ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务组名
     */
    private String jobGroup;

    /**
     * 任务方法名
     */
    private String methodName;

    /**
     * 方法参数
     */
    private String methodParams;

    /**
     * cron 表达式
     */
    private String cronExpression;

    /**
     * 任务状态：0-正常，1-暂停
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
