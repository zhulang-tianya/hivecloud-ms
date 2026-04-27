package com.hivecloud.module.claim.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 理赔申请实体类
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Data
@TableName("t_claim")
public class ClaimEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 理赔申请主键 ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 理赔申请号
     */
    private String claimNo;

    /**
     * 保单号
     */
    private String policyNo;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 理赔类型：medical-医疗，accident-意外，critical-重疾
     */
    private String claimType;

    /**
     * 理赔金额
     */
    private BigDecimal claimAmount;

    /**
     * 理赔状态：0-待审核，1-审核中，2-审核通过，3-审核拒绝，4-已打款
     */
    private Integer claimStatus;

    /**
     * 事故时间
     */
    private LocalDateTime accidentTime;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核人 ID
     */
    private Long auditorId;

    /**
     * 审核意见
     */
    private String auditOpinion;

    /**
     * 打款时间
     */
    private LocalDateTime paymentTime;

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
