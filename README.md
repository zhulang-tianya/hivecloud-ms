# HiveCloud 轻量微服务脚手架

HiveCloud 是一个基于 Spring Boot 3.x + Spring Cloud Alibaba 的轻量级微服务脚手架，采用**轻量中心化治理架构**，支持**模块化单体 + 轻量微服务混合部署**模式。

## 架构特点

- **轻量中心化治理**: 基于 Redis 的轻量级服务注册与发现
- **双模式流量接入**: 统一业务网关 + 域内直连通道
- **插件化架构**: 基于 SPI 标准接口的插件引擎
- **故障剔除三重机制**: 心跳探测 + 主动剔除 + Gossip 同步
- **双轨业务链路**: 快轨（缓存命中）+ 稳轨（数据库操作）

## 项目结构

```
hivecloud-ms/
├── hivecloud-dependencies          # 统一依赖管理 BOM
├── hivecloud-common                # 公共组件模块
│   ├── hivecloud-common-core       # 核心工具类
│   ├── hivecloud-common-security   # 安全组件
│   ├── hivecloud-common-redis      # Redis组件
│   ├── hivecloud-common-mybatis    # MyBatis组件
│   ├── hivecloud-common-web        # Web组件
│   └── hivecloud-common-test       # 测试工具
├── hivecloud-framework             # 核心框架模块
│   ├── hivecloud-plugin-engine     # 插件引擎
│   ├── hivecloud-service-registry  # 轻量元数据注册模块
│   ├── hivecloud-heartbeat         # 分布式心跳探测模块
│   ├── hivecloud-fault-removal     # 故障剔除三重机制
│   ├── hivecloud-gossip-sync       # 轻量集群同步模块
│   ├── hivecloud-direct-connector  # 域内直连通道
│   ├── hivecloud-business-link     # 双轨业务链路
│   └── hivecloud-event-bus         # 事件总线
├── hivecloud-gateway               # 统一业务网关
├── hivecloud-modules               # 模块化单体
│   └── hivecloud-module-system     # 系统管理模块（含控制台）
├── hivecloud-services              # 独立微服务
│   ├── hivecloud-service-pay       # 支付服务
│   ├── hivecloud-service-claim     # 理赔服务
│   ├── hivecloud-service-activity  # 活动服务
│   └── hivecloud-service-notice    # 通知服务
└── hivecloud-plugins               # 插件库
    ├── hivecloud-plugin-auth       # 权限插件
    ├── hivecloud-plugin-log        # 日志插件
    ├── hivecloud-plugin-dict       # 字典插件
    ├── hivecloud-plugin-cache      # 缓存插件
    ├── hivecloud-plugin-file       # 文件插件
    ├── hivecloud-plugin-job        # 定时任务插件
    ├── hivecloud-plugin-doc        # 在线文档插件
    ├── hivecloud-plugin-ai         # AI插件
    ├── hivecloud-plugin-encrypt    # 加密插件
    └── hivecloud-plugin-message    # 消息插件
```

## 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 基础运行环境 |
| Spring Boot | 3.2.4 | 应用框架 |
| Spring Cloud | 2023.0.1 | 微服务框架 |
| Spring Cloud Alibaba | 2023.0.1.0 | 阿里微服务套件 |
| MyBatis Plus | 3.5.5 | ORM 框架 |
| MySQL | 8.0.33 | 关系型数据库 |
| Redis | 3.2.4 | 缓存/元数据存储 |
| Caffeine | 3.1.8 | 本地缓存 |
| Knife4j | 4.4.0 | API 文档 |
| OkHttp | 4.12.0 | HTTP 客户端 |
| Hutool | 5.8.25 | Java 工具库 |
| Guava | 33.0.0-jre | Google 工具库 |
| MapStruct | 1.5.5.Final | 对象映射 |
| Lombok | 1.18.30 | 代码简化 |

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

### 构建项目

```bash
# 克隆项目
git clone <repository-url>
cd hivecloud-ms

# 编译打包
mvn clean install -DskipTests

# 指定环境打包（默认 dev）
mvn clean install -Pdev
mvn clean install -Ptest
mvn clean install -Pprod
```

### 运行服务

```bash
# 运行系统管理模块
cd hivecloud-modules/hivecloud-module-system
mvn spring-boot:run

# 运行网关
cd hivecloud-gateway
mvn spring-boot:run

# 运行支付服务
cd hivecloud-services/hivecloud-service-pay
mvn spring-boot:run
```

## 多环境配置

项目支持三种环境配置：

| 环境 | Profile | 说明 |
|------|---------|------|
| 开发环境 | dev | 本地开发，默认激活 |
| 测试环境 | test | 集成测试 |
| 生产环境 | prod | 生产部署 |

配置优先级：环境变量/JVM参数 > Nacos > application-{profile}.yml > application.yml

## 插件开发

### 创建新插件

1. 在 `hivecloud-plugins` 目录下创建新模块
2. 继承 `hivecloud-plugin-engine` 依赖
3. 实现 `Plugin` 接口
4. 在 `META-INF/services` 中注册 SPI

### 插件示例

```java
@HiveCloudPlugin(name = "my-plugin", version = "1.0.0")
public class MyPlugin implements Plugin {
    
    @Override
    public void init() {
        // 初始化逻辑
    }
    
    @Override
    public void destroy() {
        // 销毁逻辑
    }
}
```

## 性能指标

| 指标 | 目标值 | 测试条件 |
|------|--------|---------|
| 网关QPS | >= 8000/节点 | 单节点，8C16G |
| 服务间调用RT | <= 50ms | 内网直连，100并发 |
| 快轨响应时间 | 5-15ms | 缓存命中 |
| 稳轨响应时间 | <= 200ms | 数据库操作 |
| 故障剔除时间 | <= 5s | 模拟宕机 |
| 集群同步延迟 | 3-10s | 10节点集群 |

## 文档

- [架构设计文档](../architecture-diagrams/hivecloud-architecture.html)
- [服务流程图](../architecture-diagrams/hivecloud-service-flow.html)
- [部署架构图](../architecture-diagrams/hivecloud-deployment.html)
- [实施报告](../architecture-diagrams/IMPLEMENTATION_REPORT.md)
- [开发计划](../architecture-diagrams/DEVELOPMENT_PLAN.md)

## License

[MIT License](LICENSE)
