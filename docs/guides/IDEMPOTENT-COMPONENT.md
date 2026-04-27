# 幂等性组件使用指南

> **文档版本**: v1.0.0  
> **创建日期**: 2026-04-27  
> **最后更新**: 2026-04-27

---

## 📋 概述

幂等性组件基于 Redis SETNX 实现分布式锁机制，确保同一请求在分布式环境下只会被执行一次。适用于防止表单重复提交、接口重复调用等场景。

## 🎯 核心特性

- ✅ **分布式支持**：基于 Redis，支持集群环境下的幂等性保障
- ✅ **灵活配置**：支持自定义幂等键、过期时间、错误信息
- ✅ **AOP 无侵入**：基于注解实现，业务代码无感知
- ✅ **SpEL 表达式**：支持使用方法参数生成动态幂等键
- ✅ **重试策略**：支持允许重试和严格幂等两种模式

## 📦 组件结构

```
hivecloud-common-core
├── annotation
│   └── Idempotent.java          # 幂等性注解
├── exception
│   └── IdempotentException.java # 幂等性异常
├── interceptor
│   └── IdempotentInterceptor.java # 幂等性拦截器
└── test
    └── IdempotentInterceptorTest.java # 单元测试
```

## 🚀 快速开始

### 1. 添加依赖（已集成）

幂等性组件已集成在 `hivecloud-common-core` 模块中，无需额外添加依赖。

### 2. 使用注解

在需要保证幂等性的接口方法上添加 `@Idempotent` 注解：

```java
@Idempotent(key = "'order:create:' + #userId + ':' + #request.orderNo", expire = 86400)
@PostMapping("/orders")
public Result<Long> createOrder(@RequestBody OrderRequest request) {
    // 业务逻辑
    Long orderId = orderService.createOrder(request);
    return Result.success(orderId);
}
```

### 3. 配置说明

#### 注解参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `key` | String | 必填 | 幂等键的 SpEL 表达式 |
| `expire` | long | 86400 | 幂等键过期时间（秒） |
| `message` | String | "请求已处理，请勿重复提交" | 幂等性检查失败的错误信息 |
| `allowRetry` | boolean | false | 是否允许重试 |

#### SpEL 表达式语法

支持在表达式中引用：
- 方法参数：`#参数名`
- 请求对象：`#request`（HttpServletRequest）
- 系统方法：`T(类).方法()`

**示例**：
```java
// 引用单个参数
@Idempotent(key = "'user:login:' + #username")

// 引用多个参数
@Idempotent(key = "'order:pay:' + #userId + ':' + #orderId")

// 引用对象属性
@Idempotent(key = "'order:create:' + #request.userId + ':' + #request.orderNo")

// 引用系统方法
@Idempotent(key = "'dict:refresh:' + T(java.lang.System).currentTimeMillis() / 60000")

// 引用 request 对象
@Idempotent(key = "'auth:logout:' + #request.remoteAddr")
```

## 📖 使用场景

### 场景 1：防止表单重复提交

```java
@Idempotent(key = "'order:create:' + #userId + ':' + #request.orderNo", expire = 86400)
@PostMapping("/orders")
public Result<Long> createOrder(@RequestBody OrderRequest request) {
    return Result.success(orderService.createOrder(request));
}
```

### 场景 2：刷新缓存

```java
@Idempotent(key = "'dict:refresh:' + T(java.lang.System).currentTimeMillis() / 60000", expire = 60)
@PostMapping("/dict/refresh")
public Result<Void> refreshCache() {
    dictService.refreshCache();
    return Result.success();
}
```

### 场景 3：用户登出

```java
@Idempotent(key = "'auth:logout:' + #request.remoteAddr", expire = 60)
@PostMapping("/logout")
public Result<Void> logout() {
    SecurityContextHolder.clearContext();
    return Result.success();
}
```

### 场景 4：允许重试的场景

```java
// 允许重试：首次执行成功后，后续相同请求直接返回成功
@Idempotent(key = "'payment:notify:' + #orderId", expire = 3600, allowRetry = true)
@PostMapping("/payment/notify")
public Result<Void> paymentNotify(@RequestBody PaymentNotifyRequest request) {
    paymentService.processNotify(request);
    return Result.success();
}
```

## 🔧 工作原理

### 执行流程

```
1. 拦截器扫描@Idempotent 注解
   ↓
2. 解析 SpEL 表达式，生成幂等键
   ↓
3. 尝试 Redis SETNX 设置键
   ↓
4a. 设置失败 → 抛出幂等异常
4b. 设置成功 → 执行业务逻辑
   ↓
5a. 业务成功 → 根据 allowRetry 决定是否删除键
5b. 业务失败 → 删除键，允许重试
```

### 幂等键设计

**格式**：`hivecloud:idempotent:{业务类型}:{业务标识}:{时间戳}`

**示例**：
- `hivecloud:idempotent:order:create:user123:1001`
- `hivecloud:idempotent:dict:refresh:20260427103000`
- `hivecloud:idempotent:auth:logout:192.168.1.100`

**过期时间设计**：
- 订单创建：24 小时（86400 秒）
- 刷新缓存：1 分钟（60 秒）
- 用户登出：1 分钟（60 秒）
- 支付回调：1 小时（3600 秒）

## ⚠️ 注意事项

### 1. 幂等键设计

- ✅ 确保幂等键唯一性，包含足够的业务信息
- ✅ 设置合理的过期时间，防止 Redis 内存溢出
- ❌ 避免使用固定值作为幂等键

**正确示例**：
```java
@Idempotent(key = "'order:pay:' + #userId + ':' + #orderId")
```

**错误示例**：
```java
@Idempotent(key = "'order:pay'")  // 所有订单支付共用一个键
```

### 2. 过期时间设置

- ✅ 根据业务场景设置
- ✅ 考虑网络延迟和重试时间
- ❌ 避免设置过长（如 7 天、30 天）

**推荐值**：
- 表单提交：5-30 分钟
- 刷新操作：1-5 分钟
- 支付回调：1-24 小时
- 消息通知：1-24 小时

### 3. 异常处理

幂等性检查失败会抛出 `IdempotentException`，需要在全局异常处理器中捕获：

```java
@ExceptionHandler(IdempotentException.class)
public Result<Void> handleIdempotentException(IdempotentException e) {
    return Result.error(e.getCode(), e.getMessage());
}
```

### 4. 性能影响

- ✅ 单次 Redis 操作，耗时约 1-5ms
- ✅ 对业务性能影响可忽略
- ⚠️ 高并发场景下注意 Redis 集群压力

## 🧪 测试用例

### 单元测试

```java
@Test
@DisplayName("重复请求应该抛出幂等异常")
void testDuplicateRequest() {
    when(redisUtil.setIfAbsent(anyString(), anyString(), anyLong())).thenReturn(false);
    
    assertThrows(IdempotentException.class, () -> {
        interceptor.invoke(service, "testMethod", "param1");
    });
}
```

### 集成测试

```java
@Test
void testIdempotentApi() {
    // 第一次请求
    Result response1 = mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(orderJson))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    
    // 第二次请求（相同参数）
    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(orderJson))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message").value("请求已处理，请勿重复提交"));
}
```

## 📊 监控指标

### Redis Key 监控

```bash
# 查看幂等键数量
redis-cli KEYS "hivecloud:idempotent:*" | wc -l

# 查看即将过期的键
redis-cli --scan --pattern "hivecloud:idempotent:*" --count 1000
```

### 异常监控

在日志中搜索幂等异常：
```bash
grep "IdempotentException" application.log
```

## 🔗 相关文档

- [ARCH-RECHECK-004.md](../../reports/ARCH-RECHECK-004.md) - 架构代码复核报告
- [T1-OPTIMIZATION-TASK.md](../../optimization/T1-OPTIMIZATION-TASK.md) - T1 优化任务计划

---

**作者**: HiveCloud Team  
**日期**: 2026-04-27  
**版本**: v1.0.0
