# HiveCloud 微服务架构

> **版本**: 1.0.0  
> **描述**: 基于 Spring Cloud 的企业级微服务架构  
> **许可证**: Apache 2.0  
> **状态**: 生产就绪 ✅

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![JDK](https://img.shields.io/badge/JDK-17+-green.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()

## 📖 简介

HiveCloud 是一个基于 Spring Cloud 构建的企业级微服务架构，采用 DDD（领域驱动设计）理念，提供完整的微服务解决方案。架构包含服务注册与发现、分布式心跳探测、故障自动剔除、集群同步等核心功能，并集成了支付、理赔、活动、通知等多个业务模块。

### 核心特性

- 🏗️ **五层架构体系**: 流量接入层、服务治理层、业务核心层、数据管理层、基础层
- 🔍 **服务治理**: 基于 Redis+Gossip 的服务注册与发现
- 💓 **健康监控**: 分布式心跳探测和故障自动剔除
- 🔐 **安全认证**: JWT 统一认证授权
- 💳 **支付集成**: 支持支付宝、微信支付、银联支付
- 🏥 **理赔服务**: 完整的保险理赔流程管理
- 🎁 **营销活动**: 优惠券、活动管理
- 📱 **通知服务**: 短信、邮件、推送通知
- 🤖 **AI 客服**: 智能问答、风险评估
- 🔒 **数据安全**: AES、MD5、SHA256 加密
- 💌 **站内消息**: 消息管理和已读未读状态
- 📊 **监控告警**: Prometheus+Grafana 监控体系

## 🚀 快速开始

### 环境要求
- **JDK**: 17+
- **Maven**: 3.8+
- **MySQL**: 8.0+
- **Redis**: 6.0+

### 快速启动
```bash
# 克隆项目
git clone https://github.com/zhulang-tianya/hivecloud-ms.git
cd hivecloud-ms

# 编译项目
mvn clean package -DskipTests

# 启动服务
java -jar hivecloud-framework/hivecloud-service-registry/target/*.jar
```

### Docker 部署
```bash
cd deploy
docker-compose up -d
```

详细部署指南请参考 [快速开始文档](docs/guides/QUICKSTART.md)

## 📦 项目结构

```
hivecloud-ms
├── hivecloud-common/        # 公共模块
│   ├── hivecloud-common-core/      # 核心工具类
│   ├── hivecloud-common-mybatis/   # MyBatis 集成
│   ├── hivecloud-common-redis/     # Redis 集成
│   └── hivecloud-common-web/       # Web 集成
├── hivecloud-framework/     # 框架模块
│   ├── hivecloud-service-registry/ # 服务注册与发现
│   ├── hivecloud-heartbeat/        # 心跳探测
│   ├── hivecloud-gossip-sync/      # Gossip 同步
│   └── hivecloud-fault-removal/    # 故障剔除
├── hivecloud-plugins/       # 插件模块
│   ├── hivecloud-plugin-cache/     # 缓存插件
│   ├── hivecloud-plugin-auth/      # 认证插件
│   ├── hivecloud-plugin-encrypt/   # 加密插件
│   └── hivecloud-plugin-ai/        # AI 插件
├── hivecloud-modules/       # 业务模块
│   ├── hivecloud-module-system/    # 系统管理
│   ├── hivecloud-module-payment/   # 支付服务
│   ├── hivecloud-module-claim/     # 理赔服务
│   └── hivecloud-module-activity/  # 活动服务
├── hivecloud-gateway/       # 网关模块
├── deploy/                  # 部署脚本
│   ├── Dockerfile
│   ├── docker-compose.yml
│   ├── prometheus.yml
│   └── alerts.yml
└── docs/                    # 文档
    ├── architecture/        # 架构设计
    ├── optimization/        # 优化文档
    ├── guides/            # 使用指南
    └── progress/          # 进度文档
```

## 🎯 核心功能详解

### 服务治理

#### 服务注册与发现
基于 Redis 和 Gossip 协议的混合服务注册与发现机制：
- 使用 Redis 作为中心化存储
- Gossip 协议实现节点间数据同步
- 支持服务动态扩缩容

```java
@Resource
private ServiceRegistry serviceRegistry;

// 注册服务
serviceRegistry.register(instance);

// 发现服务
List<ServiceInstance> instances = serviceRegistry.discover("service-name");
```

#### 分布式心跳探测
- 可配置的心跳间隔
- 多节点并发探测
- 实时健康状态更新

#### 故障自动剔除
- 连续失败自动剔除
- 超时自动剔除
- 健康检查自动剔除

### 业务模块

#### 支付服务
支持多种支付方式的统一支付解决方案：
- 支付宝支付
- 微信支付
- 银联支付
- 统一接口设计
- 异步通知处理

#### 理赔服务
完整的保险理赔流程管理：
- 理赔申请创建
- 审核流程
- 理赔打款
- 状态跟踪

#### 活动服务
营销活动管理平台：
- 优惠券管理
- 活动创建
- 用户领取
- 使用统计

#### 通知服务
多渠道通知服务：
- 短信通知
- 邮件通知
- 推送通知
- 模板管理

### 插件系统

#### AI 智能客服
- 智能问答
- 风险评估
- 自动回复

#### 数据加密
- AES 加密/解密
- MD5 加密
- SHA256 加密
- 可插拔设计

#### 缓存插件
- 两级缓存（Caffeine + Redis）
- 缓存穿透/雪崩防护
- 自动过期

## 📊 技术栈

### 后端框架
- **Spring Boot**: 3.x
- **Spring Cloud**: 最新版
- **MyBatis Plus**: 3.5.x
- **HikariCP**: 数据库连接池
- **Lettuce**: Redis 客户端

### 数据库
- **MySQL**: 8.0+ (关系型数据库)
- **Redis**: 6.0+ (缓存和注册中心)

### 监控与日志
- **Prometheus**: 指标收集
- **Grafana**: 可视化仪表盘
- **SLF4J + Logback**: 日志框架

### 部署与运维
- **Docker**: 容器化
- **Kubernetes**: 容器编排
- **Docker Compose**: 本地开发环境

### 开发工具
- **Lombok**: 简化代码
- **MapStruct**: 对象映射
- **Swagger/OpenAPI**: API 文档
- **JUnit 5 + Mockito**: 单元测试

## 📖 文档

### 入门指南
- [快速开始](docs/guides/QUICKSTART.md) - 30 分钟快速上手
- [部署文档](docs/guides/DEPLOYMENT.md) - 生产环境部署
- [API 文档](docs/guides/API.md) - 完整 API 参考

### 架构设计
- [架构设计](docs/architecture/README.md) - 整体架构说明
- [服务治理](docs/architecture/SERVICE-GOVERNANCE.md) - 服务治理详解
- [数据设计](docs/architecture/DATA-DESIGN.md) - 数据库设计

### 开发指南
- [编码规范](docs/guides/CODING-STANDARDS.md) - 代码规范
- [事务管理](docs/guides/TRANSACTION-MANAGEMENT.md) - 事务处理
- [幂等性设计](docs/guides/IDEMPOTENT-COMPONENT.md) - 幂等性保障

### 优化报告
- [T1 优化总结](docs/optimization/T1-OPT-EXEC-SUMMARY.md)
- [T2 架构优化](docs/optimization/T2-FINAL-001.md)
- [T3 业务扩展](docs/optimization/T3-FINAL-001.md)

## 🤝 贡献

我们欢迎各种形式的贡献：

### 如何贡献
1. Fork 本项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

### 行为准则
本项目遵循 [贡献者公约](CODE_OF_CONDUCT.md)，请确保您的行为符合规范。

### 贡献指南
详细贡献流程请参考 [CONTRIBUTING.md](CONTRIBUTING.md)

## 📄 许可证

本项目采用 Apache License 2.0 许可证。详见 [LICENSE](LICENSE) 文件。

## 📝 变更日志

所有重要变更都将记录在 [CHANGELOG.md](CHANGELOG.md) 中。

## 👥 团队

- **作者**: HiveCloud Team
- **维护者**: HiveCloud Team
- **贡献者**: 感谢所有贡献者！

## 🙏 致谢

感谢以下开源项目：
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Cloud](https://spring.io/projects/spring-cloud)
- [MyBatis Plus](https://baomidou.com/)
- [Redis](https://redis.io/)
- [MySQL](https://www.mysql.com/)

## 📬 联系方式

- **项目地址**: https://github.com/zhulang-tianya/hivecloud-ms
- **问题反馈**: https://github.com/zhulang-tianya/hivecloud-ms/issues
- **讨论区**: https://github.com/zhulang-tianya/hivecloud-ms/discussions

## 📊 项目状态

| 阶段 | 状态 | 完成度 |
|------|------|--------|
| T1 - 代码优化 | ✅ 已完成 | 100% |
| T2 - 架构优化 | ✅ 已完成 | 100% |
| T3 - 业务扩展 | ✅ 已完成 | 100% |
| T4 - 生产就绪 | ✅ 已完成 | 100% |

---

**Made with ❤️ by HiveCloud Team**
