-- 理赔模块数据库 Schema
-- HiveCloud 理赔服务数据表定义

-- 理赔申请表
CREATE TABLE IF NOT EXISTS `t_claim` (
    `id` BIGINT NOT NULL COMMENT '主键 ID',
    `claim_no` VARCHAR(64) NOT NULL COMMENT '理赔申请号',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID',
    `claim_type` VARCHAR(50) NOT NULL COMMENT '理赔类型',
    `claim_amount` DECIMAL(10,2) NOT NULL COMMENT '理赔金额',
    `claim_status` TINYINT DEFAULT 0 COMMENT '理赔状态：0-待审核，1-审核中，2-审核通过，3-审核拒绝，4-已打款',
    `apply_time` DATETIME DEFAULT NULL COMMENT '申请时间',
    `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    `auditor_id` BIGINT DEFAULT NULL COMMENT '审核人 ID',
    `audit_opinion` VARCHAR(512) DEFAULT NULL COMMENT '审核意见',
    `payment_time` DATETIME DEFAULT NULL COMMENT '打款时间',
    `remark` VARCHAR(1024) DEFAULT NULL COMMENT '备注',
    `attachment_urls` TEXT DEFAULT NULL COMMENT '附件 URL 列表（JSON 数组）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_claim_no` (`claim_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_claim_status` (`claim_status`),
    KEY `idx_apply_time` (`apply_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='理赔申请表';
