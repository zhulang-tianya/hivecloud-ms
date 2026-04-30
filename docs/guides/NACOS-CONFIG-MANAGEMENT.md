# Nacos 配置统一管理方案

## 📋 目录

1. [方案概述](#方案概述)
2. [架构设计](#架构设计)
3. [实施步骤](#实施步骤)
4. [配置说明](#配置说明)
5. [使用指南](#使用指南)

---

## 🎯 方案概述

### 实施目标

✅ **统一配置管理**：所有微服务统一使用 Nacos 配置中心  
✅ **集中化配置**：Nacos 地址信息在父 pom.xml 中统一配置  
✅ **Maven 依赖复用**：通过统一 starter 引入 Nacos 依赖  
✅ **多环境隔离**：通过 Namespace 实现环境隔离与配置管理  

### 核心优势

| 特性 | 说明 | 收益 |
|------|------|------|
| **集中管理** | Nacos 地址在父 pom.xml 统一配置 | 避免重复配置，一处修改全局生效 |
| **依赖复用** | 通过 `hivecloud-starter-nacos` 统一管理 Nacos 依赖 | 减少依赖冲突，版本统一 |
| **环境隔离** | 通过 Namespace 实现 dev/test/prod 环境隔离 | 配置安全隔离，避免误操作 |
| **动态刷新** | 支持配置热更新，无需重启服务 | 提高运维效率，降低停机时间 |
| **配置审计** | Nacos 提供配置版本管理和变更历史 | 配置变更可追溯，便于问题排查 |

---

## 🏗️ 架构设计

### 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                      父 pom.xml                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Nacos 统一配置属性                                    │   │
│  │ <nacos.server-addr>172.10.6.194:8848</nacos.server-addr> │
│  │ <nacos.username>nacos</nacos.username>                │   │
│  │ <nacos.password>nacos</nacos.password>                │   │
│  │ <nacos.namespace>dev</nacos.namespace>                │   │
│  │ <nacos.group>DEFAULT_GROUP</nacos.group>              │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ Maven 属性继承
                            ▼
┌─────────────────────────────────────────────────────────────┐
│              hivecloud-starter-nacos (统一启动器)            │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Nacos 依赖管理                                        │   │
│  │ - spring-cloud-starter-alibaba-nacos-discovery       │   │
│  │ - spring-cloud-starter-alibaba-nacos-config          │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ Maven 依赖继承
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   微服务模块                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Gateway    │  │    System    │  │   Payment    │      │
│  │ bootstrap.yml│  │ bootstrap.yml│  │ bootstrap.yml│      │
│  │ ${nacos.*}   │  │ ${nacos.*}   │  │ ${nacos.*}   │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ 配置加载
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    Nacos Server                              │
│  ┌─────────────┬─────────────┬─────────────┐               │
│  │   Dev       │    Test     │    Prod     │               │
│  │ Namespace   │ Namespace   │ Namespace   │               │
│  └─────────────┴─────────────┴─────────────┘               │
└─────────────────────────────────────────────────────────────┘
```

### 模块关系

```
hivecloud-ms (父 POM)
    │
    ├─ hivecloud-dependencies (依赖管理 BOM)
    │
    ├─ hivecloud-framework (核心框架)
    │   └─ hivecloud-starter-nacos (Nacos 统一启动器) ⭐
    │
    ├─ hivecloud-gateway (业务网关)
    │   └─ 依赖：hivecloud-starter-nacos
    │
    ├─ hivecloud-modules (业务模块)
    │   ├─ hivecloud-module-system
    │   │   └─ 依赖：hivecloud-starter-nacos
    │   ├─ hivecloud-module-payment
    │   │   └─ 依赖：hivecloud-starter-nacos
    │   └─ ...
    │
    └─ hivecloud-plugins (插件模块)
        └─ 依赖：hivecloud-starter-nacos (可选)
```

---

## 🔧 实施步骤

### 步骤 1: 在父 pom.xml 中配置 Nacos 属性

**文件**: `pom.xml`

```xml
<properties>
    <!-- Nacos 配置中心统一管理 -->
    <nacos.server-addr>172.10.6.194:8848</nacos.server-addr>
    <nacos.username>nacos</nacos.username>
    <nacos.password>nacos</nacos.password>
    <nacos.namespace>public</nacos.namespace>
    <nacos.group>DEFAULT_GROUP</nacos.group>
</properties>

<profiles>
    <!-- 开发环境 -->
    <profile>
        <id>dev</id>
        <properties>
            <nacos.namespace>dev</nacos.namespace>
        </properties>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
    </profile>
    
    <!-- 测试环境 -->
    <profile>
        <id>test</id>
        <properties>
            <nacos.namespace>test</nacos.namespace>
        </properties>
    </profile>
    
    <!-- 生产环境 -->
    <profile>
        <id>prod</id>
        <properties>
            <nacos.namespace>prod</nacos.namespace>
        </properties>
    </profile>
</profiles>
```

### 步骤 2: 创建统一 Nacos 启动器

**文件**: `hivecloud-framework/hivecloud-starter-nacos/pom.xml`

```xml
<dependencies>
    <!-- Nacos 服务注册与发现 -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
    
    <!-- Nacos 配置中心 -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    </dependency>
</dependencies>
```

### 步骤 3: 微服务模块引入统一启动器

**示例**: `hivecloud-gateway/pom.xml`

```xml
<dependencies>
    <!-- 引入统一 Nacos 启动器 -->
    <dependency>
        <groupId>com.hivecloud</groupId>
        <artifactId>hivecloud-starter-nacos</artifactId>
        <version>${project.version}</version>
    </dependency>
    
    <!-- 其他依赖... -->
</dependencies>
```

### 步骤 4: 使用占位符配置 bootstrap.yml

**示例**: `hivecloud-gateway/src/main/resources/bootstrap.yml`

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

---

## 📝 配置说明

### 统一配置属性

| 属性 | 默认值 | 说明 | 可覆盖 |
|------|--------|------|--------|
| `nacos.server-addr` | 172.10.6.194:8848 | Nacos 服务器地址 | ✅ |
| `nacos.username` | nacos | Nacos 用户名 | ✅ |
| `nacos.password` | nacos | Nacos 密码 | ✅ |
| `nacos.namespace` | public | 命名空间 ID | ✅ (通过 Profile) |
| `nacos.group` | DEFAULT_GROUP | 配置分组 | ✅ |

### Maven Profile 配置

| Profile | Namespace | 激活方式 | 用途 |
|---------|-----------|---------|------|
| **dev** | dev | 默认激活 | 开发环境 |
| **test** | test | `-Ptest` | 测试环境 |
| **prod** | prod | `-Pprod` | 生产环境 |

### 配置优先级

```
命令行参数 > 环境变量 > Nacos 配置 > bootstrap.yml > application.yml > 默认值
```

---

## 🚀 使用指南

### 本地开发

```bash
# 使用默认 dev 环境
mvn spring-boot:run

# 或明确指定 dev 环境
mvn spring-boot:run -Pdev
```

### 测试环境部署

```bash
# 打包测试环境
mvn clean package -Ptest

# 启动服务
java -jar target/hivecloud-gateway.jar
```

### 生产环境部署

```bash
# 打包生产环境 (跳过测试)
mvn clean package -DskipTests -Pprod

# 启动服务 (使用环境变量覆盖敏感配置)
GATEWAY_JWT_SECRET="prod-secret" \
DB_USERNAME="prod_user" \
DB_PASSWORD="prod_password" \
java -jar target/hivecloud-gateway.jar
```

### 验证配置

#### 1. 检查启动日志

```log
INFO [main] c.a.c.n.NacosPropertySourceBuilder - Loading data source from nacos
INFO [main] c.a.c.n.NacosPropertySourceBuilder - namespace: dev
INFO [main] c.a.cloud.nacos.discovery.NacosWatch - Started Nacos Watch
```

#### 2. 访问 Nacos 控制台

1. 登录：`http://172.10.6.194:8848/nacos`
2. 选择对应的命名空间
3. 查看 **服务管理 > 服务列表**
4. 确认服务已注册

#### 3. 检查配置加载

```bash
# 访问 Actuator 端点
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

---

## 📊 配置管理最佳实践

### 1. 配置分离

- ✅ **公共配置**：`hivecloud-common.yaml` (所有服务共享)
- ✅ **应用配置**：`hivecloud-{应用名}.yaml` (服务专属)
- ✅ **环境配置**：通过 Namespace 隔离
- ✅ **敏感配置**：使用环境变量或加密

### 2. 配置版本管理

- 使用 Nacos 的配置版本管理功能
- 修改前创建快照
- 生产环境配置修改走审批流程

### 3. 配置加密

生产环境敏感配置使用加密：

```yaml
spring:
  datasource:
    password: '{cipher}加密后的密文'
```

### 4. 配置刷新

支持配置热更新，无需重启：

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

---

## 📚 相关文件

- **父 pom.xml**: [pom.xml](file://d:\work\workcode\trae_v_2\hivecloud-ms\pom.xml)
- **统一启动器**: [hivecloud-starter-nacos](file://d:\work\workcode\trae_v_2\hivecloud-ms\hivecloud-framework\hivecloud-starter-nacos)
- **多环境配置**: [NACOS-MULTI-ENV.md](file://d:\work\workcode\trae_v_2\hivecloud-ms\docs\guides\NACOS-MULTI-ENV.md)
- **集成指南**: [NACOS-INTEGRATION.md](file://d:\work\workcode\trae_v_2\hivecloud-ms\docs\guides\NACOS-INTEGRATION.md)

---

## ✅ 验收清单

- [ ] 父 pom.xml 中已配置 Nacos 统一属性
- [ ] 已创建 `hivecloud-starter-nacos` 统一启动器
- [ ] 所有微服务模块已引入统一启动器
- [ ] 所有 bootstrap.yml 已使用占位符配置
- [ ] Maven Profile 已配置 dev/test/prod 环境
- [ ] Nacos Namespace 已创建 dev/test/prod
- [ ] 各 Namespace 中已创建对应配置
- [ ] 服务能正确注册到对应 Namespace
- [ ] 配置能正确从 Nacos 加载

---

**实施时间**: 2026-04-28  
**版本**: v1.0.0  
**状态**: ✅ 已完成
