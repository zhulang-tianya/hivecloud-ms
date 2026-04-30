# HiveCloud 微服务部署说明

## 📋 目录

1. [模块概览](#模块概览)
2. [必选部署服务](#必选部署服务)
3. [可选部署服务](#可选部署服务)
4. [部署模式](#部署模式)
5. [启动命令](#启动命令)

---

## 🏗️ 模块概览

### 项目结构

```
hivecloud-ms (父工程)
│
├── hivecloud-gateway              # 业务网关 (P0 必选)
│
├── hivecloud-framework            # 核心框架层
│   ├── hivecloud-starter-nacos    # Nacos 统一启动器
│   ├── hivecloud-plugin-engine    # 插件引擎
│   ├── hivecloud-heartbeat        # 心跳检测
│   ├── hivecloud-fault-removal    # 故障摘除
│   ├── hivecloud-gossip-sync      # Gossip 同步
│   └── hivecloud-event-bus        # 事件总线
│
├── hivecloud-common               # 公共工具类层
│   ├── hivecloud-common-core      # 核心工具类
│   ├── hivecloud-common-web       # Web 通用
│   ├── hivecloud-common-security  # 安全认证
│   ├── hivecloud-common-redis     # Redis 工具
│   ├── hivecloud-common-mybatis   # MyBatis 配置
│   └── hivecloud-common-test      # 测试工具
│
├── hivecloud-modules              # 业务模块层
│   ├── hivecloud-module-system    # 系统管理 (P0 必选)
│   ├── hivecloud-module-payment   # 支付服务 (P1 重要)
│   ├── hivecloud-module-claim     # 理赔服务 (P1 重要)
│   ├── hivecloud-module-activity  # 活动服务 (P2 可选)
│   └── hivecloud-module-notify    # 通知服务 (P2 可选)
│
└── hivecloud-plugins              # 插件模块层
    ├── hivecloud-plugin-auth      # 认证插件
    ├── hivecloud-plugin-log       # 日志插件
    ├── hivecloud-plugin-cache     # 缓存插件
    ├── hivecloud-plugin-crypto    # 加密插件
    ├── hivecloud-plugin-ai        # AI 插件
    └── ...
```

---

## ✅ 必选部署服务 (P0)

### 1. hivecloud-gateway - 业务网关

**职责**: 统一业务入口，所有请求的流量网关

**核心功能**:
- ✅ 路由转发：将请求路由到对应的微服务
- ✅ JWT 认证：统一身份验证
- ✅ 限流降级：保护后端服务
- ✅ 日志记录：访问日志、操作日志
- ✅ 跨域处理：CORS 配置

**技术栈**:
- Spring Cloud Gateway (响应式网关)
- Redis (限流、Session)
- JWT (身份认证)

**端口**: `8080`

**依赖**:
- hivecloud-starter-nacos (Nacos 配置)
- hivecloud-plugin-log (日志插件)

**启动命令**:
```bash
# 开发环境
mvn spring-boot:run -pl hivecloud-gateway -Pdev

# 测试环境
mvn spring-boot:run -pl hivecloud-gateway -Ptest

# 生产环境
mvn spring-boot:run -pl hivecloud-gateway -Pprod
```

**验证方式**:
```bash
# 访问健康检查端点
curl http://localhost:8080/actuator/health

# 访问 API 文档
http://localhost:8080/swagger-ui.html
```

---

### 2. hivecloud-module-system - 系统管理服务

**职责**: 提供系统基础管理功能

**核心功能**:
- ✅ 用户管理：用户 CRUD、登录注册
- ✅ 角色管理：角色定义、权限分配
- ✅ 菜单管理：系统菜单配置
- ✅ 权限控制：RBAC 权限模型

**技术栈**:
- Spring Boot 3.2.4
- MyBatis Plus 3.5.5
- MySQL 8.0 (主数据库)
- Redis (缓存、Session)

**端口**: `8081`

**依赖**:
- hivecloud-starter-nacos
- MySQL
- Redis

**数据库**: `hivecloud_system`

**启动命令**:
```bash
# 开发环境
mvn spring-boot:run -pl hivecloud-modules/hivecloud-module-system -Pdev

# 测试环境
mvn spring-boot:run -pl hivecloud-modules/hivecloud-module-system -Ptest

# 生产环境
mvn spring-boot:run -pl hivecloud-modules/hivecloud-module-system -Pprod
```

**验证方式**:
```bash
# 访问健康检查端点
curl http://localhost:8081/actuator/health

# 访问 API 文档
http://localhost:8081/swagger-ui.html
```

---

## 📦 可选部署服务

### P1 优先级 - 重要服务

#### 1. hivecloud-module-payment - 支付服务

**职责**: 处理所有支付相关业务

**核心功能**:
- 微信支付集成
- 支付宝集成
- 银联支付集成
- 支付订单管理
- 支付回调处理

**端口**: `8082`

**数据库**: `hivecloud_payment`

**外部依赖**:
- 微信支付 API
- 支付宝 API
- 银联 API

**启动命令**:
```bash
mvn spring-boot:run -pl hivecloud-modules/hivecloud-module-payment -Ptest
```

**部署场景**: 需要支付功能的业务场景

---

#### 2. hivecloud-module-claim - 理赔服务

**职责**: 处理保险理赔业务

**核心功能**:
- 理赔申请
- 理赔审核
- 理赔赔付
- 理赔记录管理

**端口**: `8083`

**数据库**: `hivecloud_claim`

**启动命令**:
```bash
mvn spring-boot:run -pl hivecloud-modules/hivecloud-module-claim -Ptest
```

**部署场景**: 保险业务相关场景

---

### P2 优先级 - 可选服务

#### 1. hivecloud-module-activity - 活动服务

**职责**: 营销活动和优惠券管理

**核心功能**:
- 优惠券管理
- 活动配置
- 活动参与
- 活动统计

**端口**: `8084`

**数据库**: `hivecloud_activity`

**启动命令**:
```bash
mvn spring-boot:run -pl hivecloud-modules/hivecloud-module-activity -Ptest
```

**部署场景**: 需要营销活动的业务场景

---

#### 2. hivecloud-module-notify - 通知服务

**职责**: 统一消息通知

**核心功能**:
- 短信发送
- 消息推送
- 通知模板管理
- 发送记录管理

**端口**: `8085`

**外部依赖**:
- 短信服务商 API (阿里云/腾讯云)

**启动命令**:
```bash
mvn spring-boot:run -pl hivecloud-modules/hivecloud-module-notify -Ptest
```

**部署场景**: 需要发送通知的业务场景

---

## 🚀 部署模式

### 开发环境 (Dev)

**目标**: 快速开发和本地调试

**部署服务**:
- ✅ hivecloud-gateway (8080)
- ✅ hivecloud-module-system (8081)

**配置**:
```bash
# 使用 dev Profile
mvn spring-boot:run -Pdev
```

**Nacos Namespace**: `dev`

**数据库**: 本地 MySQL

**Redis**: 本地 Redis

---

### 测试环境 (Test)

**目标**: 集成测试和功能验证

**部署服务**:
- ✅ hivecloud-gateway (8080)
- ✅ hivecloud-module-system (8081)
- ✅ hivecloud-module-payment (8082) - 可选
- ✅ hivecloud-module-claim (8083) - 可选
- ✅ hivecloud-module-activity (8084) - 可选
- ✅ hivecloud-module-notify (8085) - 可选

**配置**:
```bash
# 使用 test Profile
mvn spring-boot:run -Ptest
```

**Nacos Namespace**: `test`

**数据库**: 测试环境 MySQL

**Redis**: 测试环境 Redis

---

### 生产环境 (Prod)

**目标**: 正式业务运行

**部署服务**: 根据业务需求选择

**最小化部署** (基础业务):
- ✅ hivecloud-gateway
- ✅ hivecloud-module-system

**标准部署** (完整业务):
- ✅ hivecloud-gateway
- ✅ hivecloud-module-system
- ✅ hivecloud-module-payment
- ✅ hivecloud-module-claim
- ✅ hivecloud-module-activity
- ✅ hivecloud-module-notify

**配置**:
```bash
# 使用 prod Profile
mvn spring-boot:run -Pprod
```

**Nacos Namespace**: `prod`

**数据库**: 生产环境 MySQL (主从架构)

**Redis**: 生产环境 Redis (集群模式)

---

## 📝 启动命令

### 单个服务启动

```bash
# 启动 Gateway
cd hivecloud-gateway
mvn spring-boot:run

# 启动 System 模块
cd hivecloud-modules/hivecloud-module-system
mvn spring-boot:run

# 启动 Payment 模块
cd hivecloud-modules/hivecloud-module-payment
mvn spring-boot:run
```

### 多服务同时启动 (多个终端)

**终端 1 - Gateway**:
```bash
cd hivecloud-gateway
mvn spring-boot:run
```

**终端 2 - System**:
```bash
cd hivecloud-modules/hivecloud-module-system
mvn spring-boot:run
```

**终端 3 - Payment** (可选):
```bash
cd hivecloud-modules/hivecloud-module-payment
mvn spring-boot:run
```

### Docker 部署

```bash
# 构建镜像
mvn clean package -DskipTests
docker build -t hivecloud-gateway:latest ./hivecloud-gateway

# 启动容器
docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  --name hivecloud-gateway \
  hivecloud-gateway:latest
```

### Docker Compose 部署

```bash
cd deploy
docker-compose up -d
```

**docker-compose.yml** 包含:
- Gateway 服务
- System 服务
- MySQL
- Redis
- Nacos

---

## 🔍 服务验证

### 健康检查

```bash
# Gateway 健康检查
curl http://localhost:8080/actuator/health

# System 健康检查
curl http://localhost:8081/actuator/health

# Payment 健康检查
curl http://localhost:8082/actuator/health
```

### 服务注册验证

访问 Nacos 控制台：`http://172.10.6.194:8848/nacos`

1. 选择对应的 Namespace (dev/test/prod)
2. 查看 **服务管理 > 服务列表**
3. 确认服务状态为 **健康**

### API 文档验证

```bash
# Gateway API 文档
http://localhost:8080/swagger-ui.html

# System API 文档
http://localhost:8081/swagger-ui.html

# Payment API 文档
http://localhost:8082/swagger-ui.html
```

---

## 📊 部署检查清单

### 启动前检查

- [ ] Nacos 服务已启动且可访问
- [ ] MySQL 数据库已创建
- [ ] Redis 服务已启动
- [ ] 数据库 schema 已初始化
- [ ] Nacos 配置已创建 (对应 Namespace)
- [ ] 环境变量已配置 (生产环境)

### 启动后检查

- [ ] 服务成功注册到 Nacos
- [ ] 健康检查端点返回正常
- [ ] API 文档可正常访问
- [ ] 日志无 ERROR 级别错误
- [ ] 数据库连接正常
- [ ] Redis 连接正常

---

## 🎯 部署建议

### 开发环境

**建议**: 只部署 Gateway + System 模块

**原因**:
- 快速启动，减少资源占用
- 满足日常开发需求
- 便于调试和问题排查

### 测试环境

**建议**: 部署所有 P0 + P1 服务

**原因**:
- 完整的功能测试
- 服务间调用验证
- 集成测试需要

### 生产环境

**建议**: 根据业务需求按需部署

**原则**:
- 最小化部署，减少运维成本
- 核心业务优先保障
- 按需扩展，逐步完善

---

## 📚 相关文档

- [模块关系图](file://d:\work\workcode\trae_v_2\hivecloud-ms\docs\architecture\hivecloud-module-architecture.html)
- [服务调用关系图](file://d:\work\workcode\trae_v_2\hivecloud-ms\docs\architecture\hivecloud-service-calls.html)
- [Nacos 配置管理方案](file://d:\work\workcode\trae_v_2\hivecloud-ms\docs\guides\NACOS-CONFIG-MANAGEMENT.md)
- [Nacos 多环境配置](file://d:\work\workcode\trae_v_2\hivecloud-ms\docs\guides\NACOS-MULTI-ENV.md)
- [快速开始指南](file://d:\work\workcode\trae_v_2\hivecloud-ms\docs\guides\QUICKSTART.md)

---

**更新时间**: 2026-04-30  
**版本**: v1.0.0
