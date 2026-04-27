package com.hivecloud.plugin.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统操作日志实体类
 * 对应数据库表 sys_oper_log
 * 记录用户操作日志信息
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see TableName
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog implements Serializable {

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
     * 操作标题
     */
    private String title;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 方法名称
     */
    private String method;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 操作人类别
     */
    private String operatorType;

    /**
     * 操作人员姓名
     */
    private String operName;

    /**
     * 请求 URL
     */
    private String operUrl;

    /**
     * 操作 IP
     */
    private String operIp;

    /**
     * 请求参数
     */
    private String operParam;

    /**
     * 返回结果
     */
    private String jsonResult;

    /**
     * 操作状态（0 正常 1 异常）
     */
    private Integer status;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 操作时间
     */
    private Long operTime;

    /**
     * 消耗时间
     */
    private Long costTime;
}