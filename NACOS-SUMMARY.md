# Nacos 集成总结

## ✅ 已完成的工作

### 1. 添加 Maven 依赖

**Gateway 模块** (`hivecloud-gateway/pom.xml`):
```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

**System 模块** (`hivecloud-modules/hivecloud-module-system/pom.xml`):
```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

### 2. 创建配置文件

**Gateway 模块** - `bootstrap.yml`:
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: 172.10.6.194:8848
        username: nacos
        password: nacos
      config:
        server-addr: 172.10.6.194:8848
        username: nacos
        password: nacos
        file-extension: yaml
        shared-configs:
          - data-id: hivecloud-common.yaml
            refresh: true
        extension-configs:
          - data-id: hivecloud-gateway.yaml
            refresh: true
```

**System 模块** - `bootstrap.yml`:
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: 172.10.6.194:8848
        username: nacos
        password: nacos
      config:
        server-addr: 172.10.6.194:8848
        username: nacos
        password: nacos
        file-extension: yaml
        shared-configs:
          - data-id: hivecloud-common.yaml
            refresh: true
        extension-configs:
          - data-id: hivecloud-system.yaml
            refresh: true
```

### 3. 更新启动类

**GatewayApplication.java**:
```java
@SpringBootApplication
@EnableDiscoveryClient  // 启用 Nacos 服务发现
public class GatewayApplication {
    // ...
}
```

**SystemApplication.java**:
```java
@SpringBootApplication
@EnableDiscoveryClient  // 启用 Nacos 服务发现
@MapperScan("com.hivecloud.system.mapper")
public class SystemApplication {
    // ...
}
```

### 4. 创建 Nacos 配置示例

在 `deploy/nacos-configs/` 目录下创建了三个配置文件：

- **hivecloud-common.yaml** - Redis 配置、通用配置
- **hivecloud-gateway.yaml** - JWT 密钥、限流配置
- **hivecloud-system.yaml** - 数据库、MyBatis Plus 配置

### 5. 创建验证脚本

**verify-nacos.ps1** - PowerShell 验证脚本，用于检查：
- Nacos 服务连通性
- 用户名密码认证
- 配置文件完整性
- Maven 依赖配置

---

## 🚀 使用步骤

### 步骤 1: 在 Nacos 控制台创建配置

1. 访问 Nacos 控制台：`http://172.10.6.194:8848/nacos`
2. 登录：用户名 `nacos` / 密码 `nacos`
3. 进入 **配置管理 > 配置列表**
4. 点击 **+** 创建新配置

**创建 hivecloud-common.yaml**:
- Data ID: `hivecloud-common.yaml`
- Group: `DEFAULT_GROUP`
- 配置格式：YAML
- 配置内容：复制 `deploy/nacos-configs/hivecloud-common.yaml` 的内容

**创建 hivecloud-gateway.yaml**:
- Data ID: `hivecloud-gateway.yaml`
- Group: `DEFAULT_GROUP`
- 配置格式：YAML
- 配置内容：复制 `deploy/nacos-configs/hivecloud-gateway.yaml` 的内容

**创建 hivecloud-system.yaml**:
- Data ID: `hivecloud-system.yaml`
- Group: `DEFAULT_GROUP`
- 配置格式：YAML
- 配置内容：复制 `deploy/nacos-configs/hivecloud-system.yaml` 的内容

### 步骤 2: 运行验证脚本

```powershell
cd d:\work\workcode\trae_v_2\hivecloud-ms
.\verify-nacos.ps1
```

如果所有检查都通过，会看到：
```
✓ Nacos 服务可访问
✓ Nacos 用户名密码正确
✓ 配置文件存在
✓ 已添加 Nacos 依赖
```

### 步骤 3: 启动服务

**启动 Gateway**:
```bash
cd hivecloud-gateway
mvn spring-boot:run
```

**启动 System 模块**:
```bash
cd hivecloud-modules\hivecloud-module-system
mvn spring-boot:run
```

### 步骤 4: 验证服务注册

1. 查看启动日志，应该看到：
```log
INFO [main] c.a.c.n.NacosPropertySourceBuilder - Loading data source from nacos
INFO [main] c.a.cloud.nacos.discovery.NacosWatch - Started Nacos Watch
```

2. 访问 Nacos 控制台：`http://172.10.6.194:8848/nacos`
3. 进入 **服务管理 > 服务列表**
4. 应该能看到两个服务：
   - `hivecloud-gateway`
   - `hivecloud-system`

---

## 📊 配置说明

### Nacos 配置信息

| 配置项 | 值 | 说明 |
|--------|-----|------|
| 服务器地址 | 172.10.6.194:8848 | Nacos 集群地址 |
| 用户名 | nacos | 登录用户名 |
| 密码 | nacos | 登录密码 |
| 命名空间 | public | 默认命名空间 |
| 分组 | DEFAULT_GROUP | 默认分组 |

### 配置优先级

```
Nacos 配置 > bootstrap.yml > application.yml > 默认值
```

**加载顺序**:
1. `hivecloud-common.yaml` (共享配置)
2. `hivecloud-gateway.yaml` 或 `hivecloud-system.yaml` (应用专属)
3. `bootstrap.yml` (本地引导配置)
4. `application.yml` (本地应用配置)
5. `application-dev.yml` (环境配置)

---

## 🔍 故障排查

### 问题 1: 无法连接 Nacos

**错误**:
```
com.alibaba.nacos.api.exception.NacosException: Client not connected
```

**解决方案**:
1. 检查 Nacos 是否启动：访问 `http://172.10.6.194:8848/nacos`
2. 检查网络：`telnet 172.10.6.194 8848`
3. 检查防火墙是否开放 8848 端口

### 问题 2: 认证失败

**错误**:
```
com.alibaba.nacos.api.exception.NacosException: authorization failed
```

**解决方案**:
1. 确认用户名密码正确
2. 检查 Nacos 用户权限
3. 确认有对应命名空间的读写权限

### 问题 3: 配置未生效

**解决方案**:
1. 确认配置文件名是 `bootstrap.yml` 不是 `application.yml`
2. 检查 Data ID 和 Group 是否匹配
3. 查看启动日志确认配置加载情况

---

## 📚 相关文件

- **集成文档**: [docs/guides/NACOS-INTEGRATION.md](file://d:\work\workcode\trae_v_2\hivecloud-ms\docs\guides\NACOS-INTEGRATION.md)
- **验证脚本**: [verify-nacos.ps1](file://d:\work\workcode\trae_v_2\hivecloud-ms\verify-nacos.ps1)
- **配置示例**: [deploy/nacos-configs/](file://d:\work\workcode\trae_v_2\hivecloud-ms\deploy\nacos-configs)

---

## ✅ 验证清单

- [ ] Nacos 服务可访问
- [ ] 用户名密码正确
- [ ] Maven 依赖已添加
- [ ] bootstrap.yml 已创建
- [ ] 启动类已添加 @EnableDiscoveryClient
- [ ] Nacos 配置已导入
- [ ] Gateway 服务启动成功
- [ ] System 服务启动成功
- [ ] Nacos 控制台能看到服务注册

---

**更新时间**: 2026-04-28  
**Git 提交**: `bfa5a1f`
