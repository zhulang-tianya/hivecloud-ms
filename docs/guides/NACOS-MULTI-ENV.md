# Nacos 多环境配置方案

## 📋 目录

1. [环境隔离方案](#环境隔离方案)
2. [Namespace 配置](#namespace-配置)
3. [Maven Profile 配置](#maven-profile-配置)
4. [配置示例](#配置示例)
5. [使用指南](#使用指南)

---

## 🎯 环境隔离方案

### 架构设计

```
┌─────────────────────────────────────────────────────────┐
│                    Nacos Server                         │
│                  172.10.6.194:8848                      │
│                                                         │
│  ┌─────────────┬─────────────┬─────────────┐           │
│  │   Dev       │    Test     │    Prod     │           │
│  │ Namespace   │ Namespace   │ Namespace   │           │
│  │             │             │             │           │
│  │ - common    │ - common    │ - common    │           │
│  │ - gateway   │ - gateway   │ - gateway   │           │
│  │ - system    │ - system    │ - system    │           │
│  │ - payment   │ - payment   │ - payment   │           │
│  └─────────────┴─────────────┴─────────────┘           │
└─────────────────────────────────────────────────────────┘
```

### 环境划分

| 环境 | Namespace ID | 用途 | 数据库 | Redis |
|------|-------------|------|--------|-------|
| **开发环境** | `dev` | 开发人员本地调试 | dev-db | dev-redis |
| **测试环境** | `test` | 测试环境集成测试 | test-db | test-redis |
| **生产环境** | `prod` | 生产环境正式运行 | prod-db | prod-redis |

---

## 🔧 Namespace 配置

### 步骤 1: 在 Nacos 控制台创建 Namespace

1. 登录 Nacos 控制台：`http://172.10.6.194:8848/nacos`
2. 进入 **命名空间 > 新建命名空间**
3. 创建以下三个命名空间：

**开发环境**:
- Namespace ID: `dev`
- 命名空间名称：`开发环境`
- 描述：开发人员本地调试环境

**测试环境**:
- Namespace ID: `test`
- 命名空间名称：`测试环境`
- 描述：测试环境集成测试

**生产环境**:
- Namespace ID: `prod`
- 命名空间名称：`生产环境`
- 描述：生产环境正式运行

### 步骤 2: 在各 Namespace 中创建配置

#### 公共配置 (所有环境共用)

**Data ID**: `hivecloud-common.yaml`  
**Group**: `DEFAULT_GROUP`  
**配置内容**:
```yaml
# Redis 配置
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
    timeout: 5000ms
    lettuce:
      pool:
        max-active: 8
        max-wait: -1ms
        max-idle: 8
        min-idle: 0

# 通用配置
common:
  charset: UTF-8
  token-expire-time: 7200
```

#### 开发环境配置

**Data ID**: `hivecloud-gateway.yaml`  
**Namespace**: `dev`  
**配置内容**:
```yaml
# Gateway 配置 - 开发环境
hivecloud:
  gateway:
    jwt:
      secret: dev-secret-key-for-development-only
      token-expire-time: 7200
    ratelimit:
      enabled: false  # 开发环境关闭限流

# 日志配置 - 开发环境详细日志
logging:
  level:
    root: INFO
    com.hivecloud.gateway: DEBUG
    org.springframework.cloud.gateway: DEBUG
```

**Data ID**: `hivecloud-system.yaml`  
**Namespace**: `dev`  
**配置内容**:
```yaml
# 数据库配置 - 开发环境
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://dev-db-server:3306/hivecloud_system_dev?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: dev_user
    password: dev_password
    hikari:
      minimum-idle: 2
      maximum-pool-size: 5

# MyBatis Plus 配置
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

#### 测试环境配置

**Data ID**: `hivecloud-gateway.yaml`  
**Namespace**: `test`  
**配置内容**:
```yaml
# Gateway 配置 - 测试环境
hivecloud:
  gateway:
    jwt:
      secret: test-secret-key-for-integration-testing
      token-expire-time: 7200
    ratelimit:
      enabled: true
      requests-per-second: 1000
      burst-capacity: 2000

# 日志配置 - 测试环境
logging:
  level:
    root: INFO
    com.hivecloud.gateway: INFO
```

**Data ID**: `hivecloud-system.yaml`  
**Namespace**: `test`  
**配置内容**:
```yaml
# 数据库配置 - 测试环境
spring:
  datasource:
    url: jdbc:mysql://test-db-server:3306/hivecloud_system_test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: test_user
    password: test_password
    hikari:
      minimum-idle: 5
      maximum-pool-size: 10
```

#### 生产环境配置

**Data ID**: `hivecloud-gateway.yaml`  
**Namespace**: `prod`  
**配置内容**:
```yaml
# Gateway 配置 - 生产环境
hivecloud:
  gateway:
    jwt:
      secret: ${GATEWAY_JWT_SECRET:prod-secure-secret-key-must-be-changed}
      token-expire-time: 3600
    ratelimit:
      enabled: true
      requests-per-second: 500
      burst-capacity: 1000

# 日志配置 - 生产环境
logging:
  level:
    root: WARN
    com.hivecloud.gateway: WARN
```

**Data ID**: `hivecloud-system.yaml`  
**Namespace**: `prod`  
**配置内容**:
```yaml
# 数据库配置 - 生产环境
spring:
  datasource:
    url: jdbc:mysql://prod-db-server:3306/hivecloud_system_prod?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai
    username: ${DB_USERNAME:prod_user}
    password: ${DB_PASSWORD:prod_password}
    hikari:
      minimum-idle: 10
      maximum-pool-size: 50
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

---

## 🛠️ Maven Profile 配置

### 父 pom.xml 配置

已在父 pom.xml 中配置三个 Profile：

```xml
<profiles>
    <!-- 开发环境配置 -->
    <profile>
        <id>dev</id>
        <properties>
            <nacos.namespace>dev</nacos.namespace>
        </properties>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
    </profile>
    
    <!-- 测试环境配置 -->
    <profile>
        <id>test</id>
        <properties>
            <nacos.namespace>test</nacos.namespace>
        </properties>
    </profile>
    
    <!-- 生产环境配置 -->
    <profile>
        <id>prod</id>
        <properties>
            <nacos.namespace>prod</nacos.namespace>
        </properties>
    </profile>
</profiles>
```

### 使用方式

#### 本地开发 (默认)
```bash
mvn clean install
# 或明确指定
mvn clean install -Pdev
```

#### 测试环境打包
```bash
mvn clean package -Ptest
```

#### 生产环境打包
```bash
mvn clean package -DskipTests -Pprod
```

---

## 📝 配置示例

### 统一配置属性

所有微服务模块通过 Maven 占位符引用统一配置：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: ${nacos.server-addr}
        username: ${nacos.username}
        password: ${nacos.password}
        namespace: ${nacos.namespace}
        group: ${nacos.group}
      
      config:
        server-addr: ${nacos.server-addr}
        username: ${nacos.username}
        password: ${nacos.password}
        namespace: ${nacos.namespace}
        group: ${nacos.group}
```

### 环境变量覆盖

生产环境可通过环境变量覆盖敏感配置：

```bash
# Linux/Mac
export GATEWAY_JWT_SECRET="your-production-secret"
export DB_USERNAME="prod_db_user"
export DB_PASSWORD="prod_db_password"

# Windows PowerShell
$env:GATEWAY_JWT_SECRET="your-production-secret"
$env:DB_USERNAME="prod_db_user"
$env:DB_PASSWORD="prod_db_password"
```

---

## 🚀 使用指南

### 步骤 1: 选择环境 Profile

根据部署环境选择对应的 Maven Profile：

```bash
# 开发环境
mvn spring-boot:run -Pdev

# 测试环境
mvn spring-boot:run -Ptest

# 生产环境
mvn spring-boot:run -Pprod
```

### 步骤 2: 验证 Namespace

启动后检查日志确认 Namespace 配置正确：

```log
INFO [main] c.a.c.n.NacosPropertySourceBuilder - Loading data source from nacos, dataId: hivecloud-common.yaml, group: DEFAULT_GROUP, namespace: dev
INFO [main] c.a.cloud.nacos.discovery.NacosWatch - Started Nacos Watch for namespace: dev
```

### 步骤 3: 验证服务注册

访问 Nacos 控制台，确认服务注册到正确的 Namespace：

1. 登录 Nacos：`http://172.10.6.194:8848/nacos`
2. 选择对应的命名空间（dev/test/prod）
3. 查看 **服务管理 > 服务列表**
4. 应该能看到对应环境的服务

### 步骤 4: 验证配置加载

查看启动日志确认配置加载情况：

```log
INFO [main] c.a.c.n.NacosPropertySourceBuilder - Load config data successfully: hivecloud-common.yaml
INFO [main] c.a.c.n.NacosPropertySourceBuilder - Load config data successfully: hivecloud-gateway.yaml
```

---

## 🔍 配置隔离验证

### 开发环境验证

```bash
# 启动开发环境服务
cd hivecloud-gateway
mvn spring-boot:run -Pdev

# 验证
curl http://localhost:8080/actuator/env | grep nacos
```

期望输出：
```json
{
  "nacos.server-addr": "172.10.6.194:8848",
  "nacos.namespace": "dev",
  "nacos.group": "DEFAULT_GROUP"
}
```

### 测试环境验证

```bash
# 打包测试环境
mvn clean package -Ptest

# 启动
java -jar hivecloud-gateway/target/hivecloud-gateway-1.0.0-SNAPSHOT.jar \
  --spring.profiles.active=test
```

### 生产环境验证

```bash
# 打包生产环境
mvn clean package -DskipTests -Pprod

# 启动 (使用环境变量)
GATEWAY_JWT_SECRET="prod-secret" \
DB_USERNAME="prod_user" \
DB_PASSWORD="prod_password" \
java -jar hivecloud-gateway/target/hivecloud-gateway-1.0.0-SNAPSHOT.jar
```

---

## 📊 配置管理最佳实践

### 1. 配置分离原则

- ✅ **公共配置** 放在 `hivecloud-common.yaml` (所有服务共享)
- ✅ **应用配置** 放在 `hivecloud-{应用名}.yaml` (服务专属)
- ✅ **环境配置** 通过 Namespace 隔离
- ✅ **敏感配置** 使用环境变量或加密

### 2. 配置优先级

```
环境变量 > Nacos 配置 > bootstrap.yml > application.yml > 默认值
```

### 3. 配置刷新

Nacos 支持配置热更新，修改后无需重启：

```java
@RestController
@RefreshScope
public class ConfigController {
    
    @Value("${some.config:default}")
    private String someConfig;
    
    @GetMapping("/config")
    public String getConfig() {
        return someConfig;
    }
}
```

### 4. 配置加密

生产环境敏感配置使用加密：

```yaml
spring:
  datasource:
    password: '{cipher}加密后的密文'
```

---

## ⚠️ 注意事项

### 1. Namespace 切换

- 切换 Namespace 后需要重启服务才能生效
- 确保目标 Namespace 中已创建所有必需的配置

### 2. 配置版本管理

- Nacos 配置支持版本管理，修改前建议创建快照
- 生产环境配置修改需要走变更流程

### 3. 配置回滚

如果配置修改导致问题，可以在 Nacos 控制台快速回滚：
1. 进入 **配置管理 > 配置列表**
2. 找到对应配置
3. 点击 **历史版本**
4. 选择之前的版本进行回滚

### 4. 权限控制

生产环境建议配置 Nacos 权限：
1. 为不同环境创建不同用户
2. 限制用户只能访问对应的 Namespace
3. 生产环境配置修改需要审批流程

---

## 📚 相关文件

- **父 pom.xml**: [pom.xml](file://d:\work\workcode\trae_v_2\hivecloud-ms\pom.xml)
- **Gateway 配置**: [bootstrap.yml](file://d:\work\workcode\trae_v_2\hivecloud-ms\hivecloud-gateway\src\main\resources\bootstrap.yml)
- **System 配置**: [bootstrap.yml](file://d:\work\workcode\trae_v_2\hivecloud-ms\hivecloud-modules\hivecloud-module-system\src\main\resources\bootstrap.yml)
- **Nacos 配置示例**: [deploy/nacos-configs/](file://d:\work\workcode\trae_v_2\hivecloud-ms\deploy\nacos-configs)

---

**更新时间**: 2026-04-28  
**版本**: v1.0.0
