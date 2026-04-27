# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- 支付服务模块，支持支付宝、微信支付、银联支付
- 理赔服务模块，完整的理赔流程管理
- 活动服务模块，优惠券和营销活动管理
- 通知服务模块，短信、邮件、推送通知
- AI 智能客服插件，智能问答和风险评估
- 数据加密插件，AES、MD5、SHA256 加密
- 站内消息插件，消息管理和已读未读状态

### Changed
- 优化服务注册与发现机制，使用 Redis+Gossip 协议
- 改进分布式心跳探测和故障自动剔除
- 完善代码注释，所有核心类添加 Javadoc

### Fixed
- 修复服务注册中的并发问题
- 优化心跳检测的准确性
- 改进日志记录和异常处理

## [1.0.0] - 2026-04-27

### Added
- 初始版本发布
- 五层架构体系：流量接入层、服务治理层、业务核心层、数据管理层、基础层
- 服务注册与发现（Redis+Gossip）
- 分布式心跳探测
- 故障自动剔除
- 集群同步协议
- 统一认证授权
- 支付服务（支付宝/微信/银联）
- 理赔服务
- 活动服务
- 通知服务
- AI 智能客服
- 数据加密
- 站内消息
- 监控告警集成（Prometheus+Grafana）
- Docker 和 K8s 部署脚本
- 完整的文档体系

### Changed
- 从单体架构迁移到微服务架构
- 采用 DDD 领域驱动设计
- 实现服务边界清晰划分
- 优化数据库连接池配置
- 改进缓存策略

### Fixed
- 所有核心功能单元测试
- 代码规范符合阿里巴巴 Java 开发手册
- 完善异常处理机制
- 统一日志规范

### Security
- 实现 JWT 认证授权
- 数据加密存储
- 接口幂等性保障
- 分布式事务支持
