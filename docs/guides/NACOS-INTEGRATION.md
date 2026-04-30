# Nacos 集成指南

## 📋 目录

1. [Nacos 配置信息](#nacos-配置信息)
2. [添加依赖](#添加依赖)
3. [配置文件](#配置文件)
4. [启动类配置](#启动类配置)
5. [验证](#验证)

---

## 🎯 Nacos 配置信息

```properties
# Nacos 服务器地址
nacos.address=172.10.6.194:8848

# Nacos 用户名
nacos.user.name=nacos

# Nacos 密码
nacos.user.password=nacos
```

---

## 🔧 添加依赖

### 1. 在父 pom.xml 中添加 Nacos 依赖管理

**文件**: `hivecloud-dependencies/pom.xml`

在 `<properties>` 中添加（已存在，无需重复添加）：
```xml
<spring-cloud-alibaba.version>2023.0.1.0</spring-cloud-alibaba.version>
```

在 `<dependencyManagement>` 中添加（已存在，无需重复添加）：
```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-alibaba-dependencies</artifactId>
    <version>${spring-cloud-alibaba.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

### 2. 在各模块的 pom.xml 中添加依赖

#### Gateway 模块 (`hivecloud-gateway/pom.xml`)

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
    
    <!-- 其他依赖... -->
</dependencies>
```

#### System 模块 (`hivecloud-modules/hivecloud-module-system/pom.xml`)

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
    
    <!-- 其他依赖... -->
</dependencies>
```

---

## 📝 配置文件

### 1. Gateway 模块配置

**文件**: `hivecloud-gateway/src/main/resources/bootstrap.yml`

```yaml
spring:
  application:
    name: hivecloud-gateway
  cloud:
    nacos:
      # Nacos 服务注册中心配置
      discovery:
        server-addr: 172.10.6.194:8848
        username: nacos
        password: nacos
        namespace: public  # 命名空间，默认 public
        group: DEFAULT_GROUP  # 分组，默认 DEFAULT_GROUP
        register-enabled: true  # 是否注册到 Nacos
        watch-enabled: true  # 是否启用服务监听
      
      # Nacos 配置中心配置
      config:
        server-addr: 172.10.6.194:8848
        username: nacos
        password: nacos
        namespace: public
        group: DEFAULT_GROUP
        file-extension: yaml
        enabled: true  # 是否启用配置中心
        refresh-enabled: true  # 是否启用配置刷新
        shared-configs:
          - data-id: hivecloud-common.yaml
            group: DEFAULT_GROUP
            refresh: true
        extension-configs:
          - data-id: hivecloud-gateway.yaml
            group: DEFAULT_GROUP
            refresh: true
```

**文件**: `hivecloud-gateway/src/main/resources/application.yml`

```yaml
# 应用配置
spring:
  application:
    name: hivecloud-gateway
  profiles:
    active: dev  # 激活 dev 环境配置
  cloud:
    gateway:
      routes:
        - id: system
          uri: lb://hivecloud-system
          predicates:
            - Path=/api/system/**
          filters:
            - StripPrefix=1
      default-filters:
        - DedupeResponseHeader=Access-Control-Allow-Origin
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOriginPatterns: "*"
            allowedMethods: "*"
            allowedHeaders: "*"
            allowCredentials: true

# HiveCloud 网关配置
hivecloud:
  gateway:
    jwt:
      secret: hivecloud-secret-key-must-be-at-least-32-chars
      permit-all:
        - /api/system/v1/login
        - /api/system/v1/register

# 日志配置
logging:
  level:
    root: INFO
    com.hivecloud.gateway: DEBUG
    com.alibaba.cloud.nacos: DEBUG
```

### 2. System 模块配置

**文件**: `hivecloud-modules/hivecloud-module-system/src/main/resources/bootstrap.yml`

```yaml
spring:
  application:
    name: hivecloud-system
  cloud:
    nacos:
      # Nacos 服务注册中心配置
      discovery:
        server-addr: 172.10.6.194:8848
        username: nacos
        password: nacos
        namespace: public
        group: DEFAULT_GROUP
        register-enabled: true
        watch-enabled: true
      
      # Nacos 配置中心配置
      config:
        server-addr: 172.10.6.194:8848
        username: nacos
        password: nacos
        namespace: public
        group: DEFAULT_GROUP
        file-extension: yaml
        enabled: true
        refresh-enabled: true
        shared-configs:
          - data-id: hivecloud-common.yaml
            group: DEFAULT_GROUP
            refresh: true
        extension-configs:
          - data-id: hivecloud-system.yaml
            group: DEFAULT_GROUP
            refresh: true
```

**文件**: `hivecloud-modules/hivecloud-module-system/src/main/resources/application.yml`

```yaml
# 应用配置
spring:
  application:
    name: hivecloud-system
  profiles:
    active: dev

# 数据库配置
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/hivecloud_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 30000
      connection-timeout: 30000
      max-lifetime: 1800000

# MyBatis Plus 配置
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.hivecloud.system.entity
  global-config:
    db-config:
      id-type: assign_id
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: false
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

# 日志配置
logging:
  level:
    root: INFO
    com.hivecloud.system: DEBUG
    com.alibaba.cloud.nacos: DEBUG
```

---

## 🚀 启动类配置

### 1. Gateway 启动类

**文件**: `hivecloud-gateway/src/main/java/com/hivecloud/gateway/GatewayApplication.java`

```java
package com.hivecloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关启动类
 * 启用 Nacos 服务发现
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
```

### 2. System 模块启动类

**文件**: `hivecloud-modules/hivecloud-module-system/src/main/java/com/hivecloud/system/SystemApplication.java`

```java
package com.hivecloud.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 系统管理模块启动类
 * 启用 Nacos 服务发现
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.hivecloud.system.mapper")
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}
```

---

## ✅ 验证

### 1. 启动 Nacos

访问 Nacos 控制台：`http://172.10.6.194:8848/nacos`

登录账号：`nacos` / 密码：`nacos`

### 2. 在 Nacos 中创建配置

#### 创建公共配置：`hivecloud-common.yaml`

**Data ID**: `hivecloud-common.yaml`  
**Group**: `DEFAULT_GROUP`  
**配置内容**:
```yaml
# Redis 配置
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
    lettuce:
      pool:
        max-active: 8
        max-wait: -1ms
        max-idle: 8
        min-idle: 0
    timeout: 5000ms

# 通用配置
common:
  charset: UTF-8
  token-expire-time: 7200
```

#### 创建 Gateway 配置：`hivecloud-gateway.yaml`

**Data ID**: `hivecloud-gateway.yaml`  
**Group**: `DEFAULT_GROUP`  
**配置内容**:
```yaml
# Gateway 专属配置
hivecloud:
  gateway:
    jwt:
      secret: hivecloud-secret-key-must-be-at-least-32-chars
      token-expire-time: 7200
```

#### 创建 System 配置：`hivecloud-system.yaml`

**Data ID**: `hivecloud-system.yaml`  
**Group**: `DEFAULT_GROUP`  
**配置内容**:
```yaml
# System 模块专属配置
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/hivecloud_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
```

### 3. 启动服务

#### 启动 Gateway
```bash
cd hivecloud-gateway
mvn spring-boot:run
```

#### 启动 System 模块
```bash
cd hivecloud-modules/hivecloud-module-system
mvn spring-boot:run
```

### 4. 验证服务注册

在 Nacos 控制台的 **服务管理 > 服务列表** 中应该能看到：
- `hivecloud-gateway`
- `hivecloud-system`

### 5. 验证配置读取

查看启动日志，应该能看到类似输出：
```log
INFO [main] c.a.c.n.NacosPropertySourceBuilder - Loading data source from nacos, dataId: hivecloud-common.yaml, group: DEFAULT_GROUP
INFO [main] c.a.c.n.NacosPropertySourceBuilder - Load config data successfully: hivecloud-common.yaml
INFO [main] c.a.cloud.nacos.discovery.NacosWatch - Started Nacos Watch
```

---

## 🔍 故障排查

### 问题 1: 无法连接到 Nacos

**错误信息**:
```
com.alibaba.nacos.api.exception.NacosException: Client not connected, current status: STARTING
```

**解决方案**:
1. 检查 Nacos 服务是否启动：`http://172.10.6.194:8848/nacos`
2. 检查防火墙是否开放 8848 端口
3. 检查网络连通性：`telnet 172.10.6.194 8848`

### 问题 2: 认证失败

**错误信息**:
```
com.alibaba.nacos.api.exception.NacosException: authorization failed
```

**解决方案**:
1. 检查用户名密码是否正确
2. 检查 Nacos 用户权限配置
3. 确认用户有对应命名空间的读写权限

### 问题 3: 配置未生效

**解决方案**:
1. 检查配置文件名是否为 `bootstrap.yml`（不是 `application.yml`）
2. 检查 Data ID 和 Group 是否匹配
3. 查看启动日志确认配置加载情况
4. 确认 `spring.cloud.nacos.config.enabled=true`

---

## 📊 配置优先级

Nacos 配置中心的配置优先级高于本地配置文件：

```
Nacos 配置 > bootstrap.yml > application.yml > 默认值
```

**配置加载顺序**:
1. `hivecloud-common.yaml` (共享配置)
2. `hivecloud-gateway.yaml` 或 `hivecloud-system.yaml` (应用专属配置)
3. `bootstrap.yml` (本地引导配置)
4. `application.yml` (本地应用配置)
5. `application-dev.yml` (环境专属配置)

---

## 🎯 最佳实践

### 1. 配置分离

- **公共配置** 放在 `hivecloud-common.yaml`
- **应用配置** 放在 `hivecloud-{应用名}.yaml`
- **环境配置** 通过 `spring.profiles.active` 区分

### 2. 配置刷新

Nacos 支持配置热更新，修改配置后无需重启服务：

```java
@RestController
@RefreshScope  // 启用配置刷新
public class TestController {
    
    @Value("${some.config:default}")
    private String someConfig;
    
    @GetMapping("/config")
    public String getConfig() {
        return someConfig;
    }
}
```

### 3. 命名空间隔离

使用命名空间隔离不同环境：

```yaml
spring:
  cloud:
    nacos:
      config:
        namespace: dev  # 开发环境
        # namespace: test  # 测试环境
        # namespace: prod  # 生产环境
```

### 4. 配置加密

敏感配置使用加密：

```yaml
spring:
  datasource:
    password: '{cipher}加密后的密码'
```

---

## 📚 参考资料

- [Nacos 官方文档](https://nacos.io/zh-cn/docs/quick-start.html)
- [Spring Cloud Alibaba 参考文档](https://sca.aliyun.com/docs/overview/overview/)
- [Nacos 配置管理](https://nacos.io/zh-cn/docs/user-guide/config.html)
- [Nacos 服务发现](https://nacos.io/zh-cn/docs/user-guide/service.html)
