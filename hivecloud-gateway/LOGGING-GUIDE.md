# 网关日志插件集成指南

## 📋 目录

1. [当前实现](#当前实现)
2. [日志插件架构](#日志插件架构)
3. [集成方式](#集成方式)
4. [使用示例](#使用示例)

---

## 🎯 当前实现

### 已完成的日志记录

网关模块当前使用 **SLF4J + Logback** 进行日志记录，已实现以下日志功能：

#### 1. 访问日志过滤器 (`AccessLogFilter`)

**文件位置**: `hivecloud-gateway/src/main/java/com/hivecloud/gateway/filter/AccessLogFilter.java`

**功能**:
- ✅ 记录所有通过网关的请求
- ✅ 记录请求方法、URL、IP 地址
- ✅ 记录用户信息（userId、userName）
- ✅ 记录请求耗时
- ✅ 记录响应状态码

**日志格式**:
```log
【访问日志】GET /api/system/v1/getUserInfo | IP: 192.168.1.100 | User: admin(1)
【访问日志完成】GET /api/system/v1/getUserInfo | 耗时：45ms | 状态：200
```

#### 2. JWT 认证过滤器 (`JwtAuthFilter`)

**日志记录**:
- ⚠️ 记录 JWT 验证失败的警告日志
- ℹ️ 使用 `log.warn()` 记录 Token 无效或过期信息

**示例**:
```log
JWT validation failed: JWT signature does not match
JWT validation failed: Token 无效或已过期
```

#### 3. 限流过滤器 (`RateLimitFilter`)

**日志记录**:
- ⚠️ 记录 Redis 异常时的降级日志
- ℹ️ 使用 `log.warn()` 记录限流器异常信息

**示例**:
```log
Rate limiter Redis error, fallback to pass request: Redis connection timeout
```

---

## 🏗️ 日志插件架构

### 日志插件模块 (`hivecloud-plugin-log`)

**目录结构**:
```
hivecloud-plugins/hivecloud-plugin-log/
├── src/main/java/com/hivecloud/plugin/log/
│   ├── annotation/
│   │   └── OperLog.java              # 操作日志注解
│   ├── aspect/
│   │   └── OperLogAspect.java        # 操作日志切面
│   ├── controller/
│   │   └── OperLogController.java    # 操作日志控制器
│   ├── entity/
│   │   └── SysOperLog.java           # 操作日志实体
│   ├── enums/
│   │   └── LogType.java              # 日志类型枚举
│   ├── mapper/
│   │   └── OperLogMapper.java        # 操作日志 Mapper
│   ├── service/
│   │   └── OperLogService.java       # 操作日志服务
│   └── config/
│       └── LogAutoConfiguration.java # 自动配置类
└── pom.xml
```

### 核心组件

#### 1. `@OperLog` 注解

用于标注在 Controller 方法上，自动记录操作日志：

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperLog {
    String title() default "";                    // 操作标题
    String businessType() default "OTHER";        // 业务类型
    boolean isSaveRequestData() default true;     // 是否保存请求数据
    boolean isSaveResponseData() default false;   // 是否保存响应数据
}
```

#### 2. `OperLogAspect` 切面

自动拦截标注了 `@OperLog` 的方法，记录操作日志：

```java
@Aspect
@Component
public class OperLogAspect {
    
    @Around("@annotation(operLog)")
    public Object around(ProceedingJoinPoint point, OperLog operLog) {
        // 1. 记录请求开始时间
        // 2. 执行方法
        // 3. 记录操作日志到数据库
        // 4. 记录异常日志（如果有）
    }
}
```

#### 3. `SysOperLog` 实体

操作日志数据库表结构：

```java
@TableName("sys_oper_log")
public class SysOperLog {
    private Long id;              // 主键 ID（雪花算法）
    private String title;         // 操作标题
    private String businessType;  // 业务类型
    private String method;        // 方法名称
    private String requestMethod; // 请求方法
    private String operName;      // 操作人员姓名
    private String operIp;        // 操作 IP
    private String operUrl;       // 请求 URL
    private String operParam;     // 请求参数
    private String jsonResult;    // 返回结果
    private Integer status;       // 操作状态（0 正常 1 异常）
    private String errorMsg;      // 错误消息
    private Long operTime;        // 操作时间
    private Long costTime;        // 消耗时间
}
```

---

## 🔧 集成方式

### 方案一：使用日志插件（推荐用于业务模块）

**适用场景**: 业务模块（如 system、payment、claim 等）

**步骤**:

#### 1. 添加依赖

在模块的 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>com.hivecloud</groupId>
    <artifactId>hivecloud-plugin-log</artifactId>
</dependency>
```

#### 2. 使用注解

在 Controller 方法上添加 `@OperLog` 注解：

```java
@RestController
@RequestMapping("/system/v1/user")
public class SysUserController {
    
    @OperLog(title = "用户管理", businessType = "USER_ADD")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody UserDTO userDTO) {
        // 业务逻辑
        return Result.success();
    }
}
```

#### 3. 查看日志

通过 `OperLogController` 查询操作日志：

```bash
GET /api/system/v1/oper-log?pageNum=1&pageSize=10
```

### 方案二：使用网关访问日志过滤器（网关专用）

**适用场景**: 网关模块（hivecloud-gateway）

**特点**:
- ✅ 无需数据库存储
- ✅ 轻量级，纯日志文件记录
- ✅ 适合网关的高并发场景
- ❌ 不支持结构化查询

**配置**:

网关已自动集成 `AccessLogFilter`，无需额外配置。

### 方案三：混合模式（完整方案）

**适用场景**: 需要完整日志审计的生产环境

**架构**:
```
客户端请求
    ↓
网关层 (AccessLogFilter)
    ↓ 记录访问日志（日志文件）
业务模块 (OperLogAspect)
    ↓ 记录操作日志（数据库）
数据库 (sys_oper_log 表)
```

---

## 📖 使用示例

### 示例 1：在业务模块中使用日志插件

```java
package com.hivecloud.system.controller;

import com.hivecloud.plugin.log.annotation.OperLog;
import com.hivecloud.common.core.result.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/v1/user")
public class SysUserController {
    
    /**
     * 新增用户
     */
    @OperLog(title = "用户管理", businessType = "USER_ADD", isSaveRequestData = true)
    @PostMapping("/add")
    public Result<Void> add(@RequestBody UserDTO userDTO) {
        userService.add(userDTO);
        return Result.success();
    }
    
    /**
     * 删除用户
     */
    @OperLog(title = "用户管理", businessType = "USER_DELETE")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }
}
```

### 示例 2：网关访问日志输出

**请求**:
```bash
GET /api/system/v1/getUserInfo
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**日志输出**:
```log
2026-04-28 10:30:45.123 INFO  【访问日志】GET /api/system/v1/getUserInfo | IP: 192.168.1.100 | User: admin(1)
2026-04-28 10:30:45.168 INFO  【访问日志完成】GET /api/system/v1/getUserInfo | 耗时：45ms | 状态：200
```

### 示例 3：操作日志数据库记录

执行上述 Controller 方法后，数据库 `sys_oper_log` 表中会新增一条记录：

```sql
SELECT * FROM sys_oper_log ORDER BY oper_time DESC LIMIT 1;

-- 结果示例：
-- id: 1234567890
-- title: 用户管理
-- business_type: USER_ADD
-- method: com.hivecloud.system.controller.SysUserController.add
-- request_method: POST
-- oper_name: admin
-- oper_ip: 192.168.1.100
-- oper_url: /api/system/v1/user/add
-- oper_param: {"username":"test","nickname":"测试用户"}
-- status: 0 (正常)
-- oper_time: 1714291845123
-- cost_time: 45
```

---

## 🎯 最佳实践

### 1. 网关层日志

- ✅ 使用 `AccessLogFilter` 记录所有请求
- ✅ 记录关键信息：IP、方法、URL、耗时
- ✅ 使用异步日志，避免阻塞请求
- ❌ 不要在网关记录详细业务日志

### 2. 业务层日志

- ✅ 使用 `@OperLog` 注解记录关键操作
- ✅ 记录请求参数和响应结果（敏感信息除外）
- ✅ 记录操作状态和错误信息
- ❌ 避免记录密码等敏感信息

### 3. 日志级别选择

| 场景 | 推荐级别 | 说明 |
|------|---------|------|
| 访问日志 | INFO | 记录所有请求 |
| 认证失败 | WARN | 记录异常但非错误 |
| 系统异常 | ERROR | 记录严重错误 |
| 调试信息 | DEBUG | 开发环境使用 |

---

## 📊 日志查询

### 通过 API 查询

```bash
# 分页查询操作日志
GET /api/system/v1/oper-log?pageNum=1&pageSize=10

# 按标题搜索
GET /api/system/v1/oper-log?pageNum=1&pageSize=10&title=用户管理

# 按操作人员搜索
GET /api/system/v1/oper-log?pageNum=1&pageSize=10&operName=admin
```

### 通过日志文件查询

```bash
# 查看网关访问日志
tail -f hivecloud-gateway/logs/app.log | grep "访问日志"

# 搜索特定 IP 的请求
grep "192.168.1.100" hivecloud-gateway/logs/app.log

# 统计某接口的平均耗时
grep "GET /api/system/v1/getUserInfo" hivecloud-gateway/logs/app.log | awk '{print $NF}' | awk -F: '{sum+=$2} END {print sum/NR}'
```

---

## 🔒 安全注意事项

1. **敏感信息过滤**
   - 密码、Token 等敏感字段不要记录
   - 使用 `isSaveRequestData = false` 禁用请求数据保存

2. **日志脱敏**
   - 手机号、身份证号等个人信息需要脱敏
   - 使用工具类进行脱敏处理

3. **日志权限控制**
   - 操作日志查询接口需要权限控制
   - 只允许管理员查看日志

---

## 📈 性能优化

1. **异步记录日志**
   ```java
   @Async
   public void saveLog(SysOperLog log) {
       // 异步保存日志
   }
   ```

2. **批量插入日志**
   ```java
   // 使用 MyBatis 批量插入
   int batchInsert(List<SysOperLog> logs);
   ```

3. **日志清理策略**
   ```sql
   -- 定期删除 30 天前的日志
   DELETE FROM sys_oper_log WHERE oper_time < UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 30 DAY));
   ```

---

## ✅ 总结

### 网关日志记录方式

| 组件 | 用途 | 存储方式 | 适用场景 |
|------|------|---------|---------|
| `AccessLogFilter` | 访问日志 | 日志文件 | 网关层，记录所有请求 |
| `JwtAuthFilter` | 认证日志 | 日志文件 | 网关层，记录认证失败 |
| `RateLimitFilter` | 限流日志 | 日志文件 | 网关层，记录限流异常 |
| `@OperLog` + `OperLogAspect` | 操作日志 | 数据库 | 业务层，记录关键操作 |

### 推荐方案

- **网关模块**: 使用 `AccessLogFilter` 记录访问日志
- **业务模块**: 使用 `hivecloud-plugin-log` 记录操作日志
- **生产环境**: 混合模式，网关日志文件 + 业务日志数据库
