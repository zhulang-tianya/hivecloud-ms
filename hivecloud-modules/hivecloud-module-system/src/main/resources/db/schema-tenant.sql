-- ============================================================
-- HiveCloud 多租户系统数据库脚本
-- 设计参考：芋道源码 (yudao-cloud)
-- 多租户模式：共享数据库 + 共享表 + 租户 ID
-- 创建时间：2026-05-09
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 租户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_tenant`;
CREATE TABLE `sys_tenant` (
  `id` BIGINT NOT NULL COMMENT '租户 ID',
  `name` VARCHAR(50) NOT NULL COMMENT '租户名称',
  `tenant_code` VARCHAR(50) NOT NULL COMMENT '租户编码',
  `contact_name` VARCHAR(50) DEFAULT NULL COMMENT '联系人姓名',
  `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `contact_email` VARCHAR(100) DEFAULT NULL COMMENT '联系邮箱',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-禁用 1-正常）',
  `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_code` (`tenant_code`),
  KEY `idx_status` (`status`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

-- ----------------------------
-- 2. 租户套餐表
-- ----------------------------
DROP TABLE IF EXISTS `sys_tenant_package`;
CREATE TABLE `sys_tenant_package` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '套餐 ID',
  `name` VARCHAR(50) NOT NULL COMMENT '套餐名称',
  `code` VARCHAR(50) NOT NULL COMMENT '套餐编码',
  `menu_ids` VARCHAR(2000) DEFAULT NULL COMMENT '菜单 ID 集合（逗号分隔）',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-禁用 1-正常）',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_package_code` (`code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户套餐表';

-- ----------------------------
-- 3. 部门表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id` BIGINT NOT NULL COMMENT '部门 ID',
  `tenant_id` BIGINT NOT NULL COMMENT '租户 ID',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父部门 ID',
  `ancestors` VARCHAR(500) DEFAULT '' COMMENT '祖级列表',
  `dept_name` VARCHAR(50) DEFAULT '' COMMENT '部门名称',
  `order_num` INT DEFAULT 0 COMMENT '显示顺序',
  `leader_user_id` BIGINT DEFAULT NULL COMMENT '负责人 ID',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `email` VARCHAR(255) DEFAULT NULL COMMENT '邮箱',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-禁用 1-正常）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_dept` (`tenant_id`, `dept_name`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ----------------------------
-- 4. 岗位表
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
  `id` BIGINT NOT NULL COMMENT '岗位 ID',
  `tenant_id` BIGINT NOT NULL COMMENT '租户 ID',
  `post_code` VARCHAR(50) NOT NULL COMMENT '岗位编码',
  `post_name` VARCHAR(50) NOT NULL COMMENT '岗位名称',
  `sort` INT DEFAULT 0 COMMENT '显示顺序',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-禁用 1-正常）',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_post` (`tenant_id`, `post_code`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

-- ----------------------------
-- 5. 用户表（增加租户 ID）
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `tenant_id` BIGINT NOT NULL COMMENT '租户 ID',
  `username` VARCHAR(64) NOT NULL COMMENT '用户名',
  `password` VARCHAR(128) NOT NULL COMMENT '密码',
  `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
  `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `avatar` VARCHAR(256) DEFAULT NULL COMMENT '头像',
  `gender` TINYINT DEFAULT 0 COMMENT '性别（0-未知 1-男 2-女）',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-禁用 1-正常）',
  `dept_id` BIGINT DEFAULT NULL COMMENT '部门 ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_username` (`tenant_id`, `username`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 6. 角色表（增加租户 ID 和数据权限）
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` BIGINT NOT NULL COMMENT '角色 ID',
  `tenant_id` BIGINT NOT NULL COMMENT '租户 ID',
  `role_code` VARCHAR(100) NOT NULL COMMENT '角色编码',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `data_scope` TINYINT DEFAULT 1 COMMENT '数据范围（1-全部 2-本部门及以下 3-仅本部门 4-仅本人 5-自定义）',
  `data_scope_dept_ids` VARCHAR(500) DEFAULT NULL COMMENT '自定义部门 ID 集合',
  `sort` INT DEFAULT 0 COMMENT '显示顺序',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-禁用 1-正常）',
  `description` VARCHAR(256) DEFAULT NULL COMMENT '角色描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_role_code` (`tenant_id`, `role_code`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- 7. 菜单表（增加租户 ID 和祖级列表）
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` BIGINT NOT NULL COMMENT '菜单 ID',
  `tenant_id` BIGINT NOT NULL COMMENT '租户 ID',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父菜单 ID',
  `ancestors` VARCHAR(500) DEFAULT '' COMMENT '祖级列表',
  `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
  `menu_code` VARCHAR(100) NOT NULL COMMENT '菜单编码',
  `menu_type` TINYINT DEFAULT 0 COMMENT '类型（0-目录 1-菜单 2-按钮）',
  `path` VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
  `component` VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
  `perms` VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
  `icon` VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
  `sort` INT DEFAULT 0 COMMENT '显示顺序',
  `visible` TINYINT DEFAULT 1 COMMENT '是否可见（0-隐藏 1-显示）',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-禁用 1-正常）',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_menu_code` (`tenant_id`, `menu_code`),
  UNIQUE KEY `uk_tenant_perms` (`tenant_id`, `perms`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- ----------------------------
-- 8. 用户角色关联表（增加租户 ID）
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `tenant_id` BIGINT NOT NULL COMMENT '租户 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `role_id` BIGINT NOT NULL COMMENT '角色 ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ----------------------------
-- 9. 角色菜单关联表（增加租户 ID）
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `tenant_id` BIGINT NOT NULL COMMENT '租户 ID',
  `role_id` BIGINT NOT NULL COMMENT '角色 ID',
  `menu_id` BIGINT NOT NULL COMMENT '菜单 ID',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ----------------------------
-- 10. 用户岗位关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post` (
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `tenant_id` BIGINT NOT NULL COMMENT '租户 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `post_id` BIGINT NOT NULL COMMENT '岗位 ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户岗位关联表';

-- ----------------------------
-- 11. 字典类型表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `id` BIGINT NOT NULL COMMENT '字典类型 ID',
  `tenant_id` BIGINT NOT NULL COMMENT '租户 ID',
  `dict_name` VARCHAR(100) DEFAULT '' COMMENT '字典类型名称',
  `dict_type` VARCHAR(100) DEFAULT '' COMMENT '字典类型标识',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-禁用 1-正常）',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_dict_type` (`tenant_id`, `dict_type`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- ----------------------------
-- 12. 字典数据表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `id` BIGINT NOT NULL COMMENT '字典数据 ID',
  `tenant_id` BIGINT NOT NULL COMMENT '租户 ID',
  `dict_type_id` BIGINT NOT NULL COMMENT '字典类型 ID',
  `dict_label` VARCHAR(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` VARCHAR(100) DEFAULT '' COMMENT '字典键值',
  `sort` INT DEFAULT 0 COMMENT '显示顺序',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-禁用 1-正常）',
  `color_type` VARCHAR(100) DEFAULT '' COMMENT '颜色类型',
  `css_class` VARCHAR(100) DEFAULT '' COMMENT '样式属性',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_dict_type_id` (`dict_type_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- ----------------------------
-- 初始化数据
-- ----------------------------

-- 1. 创建默认租户
INSERT INTO `sys_tenant` (`id`, `name`, `tenant_code`, `status`) VALUES
(1, '默认租户', 'default', 1);

-- 2. 创建默认租户套餐
INSERT INTO `sys_tenant_package` (`id`, `name`, `code`, `menu_ids`, `status`) VALUES
(1, '基础套餐', 'basic', '1,2,3,4,5,6,7,8,9', 1);

-- 3. 创建管理员用户（租户 ID=1）
INSERT INTO `sys_user` (`id`, `tenant_id`, `username`, `password`, `nickname`, `status`) VALUES
(1, 1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', 1);

-- 4. 创建超级管理员角色（租户 ID=1）
INSERT INTO `sys_role` (`id`, `tenant_id`, `role_code`, `role_name`, `data_scope`, `status`) VALUES
(1, 1, 'admin', '超级管理员', 1, 1);

-- 5. 关联用户角色
INSERT INTO `sys_user_role` (`id`, `tenant_id`, `user_id`, `role_id`) VALUES
(1, 1, 1, 1);

-- 6. 创建默认菜单数据
-- 系统管理（目录）
INSERT INTO `sys_menu` (`id`, `tenant_id`, `parent_id`, `ancestors`, `menu_name`, `menu_code`, `menu_type`, `icon`, `sort`, `visible`, `status`) VALUES
(1, 1, 0, '1', '系统管理', 'system', 0, 'system', 1, 1, 1);

-- 用户管理（菜单）
INSERT INTO `sys_menu` (`id`, `tenant_id`, `parent_id`, `ancestors`, `menu_name`, `menu_code`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort`, `visible`, `status`) VALUES
(2, 1, 1, '1,2', '用户管理', 'user', 1, '/system/user', 'system/user/index', 'system:user:query', 'user', 1, 1, 1);

-- 用户查询（按钮）
INSERT INTO `sys_menu` (`id`, `tenant_id`, `parent_id`, `ancestors`, `menu_name`, `menu_code`, `menu_type`, `perms`, `icon`, `sort`, `visible`, `status`) VALUES
(3, 1, 2, '1,2,3', '用户查询', 'user:query', 2, 'system:user:query', '#', 1, 1, 1);

-- 用户新增（按钮）
INSERT INTO `sys_menu` (`id`, `tenant_id`, `parent_id`, `ancestors`, `menu_name`, `menu_code`, `menu_type`, `perms`, `icon`, `sort`, `visible`, `status`) VALUES
(4, 1, 2, '1,2,4', '用户新增', 'user:add', 2, 'system:user:add', '#', 2, 1, 1);

-- 用户修改（按钮）
INSERT INTO `sys_menu` (`id`, `tenant_id`, `parent_id`, `ancestors`, `menu_name`, `menu_code`, `menu_type`, `perms`, `icon`, `sort`, `visible`, `status`) VALUES
(5, 1, 2, '1,2,5', '用户修改', 'user:edit', 2, 'system:user:edit', '#', 3, 1, 1);

-- 用户删除（按钮）
INSERT INTO `sys_menu` (`id`, `tenant_id`, `parent_id`, `ancestors`, `menu_name`, `menu_code`, `menu_type`, `perms`, `icon`, `sort`, `visible`, `status`) VALUES
(6, 1, 2, '1,2,6', '用户删除', 'user:remove', 2, 'system:user:remove', '#', 4, 1, 1);

-- 角色管理（菜单）
INSERT INTO `sys_menu` (`id`, `tenant_id`, `parent_id`, `ancestors`, `menu_name`, `menu_code`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort`, `visible`, `status`) VALUES
(7, 1, 1, '1,7', '角色管理', 'role', 1, '/system/role', 'system/role/index', 'system:role:query', 'peoples', 2, 1, 1);

-- 菜单管理（菜单）
INSERT INTO `sys_menu` (`id`, `tenant_id`, `parent_id`, `ancestors`, `menu_name`, `menu_code`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort`, `visible`, `status`) VALUES
(8, 1, 1, '1,8', '菜单管理', 'menu', 1, '/system/menu', 'system/menu/index', 'system:menu:query', 'tree-table', 3, 1, 1);

-- 部门管理（菜单）
INSERT INTO `sys_menu` (`id`, `tenant_id`, `parent_id`, `ancestors`, `menu_name`, `menu_code`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort`, `visible`, `status`) VALUES
(9, 1, 1, '1,9', '部门管理', 'dept', 1, '/system/dept', 'system/dept/index', 'system:dept:query', 'tree', 4, 1, 1);

-- 岗位管理（菜单）
INSERT INTO `sys_menu` (`id`, `tenant_id`, `parent_id`, `ancestors`, `menu_name`, `menu_code`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort`, `visible`, `status`) VALUES
(10, 1, 1, '1,10', '岗位管理', 'post', 1, '/system/post', 'system/post/index', 'system:post:query', 'post', 5, 1, 1);

-- 租户管理（菜单）
INSERT INTO `sys_menu` (`id`, `tenant_id`, `parent_id`, `ancestors`, `menu_name`, `menu_code`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort`, `visible`, `status`) VALUES
(11, 1, 1, '1,11', '租户管理', 'tenant', 1, '/system/tenant', 'system/tenant/index', 'system:tenant:query', 'dashboard', 6, 1, 1);

SET FOREIGN_KEY_CHECKS = 1;
