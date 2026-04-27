# HiveCloud 轻量微服务脚手架 - 项目实现分析报告

> **文档版本**: v1.0.0  
> **生成日期**: 2026-04-24  
> **分析范围**: 系统架构设计文档 + 三张架构设计图  
> **目标**: 制定完整的开发实施方案，确保项目可落地、可交付

---

## 目录

1. [架构解读](#1-架构解读)
2. [技术选型](#2-技术选型)
3. [关键功能模块识别](#3-关键功能模块识别)
4. [数据流路径与接口定义](#4-数据流路径与接口定义)
5. [系统边界与依赖关系](#5-系统边界与依赖关系)
6. [分阶段开发计划](#6-分阶段开发计划)
7. [测试策略](#7-测试策略)
8. [质量标准与验收准则](#8-质量标准与验收准则)
9. [风险评估](#9-风险评估)
10. [交付时间表](#10-交付时间表)

---

## 1. 架构解读

### 1.1 核心定位

HiveCloud 是一款基于 Java 生态开发的下一代轻量微服务脚手架，核心定位为：
- **轻量落地**：摒弃 K8s、Istio、Etcd 等重型中间件
- **轻量中心化**：基于 Redis 存储服务元数据，结合 Gossip 实现分布式故障检测
- **自愈自治**：内置心跳检测、故障剔除、熔断降级、缓存自愈
- **灵活扩展**：模块化单体与轻量微服务自由切换

### 1.2 五层架构体系

```
┌─────────────────────────────────────────────────────────────┐
│ L1: 双模流量接入层 (Traffic Access Layer)                    │
│ 统一业务网关 + 域内直连通道 + 流量分发引擎                    │
├─────────────────────────────────────────────────────────────┤
│ L2: 轻量中心化服务治理层 (Lightweight Centralized Governance)    │
│ 元数据注册 + 心跳探测 + 故障剔除 + 集群同步 (Gossip协议)      │
├─────────────────────────────────────────────────────────────┤
│ L3: 业务内核运行层 (Business Core Runtime Layer)             │
│ 模块化单体 + 独立微服务 + 插件引擎 + 双轨链路 + 事件总线      │
├─────────────────────────────────────────────────────────────┤
│ L4: 数据自治管理层 (Data Management Layer)                   │
│ Caffeine本地缓存 + Redis分布式缓存 + MySQL持久化 + 数据自愈   │
├─────────────────────────────────────────────────────────────┤
│ L5: 基础底座层 (Foundation Layer)                            │
│ Nacos配置中心 + 日志系统 + 监控告警 + 安全认证 + JDK/SpringBoot│
└─────────────────────────────────────────────────────────────┘
```

### 1.3 架构设计图解读

#### 1.3.1 五层架构图 ([hivecloud-architecture.html](file:///d:/work/workcode/trae_v_2/architecture-diagrams/hivecloud-architecture.html))

- **视觉设计**：深色主题 + 语义化颜色编码（青绿/翡翠/紫罗兰/琥珀/玫瑰）
- **层次关系**：清晰展示五层架构的模块划分和功能职责
- **关键亮点**：
  - L2 治理层嵌入每个服务节点，无独立服务
  - L3 业务层支持模块化单体与独立微服务混合部署
  - L4 数据层采用本地优先原则，降低对第三方组件依赖

#### 1.3.2 服务调用与数据流图 ([hivecloud-service-flow.html](file:///d:/work/workcode/trae_v_2/architecture-diagrams/hivecloud-service-flow.html))

- **数据流向**：客户端 → 网关 → 服务治理 → 业务服务 → 数据存储
- **调用方式**：
  - HTTP 直连调用（内网高频场景）
  - 事件总线异步调用（解耦场景）
  - 网关路由调用（跨域场景）
- **双轨链路**：
  - 快轨（只读）：@FastTrack，响应 5-15ms
  - 稳轨（写事务）：@StableTrack，响应 50-200ms

#### 1.3.3 部署架构图 ([hivecloud-deployment.html](file:///d:/work/workcode/trae_v_2/architecture-diagrams/hivecloud-deployment.html))

- **部署方式**：纯 Jar 包进程部署，无需 Docker/K8s
- **集群结构**：
  - 网关集群：支持水平扩展，Redis 共享限流计数
  - 模块化单体集群：system/file/job 统一部署
  - 独立微服务集群：pay/claim/activity/notice 独立部署
- **中间件**：Redis (元数据存储/心跳/限流) + MySQL 集群 + Nacos 配置中心 + 消息队列（可选）

### 1.4 核心解决痛点

| 痛点 | 传统方案 | HiveCloud 方案 |
|------|---------|---------------|
| 中心化单点风险 | Nacos/Eureka 宕机全集群失效 | 轻量中心化治理，Redis+本地缓存兜底 |
| 幽灵节点残留 | 服务下线后调用报错 | 三重剔除机制，100ms~5s 清除 |
| 网关性能瓶颈 | 所有流量挤压单一网关 | 网关+内网直连双模式 |
| 中间件过重 | K8s/Istio/Etcd/Sentinel | 轻量中间件，运维成本降低 60%+ |
| 服务耦合严重 | 扩展需改动整体架构 | 模块化+插件化，解耦扩展 |
| 开发效率低 | 框架臃肿，治理代码耦合 | 核心极简，开发效率提升 50%+ |

---

## 2. 技术选型

### 2.1 核心技术栈

| 层级 | 技术 | 版本 | 用途 | 可替换方案 |
|------|------|------|------|-----------|
| 基础框架 | SpringBoot | 3.x | 应用框架 | 无 |
| 微服务 | SpringCloud Alibaba | 精简版 | 微服务基础组件 | SpringCloud Netflix |
| JDK | OpenJDK | 17/21 | 运行时环境 | 无 |
| 数据库 | MySQL | 5.7/8.0 | 关系型数据库 | PostgreSQL |
| 缓存 | Redis | 6.x/7.x | 分布式缓存/元数据存储 | Memcached |
| 本地缓存 | Caffeine | 最新 | 本地缓存 | Guava Cache |
| 配置中心 | Nacos | 最新 | 动态配置管理 | Apollo |
| HTTP客户端 | OkHttp | 最新 | 服务间调用 | Apache HttpClient |
| 集群同步 | Apache Gossip | 纯Java实现 | 轻量中心化集群同步 | 自研实现 |
| 网关 | SpringCloud Gateway | 精简版 | 统一业务网关 | Kong |
| 安全 | Spring Security + JWT | 最新 | 认证鉴权 | Sa-Token |
| 消息队列 | RocketMQ/RabbitMQ | 可选 | 异步消息 | Kafka |

### 2.2 插件技术栈

| 插件 | 技术 | 说明 |
|------|------|------|
| 权限插件 | RBAC + 动态数据权限 | 菜单/按钮/数据权限控制 |
| 日志插件 | 操作日志 + 异常日志 + 访问日志 | 支持 MySQL/文件持久化 |
| 字典插件 | 全局字典管理 + 缓存 | 支持动态更新 |
| 缓存插件 | Caffeine + Redis 适配 | 统一缓存操作接口 |
| AI插件 | 第三方AI接口集成 | 文本识别/语音转换等 |
| 加密插件 | 自定义加密算法 | 敏感信息加密存储 |
| 消息插件 | RocketMQ/RabbitMQ 适配 | 统一消息发送/接收 |

### 2.3 开发工具链

| 工具 | 用途 |
|------|------|
| Maven/Gradle | 项目构建 |
| Git | 版本控制 |
| IntelliJ IDEA | 开发IDE |
| Postman/Apifox | API测试 |
| JMeter | 性能测试 |
| ELK | 日志收集 |
| Prometheus + Grafana | 指标监控 |
| SkyWalking | 链路追踪 |

---

## 3. 关键功能模块识别

### 3.1 模块清单与优先级

#### P0 - 核心必选模块（第一阶段）

| 模块名称 | 标识 | 部署方式 | 功能说明 | 依赖关系 |
|---------|------|---------|---------|---------|
| 系统管理模块 | hivecloud-module-system | 模块化单体 | 用户、角色、权限、菜单、字典、配置管理 | 无 |
| 统一业务网关 | hivecloud-gateway | 独立部署 | 鉴权、限流、路由转发、风控、跨域处理 | 依赖 system |
| 轻量元数据注册 | hivecloud-service-registry | 嵌入服务节点 | Redis Hash 存储元数据，元数据TTL=30s | 依赖 Redis |
| 分布式心跳探测 | hivecloud-heartbeat | 嵌入服务节点 | 1s心跳间隔，3s不健康，4s剔除 | 依赖 Redis |
| 故障剔除机制 | hivecloud-fault-removal | 嵌入服务节点 | 主动下线+被动剔除+调用兜底 | 依赖 heartbeat |
| 轻量集群同步 | hivecloud-gossip-sync | 嵌入服务节点 | Gossip协议，3-5邻居，同步延迟3-10s | 无 |
| 权限插件 | hivecloud-plugin-auth | 插件 | RBAC权限模型 + 动态数据权限 | 依赖 system |
| 日志插件 | hivecloud-plugin-log | 插件 | 操作/异常/访问日志记录 | 无 |
| 缓存插件 | hivecloud-plugin-cache | 插件 | Caffeine + Redis 统一缓存接口 | 依赖 Caffeine/Redis |
| 字典插件 | hivecloud-plugin-dict | 插件 | 全局字典管理 + 缓存翻译 | 依赖 cache |

#### P1 - 重要可选模块（第二阶段）

| 模块名称 | 标识 | 部署方式 | 功能说明 | 依赖关系 |
|---------|------|---------|---------|---------|
| 文件插件 | hivecloud-plugin-file | 插件 | 本地存储、阿里云OSS、腾讯云COS、MinIO | 无 |
| 定时任务插件 | hivecloud-plugin-job | 插件 | 轻量任务调度，cron表达式，任务监控 | 无 |
| 在线文档插件 | hivecloud-plugin-doc | 插件 | Knife4j API文档、文档聚合、在线调试 | 无 |
| 域内直连通道 | hivecloud-direct-connector | 嵌入服务节点 | HTTP直连，OkHttp连接池，绕开网关 | 依赖 service-registry |
| 双轨业务链路 | hivecloud-business-link | 嵌入服务节点 | 快轨(只读)+稳轨(写事务)分离 | 依赖 cache |
| 事件总线 | hivecloud-event-bus | 嵌入服务节点 | 服务间异步通信，发布订阅模式 | 无 |

#### P2 - 扩展模块（第三阶段）

| 模块名称 | 标识 | 部署方式 | 功能说明 | 依赖关系 |
|---------|------|---------|---------|---------|
| 支付服务 | hivecloud-service-pay | 独立微服务 | 订单支付、退款、对账、支付渠道对接 | 无 |
| 理赔服务 | hivecloud-service-claim | 独立微服务 | 理赔申请、审核、赔付 | 无 |
| 活动服务 | hivecloud-service-activity | 独立微服务 | 活动创建、参与、奖励发放 | 无 |
| 通知服务 | hivecloud-service-notice | 独立微服务 | 短信、邮件、站内信 | 无 |
| AI插件 | hivecloud-plugin-ai | 插件 | AI能力集成 | 无 |
| 加密插件 | hivecloud-plugin-encrypt | 插件 | 数据加密/解密 | 无 |
| 消息插件 | hivecloud-plugin-message | 插件 | RocketMQ/RabbitMQ 集成 | 无 |

### 3.2 模块依赖关系图

```
hivecloud-gateway
    ├── hivecloud-service-registry (嵌入)
    ├── hivecloud-heartbeat (嵌入)
    ├── hivecloud-fault-removal (嵌入)
    ├── hivecloud-gossip-sync (嵌入)
    ├── hivecloud-direct-connector (嵌入)
    ├── hivecloud-plugin-auth
    │   └── hivecloud-module-system
    ├── hivecloud-plugin-log
    ├── hivecloud-plugin-cache
    │   ├── Caffeine
    │   └── Redis
    └── hivecloud-plugin-dict
        └── hivecloud-plugin-cache

hivecloud-module-system
    ├── hivecloud-plugin-auth
    ├── hivecloud-plugin-log
    ├── hivecloud-plugin-cache
    └── hivecloud-plugin-dict

hivecloud-plugin-file
    ├── hivecloud-plugin-auth
    ├── hivecloud-plugin-log
    └── hivecloud-plugin-cache

hivecloud-plugin-job
    ├── hivecloud-plugin-auth
    ├── hivecloud-plugin-log
    └── hivecloud-plugin-cache

hivecloud-service-pay (独立微服务)
    ├── hivecloud-plugin-auth
    ├── hivecloud-plugin-log
    ├── hivecloud-plugin-cache
    └── hivecloud-plugin-encrypt (可选)

业务服务之间
    ├── hivecloud-business-link (双轨链路)
    ├── hivecloud-event-bus (事件总线)
    └── HTTP直连调用 (OkHttp)
```

---

## 4. 数据流路径与接口定义

### 4.1 核心数据流路径

#### 4.1.1 公网请求流程（稳轨链路）

```
客户端 → Nginx/Haproxy → 网关集群 → 服务治理层 → 业务服务 → MySQL
    │         │              │            │            │
    │         │              │            │            └─ 数据持久化
    │         │              │            └─ 本地缓存查询服务列表
    │         │              └─ JWT鉴权/限流/路由
    │         └─ SSL卸载/黑名单过滤
    └─ 发起HTTPS请求
```

**响应时间**: 50-200ms

#### 4.1.2 内网高频请求流程（快轨链路）

```
服务A → 本地缓存查询服务B列表 → OkHttp直连服务B → 服务B本地缓存返回数据
    │            │                      │                    │
    │            │                      │                    └─ 响应 5-15ms
    │            │                      └─ 直连 IP:Port
    │            └─ 无需调用Redis
    └─ @FastTrack 注解标记
```

**响应时间**: 5-15ms

#### 4.1.3 服务注册与发现流程

```
服务启动 → 上报元数据到Redis (元数据TTL=30s) → 每秒刷新心跳
    │              │                          │
    │              │                          └─ 推送心跳时间戳
    │              └─ Hash结构存储
    │                 Key: service:{服务标识}
    └─ 读取application.yml配置
          
其他服务 → 查询Redis获取服务列表 → 缓存到本地 → 每秒巡检健康状态
    │            │                      │              │
    │            │                      │              └─ >3s标记不健康
    │            │                      │              └─ >5s剔除
    │            │                      └─ Caffeine缓存
    │            └─ Hash查询
    └─ 发起服务调用前
```

#### 4.1.4 故障剔除流程

```
场景1: 主动优雅下线
服务关闭 → @PreDestroy钩子 → 删除Redis元数据和心跳Key → Gossip广播下线事件 → 集群100ms内删除本地缓存

场景2: 被动剔除（宕机/断网）
节点停止心跳 → Redis心跳Key过期(心跳TTL=5s) → 本地巡检发现 → 超过4s无心跳 → 移除节点 → 同步集群

场景3: 调用故障兜底
发起调用 → 连接超时(1000ms)/端口拒绝/连接重置 → 加入本地黑名单(10s) → 同步更新本地缓存和集群
```

### 4.2 核心接口定义

#### 4.2.1 网关接口规范

| 接口路径 | 方法 | 说明 | 响应格式 |
|---------|------|------|---------|
| `/api/{服务标识}/v1/{接口路径}` | 多种 | 公网统一入口 | `{code, message, data, timestamp}` |
| `/api/system/v1/login` | POST | 用户登录 | JWT Token |
| `/api/system/v1/getUserInfo` | GET | 获取用户信息 | 用户详情 |
| `/api/pay/v1/createOrder` | POST | 创建订单 | 订单信息 |

#### 4.2.2 内网直连接口规范

| 接口路径 | 方法 | 说明 | 鉴权 |
|---------|------|------|------|
| `/direct/{服务标识}/v1/{接口路径}` | 多种 | 内网高频调用 | 无需网关鉴权（内网隔离） |
| `/direct/system/v1/getUser` | GET | 获取用户信息 | 内网IP白名单 |

#### 4.2.3 元数据接口规范

| 接口 | 类型 | 说明 | 调用方 |
|------|------|------|--------|
| 元数据上报 | 内部调用 | 服务启动时上报自身信息到Redis | 服务节点 |
| 元数据查询 | 本地缓存 | 查询本地缓存的服务列表 | 服务节点 |
| 心跳推送 | 内部调用 | 每秒推送心跳时间戳到Redis | 服务节点 |

#### 4.2.4 插件SPI接口规范

```java
public interface Plugin {
    void initialize();  // 插件初始化
    void start();       // 插件启动
    void destroy();     // 插件销毁
}
```

### 4.3 数据存储结构

#### 4.3.1 Redis 元数据格式

```json
{
  "ip": "192.168.1.100",
  "port": 8080,
  "serviceId": "system",
  "env": "prod",
  "version": "1.0.0",
  "bizDomain": "base",
  "heartbeatTime": 1714000000000
}
```

**存储结构**: Redis Hash  
**Key**: `service:{服务标识}`  
**TTL**: 6秒，每秒刷新

#### 4.3.2 MySQL 数据库设计原则

- **模块化单体**: 统一数据库，可分表
- **独立微服务**: 独立数据库，垂直分库
- **读写分离**: 主从同步
- **数据备份**: 定期备份策略

---

## 5. 系统边界与依赖关系

### 5.1 系统边界

```
┌─────────────────────────────────────────────────────────────┐
│                      HiveCloud 系统边界                       │
│                                                             │
│  内部组件:                                                   │
│  ├── 网关集群 (hivecloud-gateway)                            │
│  ├── 业务服务 (模块化单体 + 独立微服务)                        │
│  ├── 服务治理 (嵌入每个服务节点)                              │
│  ├── 插件引擎 (SPI标准接口)                                  │
│  └── 数据自治 (本地缓存 + 分布式缓存)                         │
│                                                             │
│  外部依赖:                                                   │
│  ├── Redis (元数据存储/心跳/限流) - 可替换为 Memcached        │
│  ├── MySQL (数据持久化) - 可替换为 PostgreSQL                │
│  ├── Nacos (配置中心) - 可替换为 Apollo                      │
│  └── 消息队列 (可选) - RocketMQ/RabbitMQ                     │
│                                                             │
│  不依赖:                                                     │
│  ├── K8s / Docker (纯Jar包部署)                              │
│  ├── Redis (轻量中心化元数据存储)                          │
│  ├── Sentinel (内置限流算法)                                 │
│  └── XXL-Job (内置轻量任务调度)                              │
└─────────────────────────────────────────────────────────────┘
```

### 5.2 依赖关系矩阵

| 模块 | Redis | MySQL | Nacos | Caffeine | OkHttp | Gossip |
|------|-------|-------|-------|----------|--------|--------|
| hivecloud-gateway | 必须 | 可选 | 必须 | 必须 | 必须 | 必须 |
| hivecloud-module-system | 必须 | 必须 | 必须 | 必须 | 可选 | 必须 |
| hivecloud-plugin-file | 必须 | 必须 | 必须 | 必须 | 可选 | 必须 |
| hivecloud-plugin-job | 必须 | 必须 | 必须 | 必须 | 可选 | 必须 |
| hivecloud-service-pay | 必须 | 必须 | 必须 | 必须 | 必须 | 必须 |
| hivecloud-service-claim | 必须 | 必须 | 必须 | 必须 | 必须 | 必须 |
| hivecloud-service-activity | 必须 | 必须 | 必须 | 必须 | 必须 | 必须 |
| hivecloud-service-notice | 必须 | 必须 | 必须 | 必须 | 必须 | 必须 |

---

## 6. 分阶段开发计划

### 6.1 阶段划分

#### 第一阶段：核心底座搭建（P0模块）

**目标**: 完成基础框架和核心治理能力，确保系统可运行

| 任务编号 | 任务名称 | 模块 | 优先级 | 预估工作量 | 依赖 |
|---------|---------|------|--------|-----------|------|
| T1.1 | 项目骨架搭建 | 全局 | P0 | 3天 | 无 |
| T1.2 | 基础依赖配置 | 全局 | P0 | 2天 | T1.1 |
| T1.3 | 轻量元数据注册模块 | hivecloud-service-registry | P0 | 3天 | T1.2 |
| T1.4 | 分布式心跳探测模块 | hivecloud-heartbeat | P0 | 3天 | T1.3 |
| T1.5 | 故障剔除三重机制 | hivecloud-fault-removal | P0 | 4天 | T1.4 |
| T1.6 | 轻量集群同步模块 | hivecloud-gossip-sync | P0 | 5天 | T1.5 |
| T1.7 | 系统管理模块 | hivecloud-module-system | P0 | 7天 | T1.2 |
| T1.8 | 权限插件 | hivecloud-plugin-auth | P0 | 5天 | T1.7 |
| T1.9 | 日志插件 | hivecloud-plugin-log | P0 | 3天 | T1.2 |
| T1.10 | 缓存插件 | hivecloud-plugin-cache | P0 | 4天 | T1.2 |
| T1.11 | 字典插件 | hivecloud-plugin-dict | P0 | 3天 | T1.10 |
| T1.12 | 统一业务网关 | hivecloud-gateway | P0 | 5天 | T1.3-T1.6 |

**第一阶段交付物**:
- 可运行的基础系统（system模块 + 网关 + 服务治理）
- 服务注册/发现/心跳/故障剔除功能
- 基础权限/日志/缓存/字典插件
- 单元测试覆盖率 >= 70%

#### 第二阶段：业务能力增强（P1模块）

**目标**: 完善业务支撑能力，支持混合部署模式

| 任务编号 | 任务名称 | 模块 | 优先级 | 预估工作量 | 依赖 |
|---------|---------|------|--------|-----------|------|
| T2.1 | 文件插件 | hivecloud-plugin-file | P1 | 5天 | 第一阶段 |
| T2.2 | 定时任务插件 | hivecloud-plugin-job | P1 | 5天 | 第一阶段 |
| T2.3 | 在线文档插件 | hivecloud-plugin-doc | P1 | 3天 | 第一阶段 |
| T2.4 | 控制台功能 | system模块内置 | P1 | 4天 | 第一阶段 |
| T2.5 | 域内直连通道 | hivecloud-direct-connector | P1 | 4天 | T1.3 |
| T2.6 | 双轨业务链路 | hivecloud-business-link | P1 | 5天 | T1.10 |
| T2.7 | 事件总线 | hivecloud-event-bus | P1 | 4天 | 第一阶段 |
| T2.8 | 混合部署切换机制 | 全局 | P1 | 3天 | T2.1-T2.7 |
| T2.9 | 集成测试 | 全局 | P1 | 5天 | T2.1-T2.8 |

**第二阶段交付物**:
- 完整的模块化单体（system + file插件 + job插件）
- 控制台功能（嵌入system模块，实时监控服务状态）
- 在线文档插件（Knife4j API文档）
- 域内直连通道（内网高频调用）
- 双轨链路（快轨/稳轨自动路由）
- 事件总线（服务间异步通信）
- 集成测试覆盖率 >= 60%

#### 第三阶段：微服务扩展（P2模块）

**目标**: 实现独立微服务，支持业务场景扩展

| 任务编号 | 任务名称 | 模块 | 优先级 | 预估工作量 | 依赖 |
|---------|---------|------|--------|-----------|------|
| T3.1 | 支付服务 | hivecloud-service-pay | P2 | 7天 | 第二阶段 |
| T3.2 | 理赔服务 | hivecloud-service-claim | P2 | 7天 | 第二阶段 |
| T3.3 | 活动服务 | hivecloud-service-activity | P2 | 5天 | 第二阶段 |
| T3.4 | 通知服务 | hivecloud-service-notice | P2 | 4天 | 第二阶段 |
| T3.5 | AI插件 | hivecloud-plugin-ai | P2 | 4天 | 第一阶段 |
| T3.6 | 加密插件 | hivecloud-plugin-encrypt | P2 | 3天 | 第一阶段 |
| T3.7 | 消息插件 | hivecloud-plugin-message | P2 | 4天 | 第一阶段 |
| T3.8 | 性能优化 | 全局 | P2 | 5天 | T3.1-T3.4 |
| T3.9 | 压力测试 | 全局 | P2 | 5天 | T3.8 |

**第三阶段交付物**:
- 4个独立微服务（pay/claim/activity/notice）
- 3个扩展插件（ai/encrypt/message）
- 性能优化报告
- 压力测试报告

#### 第四阶段：生产就绪

**目标**: 完善监控、文档、部署脚本，达到生产可用标准

| 任务编号 | 任务名称 | 模块 | 优先级 | 预估工作量 | 依赖 |
|---------|---------|------|--------|-----------|------|
| T4.1 | 监控告警集成 | 全局 | P1 | 4天 | 第三阶段 |
| T4.2 | 部署脚本编写 | 全局 | P1 | 3天 | 第三阶段 |
| T4.3 | 文档完善 | 全局 | P1 | 5天 | 第三阶段 |
| T4.4 | 示例项目 | 全局 | P1 | 5天 | 第三阶段 |
| T4.5 | 开源发布准备 | 全局 | P1 | 3天 | T4.1-T4.4 |

**第四阶段交付物**:
- 完整的监控告警体系
- 一键部署脚本
- 完整的技术文档
- 示例项目代码
- 开源仓库发布

### 6.2 模块划分与包结构（Maven 多模块多环境 Alibaba 微服务规范）

```
hivecloud-ms/                                          # 根项目（父POM）
├── pom.xml                                            # 父POM：统一管理依赖版本、插件配置、多环境Profile
├── README.md                                          # 项目说明文档
├── .gitignore                                         # Git忽略配置
│
├── hivecloud-dependencies/                            # 公共依赖管理（BOM）
│   └── pom.xml                                        # 统一依赖版本，供所有子模块引用
│
├── hivecloud-common/                                  # 公共组件模块
│   ├── pom.xml
│   ├── hivecloud-common-core/                         # 核心工具类
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/common/core/
│   │       ├── constant/                              # 常量定义
│   │       ├── enums/                                 # 枚举类
│   │       ├── exception/                             # 异常定义
│   │       │   ├── BaseException.java
│   │       │   ├── BusinessException.java
│   │       │   └── GlobalExceptionHandler.java
│   │       ├── result/                                # 统一响应
│   │       │   ├── Result.java
│   │       │   └── PageResult.java
│   │       ├── utils/                                 # 工具类
│   │       │   ├── DateUtils.java
│   │       │   ├── StringUtils.java
│   │       │   └── JsonUtils.java
│   │       └── domain/                                # 基础领域对象
│   │           └── BaseEntity.java
│   │
│   ├── hivecloud-common-security/                     # 安全组件
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/common/security/
│   │       ├── config/                                # 安全配置
│   │       ├── jwt/                                   # JWT工具类
│   │       ├── annotation/                            # 安全注解
│   │       └── interceptor/                           # 安全拦截器
│   │
│   ├── hivecloud-common-redis/                        # Redis组件
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/common/redis/
│   │       ├── config/                                # Redis配置
│   │       ├── util/                                  # Redis工具类
│   │       └── serializer/                            # 序列化配置
│   │
│   ├── hivecloud-common-mybatis/                      # MyBatis组件
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/common/mybatis/
│   │       ├── config/                                # MyBatis配置
│   │       ├── handler/                               # 类型处理器
│   │       └── interceptor/                           # 分页拦截器
│   │
│   ├── hivecloud-common-web/                          # Web组件
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/common/web/
│   │       ├── config/                                # Web配置
│   │       ├── filter/                                # 过滤器
│   │       └── interceptor/                           # 拦截器
│   │
│   └── hivecloud-common-test/                         # 测试工具
│       ├── pom.xml
│       └── src/main/java/com/hivecloud/common/test/
│
├── hivecloud-framework/                               # 核心框架模块
│   ├── pom.xml
│   ├── hivecloud-plugin-engine/                       # 插件引擎
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/plugin/engine/
│   │       ├── Plugin.java                            # SPI标准接口
│   │       ├── PluginManager.java                     # 插件管理器
│   │       └── PluginLoader.java                      # 插件加载器
│   │
│   ├── hivecloud-service-registry/                    # 元数据注册
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/core/registry/
│   │       ├── ServiceRegistry.java                   # 服务注册接口
│   │       ├── ServiceRegistryImpl.java               # 服务注册实现
│   │       ├── model/
│   │       │   └── ServiceMetadata.java               # 服务元数据模型
│   │       └── config/
│   │           └── RegistryProperties.java            # 注册配置
│   │
│   ├── hivecloud-heartbeat/                           # 心跳探测
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/core/heartbeat/
│   │       ├── HeartbeatManager.java                  # 心跳管理器
│   │       ├── HeartbeatScheduler.java                # 心跳调度器
│   │       └── config/
│   │           └── HeartbeatProperties.java           # 心跳配置
│   │
│   ├── hivecloud-fault-removal/                        # 故障剔除
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/core/fault/
│   │       ├── FaultDetector.java                     # 故障检测器
│   │       ├── FaultRemover.java                      # 故障剔除器
│   │       ├── BlacklistManager.java                  # 黑名单管理器
│   │       └── strategy/
│   │           ├── ActiveOfflineStrategy.java         # 主动下线策略
│   │           ├── PassiveRemoveStrategy.java         # 被动剔除策略
│   │           └── CallFallbackStrategy.java          # 调用兜底策略
│   │
│   ├── hivecloud-gossip-sync/                         # 集群同步
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/core/gossip/
│   │       ├── GossipManager.java                     # Gossip管理器
│   │       ├── GossipConfig.java                      # Gossip配置
│   │       ├── member/
│   │       │   ├── Member.java                        # 成员模型
│   │       │   └── MemberList.java                    # 成员列表
│   │       └── detector/
│   │           └── PhiAccrualDetector.java            # Phi故障检测器
│   │
│   ├── hivecloud-direct-connector/                    # 直连通道
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/core/direct/
│   │       ├── DirectConnector.java                   # 直连接口
│   │       ├── OkHttpDirectConnector.java             # OkHttp实现
│   │       ├── ConnectionPoolManager.java             # 连接池管理器
│   │       └── config/
│   │           └── DirectProperties.java              # 直连配置
│   │
│   ├── hivecloud-business-link/                       # 双轨链路
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/core/business/
│   │       ├── annotation/
│   │       │   ├── FastTrack.java                     # 快轨注解
│   │       │   └── StableTrack.java                   # 稳轨注解
│   │       ├── router/
│   │       │   └── TrackRouter.java                   # 链路路由器
│   │       └── interceptor/
│   │           └── TrackInterceptor.java              # 链路拦截器
│   │
│   └── hivecloud-event-bus/                           # 事件总线
│       ├── pom.xml
│       └── src/main/java/com/hivecloud/core/event/
│           ├── EventBus.java                          # 事件总线接口
│           ├── LocalEventBus.java                     # 本地事件总线
│           ├── annotation/
│           │   └── EventListener.java                 # 事件监听注解
│           └── model/
│               └── HiveEvent.java                     # 事件模型
│
├── hivecloud-gateway/                                 # 统一网关
│   ├── pom.xml
│   ├── src/main/java/com/hivecloud/gateway/
│   │   ├── GatewayApplication.java
│   │   ├── config/
│   │   │   ├── GatewayConfig.java                     # 网关配置
│   │   │   ├── RouteConfig.java                       # 路由配置
│   │   │   └── RateLimiterConfig.java                 # 限流配置
│   │   ├── filter/
│   │   │   ├── AuthFilter.java                        # 鉴权过滤器
│   │   │   ├── RateLimitFilter.java                   # 限流过滤器
│   │   │   ├── LogFilter.java                         # 日志过滤器
│   │   │   └── BlacklistFilter.java                   # 黑名单过滤器
│   │   └── handler/
│   │       ├── GlobalExceptionHandler.java            # 全局异常处理
│   │       └── FallbackHandler.java                   # 降级处理器
│   └── src/main/resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-test.yml
│       └── application-prod.yml
│
├── hivecloud-modules/                                 # 模块化单体
│   ├── pom.xml
│   └── hivecloud-module-system/                       # 系统管理（含控制台）
│       ├── pom.xml
│       ├── src/main/java/com/hivecloud/module/system/
│       │   ├── SystemApplication.java
│       │   ├── controller/
│       │   │   ├── console/                           # 控制台API
│       │   │   │   ├── ServiceStatusController.java   # 服务状态接口
│       │   │   │   ├── HealthMonitorController.java   # 健康监控接口
│       │   │   │   ├── CacheInfoController.java       # 缓存信息接口
│       │   │   │   └── MetricsController.java         # 监控指标接口
│       │   │   ├── user/                              # 用户管理
│       │   │   │   ├── UserController.java
│       │   │   │   └── UserLoginController.java
│       │   │   ├── role/                              # 角色管理
│       │   │   │   └── RoleController.java
│       │   │   ├── menu/                              # 菜单管理
│       │   │   │   └── MenuController.java
│       │   │   └── dict/                              # 字典管理
│       │   │       └── DictController.java
│       │   ├── service/
│       │   │   ├── console/                           # 控制台服务
│       │   │   │   ├── ServiceRegistryService.java
│       │   │   │   ├── ServiceRegistryServiceImpl.java
│       │   │   │   ├── HealthMonitorService.java
│       │   │   │   ├── HealthMonitorServiceImpl.java
│       │   │   │   ├── CacheInfoService.java
│       │   │   │   ├── CacheInfoServiceImpl.java
│       │   │   │   ├── MetricsService.java
│       │   │   │   └── MetricsServiceImpl.java
│       │   │   ├── user/
│       │   │   │   ├── UserService.java
│       │   │   │   └── UserServiceImpl.java
│       │   │   ├── role/
│       │   │   │   ├── RoleService.java
│       │   │   │   └── RoleServiceImpl.java
│       │   │   ├── menu/
│       │   │   │   ├── MenuService.java
│       │   │   │   └── MenuServiceImpl.java
│       │   │   └── dict/
│       │   │       ├── DictService.java
│       │   │       └── DictServiceImpl.java
│       │   ├── mapper/
│       │   │   ├── UserMapper.java
│       │   │   ├── RoleMapper.java
│       │   │   ├── MenuMapper.java
│       │   │   └── DictMapper.java
│       │   ├── entity/
│       │   │   ├── UserEntity.java
│       │   │   ├── RoleEntity.java
│       │   │   ├── MenuEntity.java
│       │   │   └── DictEntity.java
│       │   ├── dto/
│       │   │   ├── UserDTO.java
│       │   │   ├── RoleDTO.java
│       │   │   └── LoginDTO.java
│       │   ├── vo/
│       │   │   ├── ServiceStatusVO.java               # 服务状态视图
│       │   │   ├── HealthStatusVO.java                # 健康状态视图
│       │   │   ├── CacheInfoVO.java                   # 缓存信息视图
│       │   │   ├── MetricsVO.java                     # 监控指标视图
│       │   │   ├── UserVO.java
│       │   │   └── RouterVO.java
│       │   └── websocket/                             # WebSocket模块
│       │       ├── WebSocketConfig.java               # WebSocket配置
│       │       ├── ConsoleWebSocketHandler.java       # WebSocket处理器
│       │       └── StatusPushService.java             # 状态推送服务
│       └── src/main/resources/
│           ├── application.yml
│           ├── application-dev.yml
│           ├── application-test.yml
│           ├── application-prod.yml
│           ├── mapper/                                # MyBatis Mapper XML
│           │   ├── UserMapper.xml
│           │   ├── RoleMapper.xml
│           │   ├── MenuMapper.xml
│           │   └── DictMapper.xml
│           └── static/console/                        # 控制台静态页面
│               ├── index.html
│               ├── dashboard.html
│               ├── css/
│               │   └── style.css
│               ├── js/
│               │   ├── websocket.js
│               │   ├── dashboard.js
│               │   └── charts.js
│               └── assets/
│
├── hivecloud-services/                                # 独立微服务
│   ├── pom.xml
│   │
│   ├── hivecloud-service-pay/                         # 支付服务
│   │   ├── pom.xml
│   │   ├── src/main/java/com/hivecloud/service/pay/
│   │   │   ├── PayApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── OrderController.java
│   │   │   │   ├── PayController.java
│   │   │   │   └── RefundController.java
│   │   │   ├── service/
│   │   │   │   ├── OrderService.java
│   │   │   │   ├── OrderServiceImpl.java
│   │   │   │   ├── PayService.java
│   │   │   │   ├── PayServiceImpl.java
│   │   │   │   ├── RefundService.java
│   │   │   │   └── RefundServiceImpl.java
│   │   │   ├── mapper/
│   │   │   │   ├── OrderMapper.java
│   │   │   │   └── PayRecordMapper.java
│   │   │   ├── entity/
│   │   │   │   ├── OrderEntity.java
│   │   │   │   └── PayRecordEntity.java
│   │   │   ├── dto/
│   │   │   │   ├── CreateOrderDTO.java
│   │   │   │   └── PayDTO.java
│   │   │   └── vo/
│   │   │       ├── OrderVO.java
│   │   │       └── PayResultVO.java
│   │   └── src/main/resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-test.yml
│   │       └── application-prod.yml
│   │
│   ├── hivecloud-service-claim/                       # 理赔服务
│   │   ├── pom.xml
│   │   ├── src/main/java/com/hivecloud/service/claim/
│   │   │   ├── ClaimApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── ClaimController.java
│   │   │   │   ├── AuditController.java
│   │   │   │   └── SettlementController.java
│   │   │   ├── service/
│   │   │   │   ├── ClaimService.java
│   │   │   │   ├── ClaimServiceImpl.java
│   │   │   │   ├── AuditService.java
│   │   │   │   ├── AuditServiceImpl.java
│   │   │   │   ├── SettlementService.java
│   │   │   │   └── SettlementServiceImpl.java
│   │   │   ├── mapper/
│   │   │   │   ├── ClaimMapper.java
│   │   │   │   └── AuditRecordMapper.java
│   │   │   ├── entity/
│   │   │   │   ├── ClaimEntity.java
│   │   │   │   └── AuditRecordEntity.java
│   │   │   ├── dto/
│   │   │   │   ├── CreateClaimDTO.java
│   │   │   │   └── AuditDTO.java
│   │   │   └── vo/
│   │   │       ├── ClaimVO.java
│   │   │       └── AuditResultVO.java
│   │   └── src/main/resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-test.yml
│   │       └── application-prod.yml
│   │
│   ├── hivecloud-service-activity/                    # 活动服务
│   │   ├── pom.xml
│   │   ├── src/main/java/com/hivecloud/service/activity/
│   │   │   ├── ActivityApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── ActivityController.java
│   │   │   │   ├── ParticipateController.java
│   │   │   │   └── RewardController.java
│   │   │   ├── service/
│   │   │   │   ├── ActivityService.java
│   │   │   │   ├── ActivityServiceImpl.java
│   │   │   │   ├── ParticipateService.java
│   │   │   │   ├── ParticipateServiceImpl.java
│   │   │   │   ├── RewardService.java
│   │   │   │   └── RewardServiceImpl.java
│   │   │   ├── mapper/
│   │   │   │   ├── ActivityMapper.java
│   │   │   │   └── ParticipateRecordMapper.java
│   │   │   ├── entity/
│   │   │   │   ├── ActivityEntity.java
│   │   │   │   └── ParticipateRecordEntity.java
│   │   │   ├── dto/
│   │   │   │   ├── CreateActivityDTO.java
│   │   │   │   └── ParticipateDTO.java
│   │   │   └── vo/
│   │   │       ├── ActivityVO.java
│   │   │       └── RewardVO.java
│   │   └── src/main/resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-test.yml
│   │       └── application-prod.yml
│   │
│   └── hivecloud-service-notice/                      # 通知服务
│       ├── pom.xml
│       ├── src/main/java/com/hivecloud/service/notice/
│       │   ├── NoticeApplication.java
│       │   ├── controller/
│       │   │   ├── SmsController.java
│       │   │   ├── EmailController.java
│       │   │   └── StationLetterController.java
│       │   ├── service/
│       │   │   ├── SmsService.java
│       │   │   ├── SmsServiceImpl.java
│       │   │   ├── EmailService.java
│       │   │   ├── EmailServiceImpl.java
│       │   │   ├── StationLetterService.java
│       │   │   └── StationLetterServiceImpl.java
│       │   ├── mapper/
│       │   │   ├── SmsRecordMapper.java
│       │   │   └── EmailRecordMapper.java
│       │   ├── entity/
│       │   │   ├── SmsRecordEntity.java
│       │   │   └── EmailRecordEntity.java
│       │   ├── dto/
│       │   │   ├── SendSmsDTO.java
│       │   │   └── SendEmailDTO.java
│       │   └── vo/
│       │       ├── SmsResultVO.java
│       │       └── EmailResultVO.java
│       └── src/main/resources/
│           ├── application.yml
│           ├── application-dev.yml
│           ├── application-test.yml
│           └── application-prod.yml
│
├── hivecloud-plugins/                                 # 插件库
│   ├── pom.xml
│   │
│   ├── hivecloud-plugin-auth/                         # 权限插件
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/plugin/auth/
│   │       ├── AuthPlugin.java
│   │       ├── config/
│   │       │   ├── AuthConfig.java
│   │       │   └── SecurityConfig.java
│   │       ├── service/
│   │       │   ├── AuthService.java
│   │       │   ├── PermissionService.java
│   │       │   └── DataScopeService.java
│   │       ├── annotation/
│   │       │   ├── RequiresPermissions.java
│   │       │   ├── RequiresRoles.java
│   │       │   └── DataScope.java
│   │       └── interceptor/
│   │           └── AuthInterceptor.java
│   │
│   ├── hivecloud-plugin-log/                          # 日志插件
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/plugin/log/
│   │       ├── LogPlugin.java
│   │       ├── annotation/
│   │       │   └── OperationLog.java
│   │       ├── aspect/
│   │       │   └── LogAspect.java
│   │       ├── service/
│   │       │   ├── OperationLogService.java
│   │       │   └── ExceptionLogService.java
│   │       ├── entity/
│   │       │   ├── OperationLogEntity.java
│   │       │   └── ExceptionLogEntity.java
│   │       └── config/
│   │           └── LogProperties.java
│   │
│   ├── hivecloud-plugin-dict/                         # 字典插件
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/plugin/dict/
│   │       ├── DictPlugin.java
│   │       ├── service/
│   │       │   ├── DictService.java
│   │       │   └── DictCacheService.java
│   │       ├── controller/
│   │       │   └── DictController.java
│   │       ├── entity/
│   │       │   └── DictEntity.java
│   │       └── config/
│   │           └── DictProperties.java
│   │
│   ├── hivecloud-plugin-cache/                        # 缓存插件
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/plugin/cache/
│   │       ├── CachePlugin.java
│   │       ├── CacheManager.java
│   │       ├── adapter/
│   │       │   ├── CaffeineAdapter.java
│   │       │   └── RedisAdapter.java
│   │       ├── annotation/
│   │       │   ├── Cacheable.java
│   │       │   ├── CacheEvict.java
│   │       │   └── CachePut.java
│   │       ├── aspect/
│   │       │   └── CacheAspect.java
│   │       └── config/
│   │           └── CacheProperties.java
│   │
│   ├── hivecloud-plugin-file/                         # 文件插件
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/plugin/file/
│   │       ├── FilePlugin.java
│   │       ├── controller/
│   │       │   └── FileController.java
│   │       ├── service/
│   │       │   ├── FileService.java
│   │       │   ├── LocalStorageService.java
│   │       │   ├── OssService.java
│   │       │   ├── MinioService.java
│   │       │   └── impl/
│   │       ├── mapper/
│   │       │   └── FileRecordMapper.java
│   │       ├── entity/
│   │       │   └── FileRecordEntity.java
│   │       ├── dto/
│   │       │   └── FileUploadDTO.java
│   │       ├── vo/
│   │       │   └── FileVO.java
│   │       └── config/
│   │           └── FileProperties.java
│   │
│   ├── hivecloud-plugin-job/                          # 定时任务插件
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/plugin/job/
│   │       ├── JobPlugin.java
│   │       ├── scheduler/
│   │       │   ├── JobScheduler.java
│   │       │   ├── JobExecutor.java
│   │       │   └── JobMonitor.java
│   │       ├── service/
│   │       │   ├── JobService.java
│   │       │   ├── JobLogService.java
│   │       │   └── impl/
│   │       ├── mapper/
│   │       │   ├── JobMapper.java
│   │       │   └── JobLogMapper.java
│   │       ├── entity/
│   │       │   ├── JobEntity.java
│   │       │   └── JobLogEntity.java
│   │       ├── annotation/
│   │       │   └── ScheduledJob.java
│   │       └── config/
│   │           └── JobProperties.java
│   │
│   ├── hivecloud-plugin-doc/                          # 在线文档插件
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/plugin/doc/
│   │       ├── DocPlugin.java
│   │       ├── config/
│   │       │   ├── SwaggerConfig.java
│   │       │   ├── OpenApiConfig.java
│   │       │   └── DocProperties.java
│   │       ├── controller/
│   │       │   ├── DocController.java
│   │       │   └── ApiDocController.java
│   │       ├── service/
│   │       │   ├── DocAggregationService.java
│   │       │   ├── ApiScanService.java
│   │       │   └── impl/
│   │       ├── model/
│   │       │   ├── ApiInfo.java
│   │       │   ├── ApiParam.java
│   │       │   ├── ApiResponse.java
│   │       │   └── ApiGroup.java
│   │       └── filter/
│   │           └── DocAccessFilter.java
│   │
│   ├── hivecloud-plugin-ai/                           # AI插件
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/plugin/ai/
│   │       ├── AiPlugin.java
│   │       ├── config/
│   │       │   └── AiProperties.java
│   │       ├── service/
│   │       │   ├── TextRecognitionService.java
│   │       │   ├── VoiceConversionService.java
│   │       │   └── impl/
│   │       └── model/
│   │           ├── TextRecognitionRequest.java
│   │           └── TextRecognitionResponse.java
│   │
│   ├── hivecloud-plugin-encrypt/                      # 加密插件
│   │   ├── pom.xml
│   │   └── src/main/java/com/hivecloud/plugin/encrypt/
│   │       ├── EncryptPlugin.java
│   │       ├── service/
│   │       │   ├── EncryptService.java
│   │       │   ├── DecryptService.java
│   │       │   └── impl/
│   │       ├── annotation/
│   │       │   └── SensitiveData.java
│   │       ├── aspect/
│   │       │   └── EncryptAspect.java
│   │       └── config/
│   │           └── EncryptProperties.java
│   │
│   └── hivecloud-plugin-message/                      # 消息插件
│       ├── pom.xml
│       └── src/main/java/com/hivecloud/plugin/message/
│           ├── MessagePlugin.java
│           ├── config/
│           │   ├── RocketMQConfig.java
│           │   ├── RabbitMQConfig.java
│           │   └── MessageProperties.java
│           ├── service/
│           │   ├── MessageProducer.java
│           │   ├── MessageConsumer.java
│           │   ├── RocketMQProducer.java
│           │   ├── RocketMQConsumer.java
│           │   ├── RabbitMQProducer.java
│           │   └── RabbitMQConsumer.java
│           ├── annotation/
│           │   └── MessageListener.java
│           └── model/
│               └── Message.java
│
├── hivecloud-examples/                                # 示例项目
│   ├── pom.xml
│   └── hivecloud-example-demo/
│       ├── pom.xml
│       └── src/
│           ├── main/
│           │   ├── java/com/hivecloud/example/
│           │   │   ├── ExampleApplication.java
│           │   │   ├── controller/
│           │   │   │   └── DemoController.java
│           │   │   ├── service/
│           │   │   │   └── DemoService.java
│           │   │   └── config/
│           │   │       └── ExampleConfig.java
│           │   └── resources/
│           │       ├── application.yml
│           │       └── application-dev.yml
│           └── test/
│               └── java/
│
└── scripts/                                           # 部署脚本
    ├── deploy-dev.sh                                  # 开发环境部署
    ├── deploy-test.sh                                 # 测试环境部署
    ├── deploy-prod.sh                                 # 生产环境部署
    ├── startup.sh                                     # 通用启动脚本
    ├── shutdown.sh                                    # 通用停止脚本
    └── health-check.sh                                # 健康检查脚本
```

### 6.3 模块转插件方案（file模块 & job模块 & console控制台）

#### 6.3.1 模块调整说明

| 调整类型 | 原模块/服务 | 新插件 | 原因 |
|---------|------------|--------|------|
| 模块转插件 | hivecloud-module-file | hivecloud-plugin-file | 1. 文件管理是通用能力，多个业务都需要；2. 插件化后可按需加载，降低业务服务依赖；3. 支持多种存储方式（本地/OSS/MinIO）灵活切换 |
| 模块转插件 | hivecloud-module-job | hivecloud-plugin-job | 1. 定时任务是通用能力，各业务服务都可能需要；2. 插件化后避免引入XXL-Job等重型组件；3. 轻量级任务调度，支持cron表达式、任务重试、任务监控 |
| 新增插件 | - | hivecloud-plugin-doc | 1. 自动生成API文档，减少文档维护成本；2. 支持Knife4j规范，提供在线调试功能；3. 聚合所有微服务API文档，统一入口查看 |
| 嵌入模块 | - | system模块内置控制台 | 1. 控制台与系统管理强关联，嵌入system模块更合理；2. 无需独立部署，降低运维复杂度；3. 静态页面+WebSocket实时推送，轻量实现 |

#### 6.3.2 插件化后的优势

```
原架构：
  业务服务 → 内置file模块（强依赖）
  业务服务 → 内置job模块（强依赖）
  运维人员 → 查看日志文件 → 手动监控服务状态

新架构：
  业务服务 → 按需加载 → file插件（可配置启用/禁用）
  业务服务 → 按需加载 → job插件（可配置启用/禁用）
  运维人员 → Console控制台 → 实时监控服务状态（WebSocket推送）
```

**性能提升**：
- 业务服务按需加载插件，减少不必要的依赖（启动时间降低 30%+）
- 控制台实时监控替代人工查看日志（运维效率提升 80%+）
- 插件热插拔，无需重启服务（可用性提升）

**灵活性**：
- file插件支持多种存储方式（本地存储、阿里云OSS、腾讯云COS、MinIO）
- job插件支持cron表达式、任务重试、任务监控、任务日志
- console插件提供WebSocket实时推送，支持服务状态、健康监控、缓存信息、性能指标

#### 6.3.3 插件实现示例

```java
// 文件插件实现
package com.hivecloud.plugin.file;

import com.hivecloud.plugin.engine.Plugin;

/**
 * 文件插件
 */
public class FilePlugin implements Plugin {
    
    @Override
    public void initialize() {
        // 初始化文件配置（本地存储/OSS/MinIO）
    }
    
    @Override
    public void start() {
        // 启动文件服务
    }
    
    @Override
    public void destroy() {
        // 清理文件资源
    }
}

// 定时任务插件实现
package com.hivecloud.plugin.job;

import com.hivecloud.plugin.engine.Plugin;

/**
 * 定时任务插件
 */
public class JobPlugin implements Plugin {
    
    @Override
    public void initialize() {
        // 初始化任务调度器
    }
    
    @Override
    public void start() {
        // 启动任务调度
    }
    
    @Override
    public void destroy() {
        // 停止任务调度
    }
}

// 控制台插件实现
package com.hivecloud.plugin.console;

import com.hivecloud.plugin.engine.Plugin;

/**
 * 控制台插件
 */
public class ConsolePlugin implements Plugin {
    
    @Override
    public void initialize() {
        // 初始化控制台配置
    }
    
    @Override
    public void start() {
        // 启动WebSocket服务
    }
    
    @Override
    public void destroy() {
        // 关闭WebSocket连接
    }
}
```

#### 6.3.4 Console控制台插件功能详解

**核心功能**：

| 功能模块 | 说明 | 接口路径 | 数据源 |
|---------|------|---------|--------|
| 服务注册状态 | 实时显示所有微服务的注册状态（在线/离线/不健康） | `/console/api/services` | Redis元数据 + 本地缓存 |
| 健康监控 | 监控服务健康状态（CPU、内存、线程池、数据库连接池） | `/console/api/health` | Actuator + 自定义指标 |
| 缓存信息 | 展示缓存数据信息（命中率、内存占用、Key数量） | `/console/api/cache` | Caffeine + Redis |
| 监控指标 | 展示关键监控指标（QPS、RT、错误率、请求量） | `/console/api/metrics` | Micrometer + Prometheus |
| 实时推送 | WebSocket实时推送状态变更 | `/console/ws` | WebSocket |

**Console控制台服务类设计**：

```java
// 服务注册状态服务
@Service
public class ServiceRegistryService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    /**
     * 获取所有服务注册状态
     */
    public List<ServiceStatusVO> getAllServiceStatus() {
        // 从Redis获取服务元数据
        // 从本地缓存获取健康状态
        // 合并返回
    }
    
    /**
     * 获取指定服务的注册状态
     */
    public ServiceStatusVO getServiceStatus(String serviceId) {
        // 查询Redis元数据
        // 返回服务状态
    }
}

// 健康监控服务
@Service
public class HealthMonitorService {
    
    /**
     * 获取服务健康状态
     */
    public HealthStatusVO getHealthStatus(String serviceId) {
        // 获取CPU使用率
        // 获取内存使用率
        // 获取线程池状态
        // 获取数据库连接池状态
        // 返回健康状态
    }
}

// 缓存信息服务
@Service
public class CacheInfoService {
    
    @Autowired
    private CacheManager cacheManager;
    
    /**
     * 获取缓存信息
     */
    public CacheInfoVO getCacheInfo() {
        // 获取Caffeine缓存信息
        // 获取Redis缓存信息
        // 返回缓存信息（命中率、内存占用、Key数量）
    }
}

// 监控指标服务
@Service
public class MetricsService {
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    /**
     * 获取监控指标
     */
    public MetricsVO getMetrics(String serviceId) {
        // 获取QPS
        // 获取响应时间（RT）
        // 获取错误率
        // 获取请求量
        // 返回监控指标
    }
}
```

**Console控制台WebSocket实时推送**：

```java
// WebSocket处理器
@Component
public class ConsoleWebSocket extends TextWebSocketHandler {
    
    @Autowired
    private StatusPushService statusPushService;
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 客户端连接后，开始推送状态
        statusPushService.registerSession(session);
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 处理客户端消息
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 客户端断开后，注销会话
        statusPushService.unregisterSession(session);
    }
}

// 状态推送服务
@Service
public class StatusPushService {
    
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    
    @Autowired
    private ServiceRegistryService registryService;
    
    @Autowired
    private HealthMonitorService healthService;
    
    /**
     * 注册会话
     */
    public void registerSession(WebSocketSession session) {
        sessions.add(session);
    }
    
    /**
     * 注销会话
     */
    public void unregisterSession(WebSocketSession session) {
        sessions.remove(session);
    }
    
    /**
     * 推送状态变更（定时任务，每秒推送一次）
     */
    @Scheduled(fixedRate = 1000)
    public void pushStatusUpdate() {
        // 获取最新状态
        List<ServiceStatusVO> statusList = registryService.getAllServiceStatus();
        HealthStatusVO healthStatus = healthService.getHealthStatus("all");
        
        // 构造推送消息
        String message = JsonUtils.toJson(Map.of(
            "type", "status_update",
            "data", Map.of(
                "services", statusList,
                "health", healthStatus
            )
        ));
        
        // 推送给所有客户端
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    // 忽略异常
                }
            }
        }
    }
}
```

**Console控制台VO设计**：

```java
// 服务状态视图
@Data
public class ServiceStatusVO {
    private String serviceId;          // 服务标识
    private String ip;                 // IP地址
    private Integer port;              // 端口
    private String status;             // 状态（online/offline/unhealthy）
    private String version;            // 版本
    private String env;                // 环境
    private String bizDomain;          // 业务域
    private Long heartbeatTime;        // 最后心跳时间
    private Long lastUpdateTime;       // 最后更新时间
}

// 健康状态视图
@Data
public class HealthStatusVO {
    private String serviceId;          // 服务标识
    private Double cpuUsage;           // CPU使用率
    private Double memoryUsage;        // 内存使用率
    private Integer threadCount;       // 线程数
    private Integer activeThreadCount; // 活跃线程数
    private Integer dbPoolSize;        // 数据库连接池大小
    private Integer dbActiveCount;     // 数据库活跃连接数
    private String status;             // 健康状态（healthy/warning/unhealthy）
}

// 缓存信息视图
@Data
public class CacheInfoVO {
    private String cacheType;          // 缓存类型（caffeine/redis）
    private Long hitCount;             // 命中次数
    private Long missCount;            // 未命中次数
    private Double hitRate;            // 命中率
    private Long memoryUsage;          // 内存占用（字节）
    private Long keyCount;             // Key数量
    private Long evictionCount;        // 淘汰次数
}

// 监控指标视图
@Data
public class MetricsVO {
    private String serviceId;          // 服务标识
    private Double qps;                // QPS
    private Double avgRt;              // 平均响应时间（ms）
    private Double p99Rt;              // P99响应时间（ms）
    private Double errorRate;          // 错误率
    private Long requestCount;         // 请求总数
    private Long successCount;         // 成功数
    private Long failCount;            // 失败数
}
```

#### 6.3.5 在线文档插件功能详解

**核心功能**：

| 功能模块 | 说明 | 接口路径 | 技术实现 |
|---------|------|---------|---------|
| API文档生成 | 自动扫描Controller生成API文档 | `/doc/api` | Knife4j OpenAPI 3 |
| 文档聚合 | 聚合所有微服务的API文档 | `/doc/aggregation` | 远程API拉取 + 本地缓存 |
| 在线调试 | 提供API在线测试功能 | `/doc/doc.html` | Knife4j UI |
| 文档导出 | 支持导出OpenAPI JSON/YAML/Markdown | `/doc/export` | OpenAPI规范 |
| 版本管理 | 支持API文档版本切换 | `/doc/version/{version}` | 版本控制 |
| 离线文档 | 生成离线Markdown/HTML文档 | `/doc/offline` | Knife4j增强功能 |

**在线文档插件实现**：

```java
// 文档插件实现
package com.hivecloud.plugin.doc;

import com.hivecloud.plugin.engine.Plugin;

/**
 * 在线文档插件
 */
public class DocPlugin implements Plugin {
    
    @Override
    public void initialize() {
        // 初始化文档配置（Swagger/OpenAPI）
    }
    
    @Override
    public void start() {
        // 启动文档服务
    }
    
    @Override
    public void destroy() {
        // 关闭文档服务
    }
}
```

**文档配置类**：

```java
// Knife4j配置
@Configuration
public class Knife4jConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("HiveCloud API文档")
                .description("HiveCloud轻量微服务脚手架API文档")
                .version("v1.0.0")
                .contact(new Contact()
                    .name("HiveCloud Team")
                    .email("support@hivecloud.com")))
            .servers(List.of(
                new Server().url("http://localhost:8080").description("开发环境"),
                new Server().url("http://test.hivecloud.com").description("测试环境"),
                new Server().url("https://api.hivecloud.com").description("生产环境")
            ));
    }
}

// 文档属性配置
@Data
@ConfigurationProperties(prefix = "hivecloud.doc")
public class DocProperties {
    private boolean enabled = true;              // 是否启用文档
    private String title = "HiveCloud API";      // 文档标题
    private String description = "API文档";      // 文档描述
    private String version = "v1.0.0";           // 文档版本
    private boolean aggregationEnabled = true;   // 是否启用文档聚合
    private List<String> serviceUrls = new ArrayList<>();  // 聚合的服务URL列表
    private long cacheTimeout = 300;             // 缓存超时时间（秒）
    private boolean basicAuthEnabled = false;    // 是否启用BasicAuth
    private String basicAuthUsername = "admin";  // BasicAuth用户名
    private String basicAuthPassword = "123456"; // BasicAuth密码
}
```

**文档聚合服务**：

```java
// 文档聚合服务
@Service
public class DocAggregationService {
    
    @Autowired
    private DocProperties docProperties;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * 聚合所有服务的API文档
     */
    public Map<String, Object> aggregateAllDocs() {
        Map<String, Object> result = new HashMap<>();
        
        // 先从Redis缓存获取
        String cached = redisTemplate.opsForValue().get("doc:aggregation");
        if (cached != null) {
            return JsonUtils.fromJson(cached, Map.class);
        }
        
        // 缓存未命中，从各服务拉取
        List<String> serviceUrls = docProperties.getServiceUrls();
        for (String url : serviceUrls) {
            try {
                String serviceId = extractServiceId(url);
                String openApiJson = restTemplate.getForObject(
                    url + "/v3/api-docs", String.class);
                result.put(serviceId, JsonUtils.fromJson(openApiJson, Map.class));
            } catch (Exception e) {
                // 记录错误，继续处理其他服务
                result.put("error", "Failed to fetch docs from: " + url);
            }
        }
        
        // 缓存结果
        redisTemplate.opsForValue().set(
            "doc:aggregation",
            JsonUtils.toJson(result),
            docProperties.getCacheTimeout(),
            TimeUnit.SECONDS
        );
        
        return result;
    }
    
    /**
     * 刷新文档缓存
     */
    public void refreshCache() {
        redisTemplate.delete("doc:aggregation");
        aggregateAllDocs();
    }
    
    private String extractServiceId(String url) {
        // 从URL提取服务标识
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
```

**文档控制器**：

```java
// 文档首页控制器
@RestController
@RequestMapping("/doc")
public class DocController {
    
    @Autowired
    private DocProperties docProperties;
    
    /**
     * 文档首页
     */
    @GetMapping
    public ResponseEntity<String> docHome() {
        if (!docProperties.isEnabled()) {
            return ResponseEntity.status(403).body("文档功能已禁用");
        }
        // 重定向到Knife4j UI
        return ResponseEntity.status(302)
            .header("Location", "/doc/doc.html")
            .build();
    }
}

// API文档接口控制器
@RestController
@RequestMapping("/doc/api")
public class ApiDocController {
    
    @Autowired
    private DocAggregationService aggregationService;
    
    /**
     * 获取聚合文档
     */
    @GetMapping("/aggregation")
    public Result<Map<String, Object>> getAggregatedDocs() {
        return Result.success(aggregationService.aggregateAllDocs());
    }
    
    /**
     * 刷新文档缓存
     */
    @PostMapping("/refresh")
    public Result<Void> refreshCache() {
        aggregationService.refreshCache();
        return Result.success();
    }
    
    /**
     * 导出OpenAPI文档
     */
    @GetMapping("/export")
    public ResponseEntity<String> exportDocs(
        @RequestParam(defaultValue = "json") String format) {
        Map<String, Object> docs = aggregationService.aggregateAllDocs();
        String content = switch (format) {
            case "json" -> JsonUtils.toJson(docs);
            case "yaml" -> convertToYaml(docs);
            case "markdown" -> convertToMarkdown(docs);
            default -> JsonUtils.toJson(docs);
        };
        
        return ResponseEntity.ok()
            .header("Content-Disposition", 
                "attachment; filename=hivecloud-api-doc." + format)
            .body(content);
    }
    
    private String convertToYaml(Map<String, Object> docs) {
        // JSON转YAML实现
        return YamlUtils.toJson(docs);
    }
    
    private String convertToMarkdown(Map<String, Object> docs) {
        // JSON转Markdown实现（Knife4j支持）
        return MarkdownUtils.fromOpenApi(docs);
    }
}
```

**文档访问过滤器**：

```java
// 文档访问过滤器
@Component
public class DocAccessFilter implements Filter {
    
    @Autowired
    private DocProperties docProperties;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                         FilterChain chain) throws IOException, ServletException {
        if (!docProperties.isEnabled()) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(403);
            httpResponse.getWriter().write("文档功能已禁用");
            return;
        }
        
        // 生产环境可配置IP白名单
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String remoteIp = httpRequest.getRemoteAddr();
        
        // 检查IP白名单（如果配置了）
        chain.doFilter(request, response);
    }
}
```

**Maven依赖配置**：

```xml
<!-- hivecloud-plugin-doc/pom.xml -->
<dependencies>
    <!-- Knife4j OpenAPI 3 (Spring Boot 3.x使用jakarta) -->
    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
        <version>4.5.0</version>
    </dependency>
    
    <!-- YAML处理 -->
    <dependency>
        <groupId>com.fasterxml.jackson.dataformat</groupId>
        <artifactId>jackson-dataformat-yaml</artifactId>
    </dependency>
    
    <!-- 插件引擎 -->
    <dependency>
        <groupId>com.hivecloud</groupId>
        <artifactId>hivecloud-plugin-engine</artifactId>
    </dependency>
    
    <!-- 公共组件 -->
    <dependency>
        <groupId>com.hivecloud</groupId>
        <artifactId>hivecloud-common-core</artifactId>
    </dependency>
    <dependency>
        <groupId>com.hivecloud</groupId>
        <artifactId>hivecloud-common-redis</artifactId>
    </dependency>
</dependencies>
```

**配置文件示例**：

```yaml
# application.yml
hivecloud:
  doc:
    enabled: true                                    # 是否启用文档
    title: "HiveCloud API文档"                       # 文档标题
    description: "HiveCloud轻量微服务脚手架API文档"   # 文档描述
    version: "v1.0.0"                                # 文档版本
    aggregation-enabled: true                        # 是否启用文档聚合
    service-urls:                                    # 聚合的服务URL列表
      - http://localhost:8081                        # system服务
      - http://localhost:8082                        # pay服务
      - http://localhost:8083                        # claim服务
    cache-timeout: 300                               # 缓存超时时间（秒）
    basic-auth-enabled: false                        # 是否启用BasicAuth
    basic-auth-username: admin                       # BasicAuth用户名
    basic-auth-password: 123456                      # BasicAuth密码

# SpringDoc配置（Knife4j基于SpringDoc）
springdoc:
  api-docs:
    path: /v3/api-docs                               # API文档路径
    enabled: true                                    # 是否启用
  swagger-ui:
    path: /swagger-ui.html                           # Swagger UI路径
    enabled: true                                    # 是否启用

# Knife4j增强配置
knife4j:
  enable: true                                       # 是否启用Knife4j增强
  setting:
    language: zh_cn                                  # 语言设置（中文）
    swagger-model-name: 实体类列表                    # 实体类列表菜单名称
    enable-footer: false                             # 是否显示底部
    enable-footer-custom: false                      # 是否显示自定义底部
    enable-home-custom: true                         # 是否启用自定义首页
    enable-search: true                              # 是否启用搜索
    enable-open-api: true                            # 是否启用OpenAPI
    enable-group: true                               # 是否启用分组
    enable-host: false                               # 是否启用Host
    enable-host-text: ""                             # Host文本
    enable-dynamic-parameter: true                   # 是否启用动态参数
    enable-debug: true                               # 是否启用调试
    enable-cache: true                               # 是否启用缓存
    enable-response-code: true                       # 是否启用响应码
    enable-document-manage: true                     # 是否启用文档管理
    enable-security: false                           # 是否启用安全配置
    enable-version: true                             # 是否启用版本控制
    enable-merge: false                              # 是否启用合并
    enable-filter-multipart-apis: false              # 是否过滤Multipart APIs
    enable-filter-multipart-api-method-type: ""      # 过滤类型
    enable-reload: true                              # 是否启用重载
    enable-footer-custom-html: ""                    # 自定义底部HTML
    enable-home-custom-html: ""                      # 自定义首页HTML
    enable-swagger-models: true                      # 是否启用实体类展示
    enable-api-sort: true                            # 是否启用API排序
    enable-show-api: true                            # 是否显示API
    enable-enable-filter-api: true                   # 是否启用过滤API
    enable-enable-filter-api-authority: false        # 是否启用过滤API权限
    enable-enable-filter-api-authority-text: ""      # 权限文本
```

**Knife4j优势说明**：

| 特性 | Knife4j | SpringDoc/Swagger |
|------|---------|-------------------|
| UI界面 | 美观的中文界面，支持深色模式 | 原生英文界面 |
| 离线文档 | 支持导出Markdown/HTML | 仅支持JSON/YAML |
| 调试功能 | 增强调试，支持参数自动填充 | 基础调试功能 |
| 文档搜索 | 支持全文搜索 | 不支持 |
| 实体展示 | 独立实体类列表展示 | 混合在API中 |
| 中文支持 | 原生中文，配置简单 | 需要额外配置 |
| 文档聚合 | 支持多服务文档聚合 | 需要额外开发 |
| 权限控制 | 内置BasicAuth等认证 | 需要额外开发 |

### 6.4 Maven 多环境配置规范

#### 6.4.1 父POM配置（hivecloud-ms/pom.xml）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.hivecloud</groupId>
    <artifactId>hivecloud-ms</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    <name>HiveCloud MicroService Scaffold</name>
    <description>轻量微服务脚手架</description>

    <modules>
        <module>hivecloud-dependencies</module>
        <module>hivecloud-common</module>
        <module>hivecloud-framework</module>
        <module>hivecloud-gateway</module>
        <module>hivecloud-modules</module>
        <module>hivecloud-services</module>
        <module>hivecloud-plugins</module>
        <module>hivecloud-examples</module>
    </modules>

    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>${java.version}</maven.compiler.source>
        <maven.compiler.target>${java.version}</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        
        <!-- Spring Boot -->
        <spring-boot.version>3.2.0</spring-boot.version>
        <!-- Spring Cloud -->
        <spring-cloud.version>2023.0.0</spring-cloud.version>
        <!-- Spring Cloud Alibaba -->
        <spring-cloud-alibaba.version>2023.0.0.0-RC1</spring-cloud-alibaba.version>
        
        <!-- 数据库 -->
        <mysql.version>8.0.33</mysql.version>
        <mybatis-plus.version>3.5.5</mybatis-plus.version>
        <druid.version>1.2.20</druid.version>
        
        <!-- 缓存 -->
        <redis.version>3.2.0</redis.version>
        <caffeine.version>3.1.8</caffeine.version>
        
        <!-- 工具类 -->
        <hutool.version>5.8.22</hutool.version>
        <lombok.version>1.18.30</lombok.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
        
        <!-- 测试 -->
        <junit.version>5.10.1</junit.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- Spring Boot BOM -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            
            <!-- Spring Cloud BOM -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            
            <!-- Spring Cloud Alibaba BOM -->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>${spring-cloud-alibaba.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            
            <!-- HiveCloud BOM -->
            <dependency>
                <groupId>com.hivecloud</groupId>
                <artifactId>hivecloud-dependencies</artifactId>
                <version>${project.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>${spring-boot.version}</version>
                    <executions>
                        <execution>
                            <goals>
                                <goal>repackage</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.11.0</version>
                    <configuration>
                        <source>${java.version}</source>
                        <target>${java.version}</target>
                        <encoding>${project.build.sourceEncoding}</encoding>
                        <annotationProcessorPaths>
                            <path>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok</artifactId>
                                <version>${lombok.version}</version>
                            </path>
                            <path>
                                <groupId>org.mapstruct</groupId>
                                <artifactId>mapstruct-processor</artifactId>
                                <version>${mapstruct.version}</version>
                            </path>
                        </annotationProcessorPaths>
                    </configuration>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>

    <!-- 多环境Profile -->
    <profiles>
        <profile>
            <id>dev</id>
            <properties>
                <profileActive>dev</profileActive>
            </properties>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
        </profile>
        <profile>
            <id>test</id>
            <properties>
                <profileActive>test</profileActive>
            </properties>
        </profile>
        <profile>
            <id>prod</id>
            <properties>
                <profileActive>prod</profileActive>
            </properties>
        </profile>
    </profiles>
</project>
```

#### 6.4.2 子模块POM配置示例（hivecloud-modules/hivecloud-module-system/pom.xml）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.hivecloud</groupId>
        <artifactId>hivecloud-modules</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>hivecloud-module-system</artifactId>
    <packaging>jar</packaging>
    <name>HiveCloud Module - System</name>
    <description>系统管理模块</description>

    <dependencies>
        <!-- 公共组件 -->
        <dependency>
            <groupId>com.hivecloud</groupId>
            <artifactId>hivecloud-common-core</artifactId>
        </dependency>
        <dependency>
            <groupId>com.hivecloud</groupId>
            <artifactId>hivecloud-common-security</artifactId>
        </dependency>
        <dependency>
            <groupId>com.hivecloud</groupId>
            <artifactId>hivecloud-common-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>com.hivecloud</groupId>
            <artifactId>hivecloud-common-mybatis</artifactId>
        </dependency>
        <dependency>
            <groupId>com.hivecloud</groupId>
            <artifactId>hivecloud-common-web</artifactId>
        </dependency>
        
        <!-- 核心框架 -->
        <dependency>
            <groupId>com.hivecloud</groupId>
            <artifactId>hivecloud-plugin-engine</artifactId>
        </dependency>
        <dependency>
            <groupId>com.hivecloud</groupId>
            <artifactId>hivecloud-service-registry</artifactId>
        </dependency>
        <dependency>
            <groupId>com.hivecloud</groupId>
            <artifactId>hivecloud-heartbeat</artifactId>
        </dependency>
        <dependency>
            <groupId>com.hivecloud</groupId>
            <artifactId>hivecloud-fault-removal</artifactId>
        </dependency>
        <dependency>
            <groupId>com.hivecloud</groupId>
            <artifactId>hivecloud-gossip-sync</artifactId>
        </dependency>
        
        <!-- 插件 -->
        <dependency>
            <groupId>com.hivecloud</groupId>
            <artifactId>hivecloud-plugin-auth</artifactId>
        </dependency>
        <dependency>
            <groupId>com.hivecloud</groupId>
            <artifactId>hivecloud-plugin-log</artifactId>
        </dependency>
        <dependency>
            <groupId>com.hivecloud</groupId>
            <artifactId>hivecloud-plugin-dict</artifactId>
        </dependency>
        <dependency>
            <groupId>com.hivecloud</groupId>
            <artifactId>hivecloud-plugin-cache</artifactId>
        </dependency>
        
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- Nacos -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>
        
        <!-- 数据库 -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-3-starter</artifactId>
        </dependency>
        
        <!-- 工具类 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>
        
        <!-- 测试 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
        
        <!-- 多环境资源过滤 -->
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>true</filtering>
                <includes>
                    <include>application.yml</include>
                    <include>application-${profileActive}.yml</include>
                </includes>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>false</filtering>
                <excludes>
                    <exclude>application*.yml</exclude>
                </excludes>
            </resource>
        </resources>
    </build>
</project>
```

#### 6.4.3 多环境配置文件示例

**application.yml（主配置文件）**
```yaml
spring:
  application:
    name: hivecloud-module-system
  profiles:
    active: @profileActive@  # Maven Profile动态替换
```

**application-dev.yml（开发环境）**
```yaml
server:
  port: 8081

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/hivecloud_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      connection-timeout: 30000
  
  data:
    redis:
      host: localhost
      port: 6379
      password: 
      database: 0

# Nacos配置
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: dev
        group: DEFAULT_GROUP
      config:
        server-addr: localhost:8848
        namespace: dev
        group: DEFAULT_GROUP
        file-extension: yaml

# 日志配置
logging:
  level:
    root: INFO
    com.hivecloud: DEBUG
  pattern:
    console: '%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n'
```

**application-prod.yml（生产环境）**
```yaml
server:
  port: 8081

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${DB_HOST:192.168.1.100}:3306/hivecloud_system?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai
    username: ${DB_USER:hivecloud}
    password: ${DB_PASSWORD:your_password}
    hikari:
      minimum-idle: 10
      maximum-pool-size: 50
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  data:
    redis:
      host: ${REDIS_HOST:192.168.1.101}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:your_redis_password}
      database: 0
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5

# Nacos配置
spring:
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_HOST:192.168.1.102}:8848
        namespace: prod
        group: DEFAULT_GROUP
      config:
        server-addr: ${NACOS_HOST:192.168.1.102}:8848
        namespace: prod
        group: DEFAULT_GROUP
        file-extension: yaml

# 日志配置
logging:
  level:
    root: WARN
    com.hivecloud: INFO
  file:
    name: /data/logs/hivecloud-module-system.log
    max-size: 100MB
    max-history: 30
```

### 6.5 资源分配建议

| 角色 | 人数 | 负责阶段 | 主要职责 |
|------|------|---------|---------|
| 架构师 | 1 | 全阶段 | 架构设计、技术选型、代码审查 |
| 后端开发 | 2-3 | 第一/二/三阶段 | 核心模块开发、服务治理、业务逻辑 |
| 前端开发 | 1 | 第二/四阶段 | 管理后台、示例项目前端 |
| 测试工程师 | 1 | 第二/三/四阶段 | 单元测试、集成测试、压力测试 |
| 运维工程师 | 1 | 第四阶段 | 部署脚本、监控告警、文档编写 |

---

## 7. 测试策略

### 7.1 测试层次

```
┌─────────────────────────────────────────────────────────────┐
│ L4: 端到端测试 (E2E Testing)                                  │
│ 完整业务流程验证，模拟真实用户操作                              │
├─────────────────────────────────────────────────────────────┤
│ L3: 集成测试 (Integration Testing)                            │
│ 模块间交互验证，服务间调用验证                                  │
├─────────────────────────────────────────────────────────────┤
│ L2: 单元测试 (Unit Testing)                                   │
│ 单个类/方法验证，Mock外部依赖                                  │
├─────────────────────────────────────────────────────────────┤
│ L1: 静态分析 (Static Analysis)                                │
│ 代码规范检查、安全漏洞扫描、依赖检查                            │
└─────────────────────────────────────────────────────────────┘
```

### 7.2 测试工具链

| 测试类型 | 工具 | 用途 |
|---------|------|------|
| 单元测试 | JUnit 5 + Mockito | 单元测试框架 + Mock |
| 集成测试 | SpringBootTest + TestContainers | 集成测试 + 容器化测试环境 |
| 接口测试 | Postman/Apifox | API接口测试 |
| 压力测试 | JMeter | 并发压力测试 |
| 代码覆盖 | JaCoCo | 代码覆盖率统计 |
| 静态分析 | SonarQube | 代码质量检查 |
| 安全扫描 | OWASP Dependency-Check | 依赖漏洞扫描 |

### 7.3 测试覆盖率目标

| 模块类型 | 单元测试覆盖率 | 集成测试覆盖率 | 关键路径覆盖率 |
|---------|--------------|---------------|---------------|
| 核心框架 (hivecloud-framework) | >= 80% | >= 70% | 100% |
| 网关 (hivecloud-gateway) | >= 75% | >= 65% | 100% |
| 模块化单体 (hivecloud-modules) | >= 70% | >= 60% | 100% |
| 独立微服务 (hivecloud-services) | >= 70% | >= 60% | 100% |
| 插件 (hivecloud-plugins) | >= 75% | >= 65% | 100% |

### 7.4 核心测试场景

#### 7.4.1 服务治理测试

| 测试场景 | 测试方法 | 预期结果 |
|---------|---------|---------|
| 服务注册 | 启动服务节点 | Redis中正确存储元数据，TTL=6s |
| 心跳检测 | 模拟服务正常运行 | 每秒刷新心跳，本地缓存健康状态 |
| 主动下线 | 正常关闭服务 | 100ms内集群删除本地缓存 |
| 被动剔除 | 模拟服务宕机 | 5s内自动剔除故障节点 |
| 调用兜底 | 模拟连接超时 | 立即加入黑名单，10s内不重试 |
| 集群同步 | 新增/下线节点 | 同步延迟 < 1s |

#### 7.4.2 网关测试

| 测试场景 | 测试方法 | 预期结果 |
|---------|---------|---------|
| JWT鉴权 | 携带/不携带Token访问 | 正确鉴权，非法请求拒绝 |
| 限流测试 | 并发请求超过阈值 | 触发限流，返回429 |
| 路由转发 | 访问不同服务接口 | 正确路由到目标服务 |
| 黑名单过滤 | 黑名单IP访问 | 直接拒绝 |
| 跨域处理 | 跨域请求 | 正确返回CORS头 |

#### 7.4.3 双轨链路测试

| 测试场景 | 测试方法 | 预期结果 |
|---------|---------|---------|
| 快轨命中 | 访问@FastTrack接口，缓存命中 | 响应时间 5-15ms |
| 快轨降级 | 访问@FastTrack接口，缓存未命中 | 自动降级到稳轨 |
| 稳轨执行 | 访问@StableTrack接口 | 响应时间 50-200ms，事务一致 |

#### 7.4.4 压力测试指标

| 指标 | 目标值 | 测试条件 |
|------|--------|---------|
| 网关QPS | >= 8000/节点 | 单节点，8C16G |
| 服务间调用RT | <= 50ms | 内网直连，100并发 |
| 快轨响应时间 | 5-15ms | 缓存命中 |
| 稳轨响应时间 | <= 200ms | 数据库操作 |
| 故障剔除时间 | <= 5s | 模拟宕机 |
| 集群同步延迟 | 3-10s | 10节点集群 |

---

## 8. 质量标准与验收准则

### 8.1 代码质量标准

| 标准 | 要求 | 检查工具 |
|------|------|---------|
| 代码规范 | 遵循阿里巴巴Java开发手册 | SonarQube + Checkstyle |
| 注释规范 | 公共方法必须有JavaDoc | Checkstyle |
| 命名规范 | 遵循驼峰命名，包名全小写 | Checkstyle |
| 复杂度控制 | 方法圈复杂度 <= 10 | SonarQube |
| 重复代码 | 重复率 <= 3% | SonarQube |
| 依赖管理 | 无循环依赖，版本统一 | Maven Enforcer |

### 8.2 性能标准

| 指标 | 目标值 | 测试环境 |
|------|--------|---------|
| 启动时间 | <= 10s | 单节点，4C8G |
| 内存占用 | <= 512MB/节点 | 单节点，4C8G |
| 部署包体积 | <= 50MB | 打包后 |
| 网关QPS | >= 10000/节点 | 8C16G |
| 服务间调用RT | <= 50ms | 内网直连 |
| 可用性 | >= 99.99% | 7*24小时运行 |

### 8.3 验收准则

#### 8.3.1 功能验收

- [ ] 服务注册/发现功能正常
- [ ] 心跳检测/故障剔除功能正常
- [ ] 集群同步功能正常（延迟 < 1s）
- [ ] 网关鉴权/限流/路由功能正常
- [ ] 双轨链路自动路由功能正常
- [ ] 插件热插拔功能正常
- [ ] 模块化单体与微服务切换功能正常

#### 8.3.2 性能验收

- [ ] 网关单节点QPS >= 10000
- [ ] 快轨响应时间 <= 5ms
- [ ] 稳轨响应时间 <= 200ms
- [ ] 故障剔除时间 <= 5s
- [ ] 集群同步延迟 <= 1s
- [ ] 7*24小时运行无内存泄漏

#### 8.3.3 可用性验收

- [ ] 单节点故障不影响全局服务
- [ ] Redis短暂故障，本地缓存可兜底
- [ ] 服务优雅下线，无请求丢失
- [ ] 自动扩容节点，秒级纳入负载

#### 8.3.4 文档验收

- [ ] 完整的架构设计文档
- [ ] 完整的API接口文档
- [ ] 完整的部署文档
- [ ] 完整的插件开发文档
- [ ] 完整的二次开发指南
- [ ] 示例项目代码

---

## 9. 风险评估

### 9.1 技术风险

| 风险项 | 风险等级 | 影响 | 缓解措施 |
|--------|---------|------|---------|
| Gossip协议同步延迟 | 中 | 集群状态不一致 | 增加邻居节点数量，优化同步算法 |
| Redis单点故障 | 中 | 元数据/心跳存储失效 | 本地缓存兜底，Redis集群部署 |
| 心跳误判（网络抖动） | 低 | 健康节点被误剔除 | 3s不健康标记，5s才剔除 |
| 插件兼容性问题 | 中 | 插件加载失败 | 严格SPI接口规范，版本管理 |
| 网关性能瓶颈 | 中 | 高并发场景响应慢 | 网关集群部署，内网直连分流 |
| 数据库连接池耗尽 | 中 | 服务不可用 | 合理配置连接池，慢查询优化 |

### 9.2 项目风险

| 风险项 | 风险等级 | 影响 | 缓解措施 |
|--------|---------|------|---------|
| 开发周期延长 | 中 | 交付延期 | 分阶段交付，优先P0模块 |
| 人员变动 | 中 | 开发进度受影响 | 完善文档，代码审查机制 |
| 需求变更 | 低 | 架构调整 | 模块化设计，灵活扩展 |
| 测试不充分 | 高 | 生产环境故障 | 严格测试流程，自动化测试 |

### 9.3 运维风险

| 风险项 | 风险等级 | 影响 | 缓解措施 |
|--------|---------|------|---------|
| 部署配置错误 | 中 | 服务启动失败 | 配置校验，部署脚本自动化 |
| 监控缺失 | 中 | 故障发现不及时 | 完善监控告警体系 |
| 日志不规范 | 低 | 问题排查困难 | 统一日志格式，集中收集 |

### 9.4 风险应对策略

```
高风险: 立即处理，制定专项应对方案
中风险: 持续监控，准备应急预案
低风险: 定期评估，保持关注
```

---

## 10. 交付时间表

### 10.1 总体时间规划

```
阶段一 (核心底座)     阶段二 (能力增强)     阶段三 (微服务扩展)     阶段四 (生产就绪)
├─────────────────┼─────────────────┼─────────────────┼─────────────────┤
│   4-5 周         │   3-4 周         │   4-5 周         │   2-3 周         │
│                 │                 │                 │                 │
│ T1.1-T1.12      │ T2.1-T2.7       │ T3.1-T3.9       │ T4.1-T4.5       │
│ 核心框架+网关    │ 模块化单体增强   │ 独立微服务+插件  │ 监控+文档+发布  │
└─────────────────┴─────────────────┴─────────────────┴─────────────────┘
```

### 10.2 详细交付里程碑

| 里程碑 | 交付内容 | 预计时间 | 验收标准 |
|--------|---------|---------|---------|
| M1: 核心底座完成 | 服务治理+网关+system模块 | 第5周 | 服务可注册/发现/心跳/剔除 |
| M2: 基础能力完成 | 插件引擎+4个核心插件 | 第6周 | 插件可热插拔，功能正常 |
| M3: 混合部署完成 | file/job模块+双轨链路+事件总线 | 第9周 | 模块化单体可运行 |
| M4: 微服务完成 | 4个独立微服务+3个扩展插件 | 第13周 | 微服务可独立部署/扩展 |
| M5: 生产就绪 | 监控+文档+部署脚本+示例 | 第16周 | 达到生产可用标准 |

### 10.3 关键路径

```
项目启动 → T1.1项目骨架 → T1.2基础依赖 → T1.3元数据注册 → T1.4心跳探测
    → T1.5故障剔除 → T1.6集群同步 → T1.7系统管理 → T1.8权限插件
    → T1.12网关 → M1里程碑
    → T2.1文件管理 → T2.2定时任务 → T2.3直连通道 → T2.4双轨链路
    → T2.5事件总线 → T2.6混合部署 → M3里程碑
    → T3.1支付服务 → T3.2理赔服务 → T3.3活动服务 → T3.4通知服务
    → M4里程碑
    → T4.1监控 → T4.2部署脚本 → T4.3文档 → T4.5开源发布 → M5里程碑
```

### 10.4 资源投入估算

| 阶段 | 人力投入 | 时间周期 | 主要产出 |
|------|---------|---------|---------|
| 第一阶段 | 3人 | 4-5周 | 核心框架、网关、系统管理 |
| 第二阶段 | 3人 | 3-4周 | 模块化单体、双轨链路、事件总线 |
| 第三阶段 | 4人 | 4-5周 | 独立微服务、扩展插件 |
| 第四阶段 | 2人 | 2-3周 | 监控、文档、部署脚本 |
| **合计** | **3-4人** | **13-17周** | **完整可交付系统** |

---

## 附录

### A. 核心配置参数

#### A.1 心跳与元数据配置

| 参数 | 默认值 | 说明 | 可配置 |
|------|--------|------|--------|
| 心跳间隔 | 1s | 服务心跳推送间隔 | 是 |
| 不健康阈值 | 3s | 标记不健康的时间阈值 | 是 |
| 剔除阈值 | 4s | 确认故障并剔除的时间阈值 | 是 |
| 元数据TTL | 30s | 服务元数据Key的过期时间 | 是 |
| 心跳TTL | 5s | 心跳Key的过期时间（与剔除阈值分离） | 是 |

**心跳Key与元数据Key分离设计**：

| Key类型 | TTL | 说明 |
|---------|-----|------|
| 元数据Key | 30s | 服务基础信息（IP、端口、版本等） |
| 心跳Key | 5s | 心跳时间戳，用于故障检测 |

```yaml
hivecloud:
  heartbeat:
    interval: 1000              # 心跳间隔1s
    unhealthy-threshold: 3000   # 3s标记不健康
    remove-threshold: 4000      # 4s剔除
  redis:
    metadata-ttl: 30000         # 元数据30s
    heartbeat-ttl: 5000         # 心跳5s
```

#### A.2 其他核心配置

| 参数 | 默认值 | 说明 | 可配置 |
|------|--------|------|--------|
| 网关QPS限制 | 8000 | 单节点QPS限制 | 是 |
| 直连连接池 | 50 | OkHttp最大连接数 | 是 |
| 直连超时 | 1000ms | HTTP直连超时时间 | 是 |
| 黑名单有效期 | 10s | 故障节点黑名单有效期 | 是 |
| Gossip邻居数 | 3-5 | 集群同步邻居节点数量 | 是 |
| Gossip同步间隔 | 3s | 集群同步间隔 | 是 |
| Gossip重试次数 | 3 | 同步消息重试次数 | 是 |
| 集群同步延迟 | 3-10s | Gossip协议最终一致性收敛时间 | 是 |

### B. 接口路径规范

| 类型 | 路径格式 | 示例 | 说明 |
|------|---------|------|------|
| 公网接口 | `/api/{服务标识}/v1/{接口路径}` | `/api/pay/v1/createOrder` | 经过网关，需鉴权 |
| 内网直连 | `/direct/{服务标识}/v1/{接口路径}` | `/direct/system/v1/getUser` | 内网访问，无需网关鉴权 |
| 健康检查 | `/actuator/health` | - | SpringBoot Actuator |
| 指标暴露 | `/actuator/prometheus` | - | Prometheus指标 |

### C. 标准化响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1714000000000
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码（200成功，400客户端错误，500服务端错误） |
| message | string | 响应消息 |
| data | object | 响应数据 |
| timestamp | long | 响应时间戳 |

### D. 插件开发模板

```java
package com.hivecloud.plugin.xxx;

import com.hivecloud.plugin.Plugin;

/**
 * XXX插件
 */
public class XxxPlugin implements Plugin {
    
    @Override
    public void initialize() {
        // 插件初始化逻辑
    }
    
    @Override
    public void start() {
        // 插件启动逻辑
    }
    
    @Override
    public void destroy() {
        // 插件销毁逻辑
    }
}
```

### E. 部署步骤

```bash
# 1. 修改配置文件
vim application.yml

# 2. 打包
mvn clean package -DskipTests

# 3. 启动服务
java -jar hivecloud-module-system.jar --spring.profiles.active=prod

# 4. 验证服务
curl http://localhost:8080/actuator/health

# 5. 查看日志
tail -f logs/hivecloud-module-system.log
```

### F. 配置优先级定义

配置加载优先级（高 → 低）：

| 优先级 | 配置来源 | 说明 | 示例 |
|--------|---------|------|------|
| 1 | 环境变量 / JVM参数 | 最高优先级，覆盖所有配置 | `-Dhivecloud.heartbeat.interval=2000` |
| 2 | Nacos配置中心 | 动态配置，支持热更新 | `hivecloud.heartbeat.remove-threshold=5000` |
| 3 | application-{profile}.yml | 环境专属配置 | `application-prod.yml` |
| 4 | application.yml | 默认配置，兜底 | 项目默认值 |

**配置冲突处理原则**：
- 高优先级配置覆盖低优先级配置
- 同优先级配置，后加载的覆盖先加载的
- Nacos配置变更时，自动触发配置热更新（无需重启服务）

### G. 服务调用重试与熔断设计

#### G.1 重试机制

```java
@Retryable(
    value = {TimeoutException.class, ConnectException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 100, multiplier = 2, maxDelay = 1000)
)
public Result<User> getUserById(Long userId) {
    return okHttpTemplate.get("/direct/system/v1/user/{id}", userId);
}
```

**重试策略**：

| 参数 | 默认值 | 说明 |
|------|--------|------|
| 最大重试次数 | 3 | 包含首次调用，最多尝试3次 |
| 初始延迟 | 100ms | 第一次重试前的等待时间 |
| 延迟倍数 | 2 | 每次重试延迟翻倍（100ms → 200ms → 400ms） |
| 最大延迟 | 1000ms | 重试延迟上限 |
| 可重试异常 | TimeoutException, ConnectException | 仅网络异常时重试 |

#### G.2 熔断机制

```java
@CircuitBreaker(
    failureThreshold = 5,
    timeout = 3000,
    windowSize = 10
)
public Result<Order> createOrder(OrderDTO orderDTO) {
    return okHttpTemplate.post("/direct/pay/v1/order", orderDTO);
}
```

**熔断状态机**：

| 状态 | 触发条件 | 行为 |
|------|---------|------|
| 关闭（Closed） | 正常状态 | 正常调用 |
| 打开（Open） | 10次调用中5次失败 | 直接返回失败，不调用下游 |
| 半开（Half-Open） | 打开3秒后 | 允许1次试探调用，成功则关闭，失败则继续打开 |

**熔断配置**：

| 参数 | 默认值 | 说明 |
|------|--------|------|
| 失败阈值 | 5 | 窗口期内失败次数达到此值则熔断 |
| 窗口大小 | 10 | 统计窗口内的调用次数 |
| 熔断超时 | 3000ms | 熔断后等待时间，之后进入半开状态 |
| 慢调用阈值 | 2000ms | 超过此时间视为慢调用，计入失败 |

### H. 服务间认证机制

#### H.1 分阶段实施方案

| 阶段 | 方案 | 说明 | 安全级别 |
|------|------|------|---------|
| 第一版 | 内网IP白名单 | 小规模集群可用，配置简单 | ⭐⭐ |
| 后续 | 服务间JWT Token | 增加身份认证，支持细粒度权限 | ⭐⭐⭐ |
| 企业版 | mTLS | 双向TLS认证，高安全要求 | ⭐⭐⭐⭐⭐ |

#### H.2 内网IP白名单（第一版）

```yaml
hivecloud:
  security:
    ip-whitelist:
      enabled: true
      allowed-ips:
        - 10.0.0.0/8      # 内网A类地址
        - 172.16.0.0/12   # 内网B类地址
        - 192.168.0.0/16  # 内网C类地址
```

#### H.3 服务间JWT Token（后续版本）

```java
// 服务间调用时自动附加JWT Token
@Component
public class ServiceAuthInterceptor implements ClientHttpRequestInterceptor {
    
    @Autowired
    private JwtTokenService jwtTokenService;
    
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) {
        // 生成服务间调用Token
        String token = jwtTokenService.generateServiceToken(
            "service-a",  // 调用方
            "service-b",  // 被调用方
            System.currentTimeMillis() + 60000  // 1分钟有效期
        );
        
        // 附加到请求头
        request.getHeaders().set("X-Service-Token", token);
        return execution.execute(request, body);
    }
}
```

**Token结构**：

| 字段 | 说明 | 示例 |
|------|------|------|
| iss | 签发方（调用方服务标识） | `service-a` |
| sub | 接收方（被调用方服务标识） | `service-b` |
| exp | 过期时间 | 1分钟后 |
| iat | 签发时间 | 当前时间戳 |
| jti | Token唯一标识 | UUID |

---

## 总结

HiveCloud 轻量微服务脚手架通过**轻量中心化治理**、**混合部署模型**、**插件化扩展**和**自愈自治**四大核心能力，解决了传统微服务架构中间件过重、运维复杂、单点风险等痛点。

本项目实现分析报告基于完整的架构设计文档和三张架构设计图，制定了详细的开发计划，包括：

1. **4个开发阶段**：核心底座 → 能力增强 → 微服务扩展 → 生产就绪
2. **25+个功能模块**：覆盖服务治理、业务逻辑、插件扩展等全场景
3. **完整的测试策略**：单元测试、集成测试、压力测试全覆盖
4. **明确的质量标准**：代码质量、性能指标、可用性要求
5. **13-17周的交付周期**：分阶段交付，降低项目风险

通过本方案的实施，可确保 HiveCloud 达到**生产可用**标准，为中小团队提供一套**轻量、高效、可落地**的微服务脚手架。
