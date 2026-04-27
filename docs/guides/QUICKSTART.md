# HiveCloud 快速开始指南

> **版本**: 1.0.0  
> **更新日期**: 2026-04-27  
> **预计时间**: 30 分钟

本指南将帮助您在 30 分钟内快速启动 HiveCloud 微服务架构。

## 📋 前置要求

### 必需软件

- **JDK**: 17 或更高版本
- **Maven**: 3.8 或更高版本
- **MySQL**: 8.0 或更高版本
- **Redis**: 6.0 或更高版本

### 可选软件

- **Docker**: 20.10 或更高版本（用于容器化部署）
- **IDE**: IntelliJ IDEA 或 Eclipse

## 🚀 快速启动

### 方式一：本地部署（推荐新手）

#### 1. 克隆项目

```bash
git clone https://github.com/zhulang-tianya/hivecloud-ms.git
cd hivecloud-ms
```

#### 2. 初始化数据库

```bash
# 登录 MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE hivecloud DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入表结构
use hivecloud;
source docs/guides/schema.sql;
```

#### 3. 启动 Redis

```bash
# Linux/Mac
redis-server

# Windows
redis-server.exe
```

#### 4. 修改配置文件

编辑 `hivecloud-framework/hivecloud-service-registry/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hivecloud?useSSL=false&serverTimezone=UTC
    username: root
    password: your_password  # 修改为您的 MySQL 密码
  redis:
    host: localhost
    port: 6379
    password:  # 如果有密码请设置
```

#### 5. 编译项目

```bash
mvn clean package -DskipTests
```

#### 6. 启动服务

```bash
# 启动服务注册中心
java -jar hivecloud-framework/hivecloud-service-registry/target/hivecloud-service-registry-1.0.0.jar

# 启动网关（新终端）
java -jar hivecloud-gateway/target/hivecloud-gateway-1.0.0.jar

# 启动系统服务（新终端）
java -jar hivecloud-modules/hivecloud-module-system/target/hivecloud-module-system-1.0.0.jar
```

#### 7. 验证服务

访问 Swagger UI：
```
http://localhost:8081/swagger-ui.html
```

访问健康检查接口：
```
http://localhost:8080/actuator/health
```

### 方式二：Docker 部署（推荐生产环境）

#### 1. 准备环境

确保已安装 Docker 和 Docker Compose。

#### 2. 启动所有服务

```bash
cd deploy
docker-compose up -d
```

#### 3. 查看日志

```bash
docker-compose logs -f hivecloud-ms
```

#### 4. 停止服务

```bash
docker-compose down
```

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
└── deploy/                  # 部署脚本
```

## 🎯 核心功能

### 服务注册与发现

```java
@Resource
private ServiceRegistry serviceRegistry;

// 注册服务
serviceRegistry.register(instance);

// 发现服务
List<ServiceInstance> instances = serviceRegistry.discover("service-name");
```

### 使用缓存插件

```java
@Resource
private TwoLevelCacheService cacheService;

// 写入缓存
cacheService.put("key", "value", 3600);

// 读取缓存
String value = cacheService.get("key", String.class);
```

### 使用加密插件

```java
@Resource
private CryptoService cryptoService;

// AES 加密
String encrypted = cryptoService.aesEncrypt("data", "key");

// MD5 加密
String md5 = cryptoService.md5("data");
```

## 🧪 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定模块测试
mvn test -pl hivecloud-framework/hivecloud-service-registry

# 运行特定测试类
mvn test -Dtest=RedisServiceRegistryTest
```

## 📊 监控与告警

### Prometheus

访问 Prometheus：
```
http://localhost:9090
```

### Grafana

访问 Grafana：
```
http://localhost:3000
```
默认账号密码：admin / admin123

## 🔧 常见问题

### 1. 启动失败：端口被占用

**解决方案**：修改配置文件中的端口号

```yaml
server:
  port: 8081  # 修改为其他端口
```

### 2. 数据库连接失败

**解决方案**：
- 检查 MySQL 是否启动
- 检查数据库用户名密码是否正确
- 检查防火墙设置

### 3. Redis 连接失败

**解决方案**：
- 检查 Redis 是否启动
- 检查 Redis 配置 `redis-cli ping` 应返回 `PONG`

### 4. Maven 编译失败

**解决方案**：
```bash
# 清理 Maven 缓存
mvn clean

# 重新下载依赖
mvn dependency:purge-local-repository

# 重新编译
mvn clean package -DskipTests
```

## 📚 下一步

- 阅读 [架构设计文档](docs/architecture/README.md) 了解系统架构
- 阅读 [API 文档](docs/guides/API.md) 了解接口详情
- 阅读 [部署文档](docs/guides/DEPLOYMENT.md) 了解生产部署
- 阅读 [贡献指南](CONTRIBUTING.md) 参与项目开发

## 💬 获取帮助

- 查看 [GitHub Issues](https://github.com/zhulang-tianya/hivecloud-ms/issues)
- 参与 [GitHub Discussions](https://github.com/zhulang-tianya/hivecloud-ms/discussions)
- 发送邮件至 [INSERT_EMAIL]

## 🎉 开始使用

现在您已经成功启动 HiveCloud，可以开始开发您的业务功能了！

祝您好运！🚀
