-- 支付模块数据库 Schema
-- HiveCloud 支付服务数据表定义

-- 支付订单表
CREATE TABLE IF NOT EXISTS `t_payment_order` (
    `id` BIGINT NOT NULL COMMENT '主键 ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '商户订单号',
    `transaction_id` VARCHAR(64) DEFAULT NULL COMMENT '支付流水号（第三方支付平台返回）',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    `currency` VARCHAR(10) DEFAULT 'CNY' COMMENT '支付币种：CNY-人民币，USD-美元',
    `payment_method` VARCHAR(20) NOT NULL COMMENT '支付方式：alipay-支付宝，wechat-微信，unionpay-银联',
    `payment_status` TINYINT DEFAULT 0 COMMENT '支付状态：0-待支付，1-支付中，2-支付成功，3-支付失败，4-已关闭，5-已退款',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户 ID',
    `subject` VARCHAR(256) DEFAULT NULL COMMENT '商品标题',
    `body` VARCHAR(512) DEFAULT NULL COMMENT '商品描述',
    `payment_time` DATETIME DEFAULT NULL COMMENT '支付成功时间',
    `close_time` DATETIME DEFAULT NULL COMMENT '订单关闭时间',
    `refund_time` DATETIME DEFAULT NULL COMMENT '退款时间',
    `refund_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '退款金额',
    `refund_reason` VARCHAR(512) DEFAULT NULL COMMENT '退款原因',
    `notify_url` VARCHAR(512) DEFAULT NULL COMMENT '异步通知地址',
    `return_url` VARCHAR(512) DEFAULT NULL COMMENT '同步跳转地址',
    `extra_params` TEXT DEFAULT NULL COMMENT '扩展参数（JSON 格式）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_payment_status` (`payment_status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付订单表';

-- 支付退款表
CREATE TABLE IF NOT EXISTS `t_payment_refund` (
    `id` BIGINT NOT NULL COMMENT '主键 ID',
    `refund_no` VARCHAR(64) NOT NULL COMMENT '退款单号',
    `order_no` VARCHAR(64) NOT NULL COMMENT '商户订单号',
    `transaction_id` VARCHAR(64) DEFAULT NULL COMMENT '支付流水号',
    `refund_transaction_id` VARCHAR(64) DEFAULT NULL COMMENT '退款流水号（第三方支付平台返回）',
    `refund_amount` DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    `refund_status` TINYINT DEFAULT 0 COMMENT '退款状态：0-待退款，1-退款中，2-退款成功，3-退款失败',
    `refund_reason` VARCHAR(512) DEFAULT NULL COMMENT '退款原因',
    `refund_time` DATETIME DEFAULT NULL COMMENT '退款时间',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户 ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_refund_no` (`refund_no`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付退款表';
