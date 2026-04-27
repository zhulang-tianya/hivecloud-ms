# 事务管理使用指南

> **文档版本**: v1.0.0  
> **创建日期**: 2026-04-27  
> **最后更新**: 2026-04-27

---

## 📋 概述

HiveCloud 项目采用 Spring 的 `@Transactional` 注解进行事务管理，确保数据操作的原子性、一致性、隔离性和持久性（ACID）。所有 Service 层实现类都必须添加事务注解。

## 🎯 核心原则

### 强制规则

1. ✅ **所有 Service 实现类必须添加 `@Transactional(rollbackFor = Exception.class)`**
2. ✅ **只读查询方法必须添加 `@Transactional(readOnly = true)` 优化性能**
3. ✅ **写操作方法使用默认的读写事务**
4. ✅ **事务注解必须声明 `rollbackFor = Exception.class`**
5. ❌ **禁止在 Controller 层使用事务注解**
6. ❌ **禁止在事务方法内捕获异常不抛出**

## 📦 事务配置

### 类级别事务

所有 Service 实现类在类级别声明事务：

```java
@Service
@Transactional(rollbackFor = Exception.class)
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    // ...
}
```

**说明**：
- `@Service`：Spring Service 注解
- `@Transactional(rollbackFor = Exception.class)`：所有异常都回滚事务

### 方法级别事务

#### 只读查询方法

```java
@Override
@Transactional(readOnly = true)
public UserVO getUserInfo(Long userId) {
    SysUser user = getById(userId);
    if (user == null) {
        return null;
    }
    return UserMapper.INSTANCE.toVO(user);
}
```

**优化效果**：
- 只读事务不会获取数据库锁
- 减少事务日志生成
- 提升查询性能 30%-50%

#### 写操作方法

```java
@Override
public void createUser(UserCreateRequest request) {
    // 检查用户名唯一性
    boolean isExist = userMapper.existUserName(request.getUsername());
    if (isExist) {
        throw new BusinessException("用户名已存在");
    }
    
    // 保存用户
    SysUser user = new SysUser();
    user.setUsername(request.getUsername());
    save(user);
    
    // 保存用户角色关联
    saveUserRoles(user.getId(), request.getRoleIds());
}
```

**说明**：
- 继承类级别的 `@Transactional(rollbackFor = Exception.class)`
- 任何异常都会回滚整个事务

## 🔧 事务传播行为

### 默认传播行为（REQUIRED）

```java
@Service
public class OrderServiceImpl implements OrderService {
    
    @Resource
    private UserService userService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(OrderRequest request) {
        // 1. 创建订单
        Order order = create(request);
        
        // 2. 扣减库存（传播到当前事务）
        userService.deductStock(request.getUserId(), request.getAmount());
        
        // 3. 如果任何一步失败，整个事务回滚
    }
}
```

### 需要新事务（REQUIRES_NEW）

```java
@Override
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void saveLog(String message) {
    // 日志保存独立事务，不受主事务影响
    logMapper.insert(message);
}
```

**使用场景**：
- 日志记录
- 审计操作
- 通知发送

## ⚠️ 事务失效场景

### 1. 方法访问修饰符错误

```java
// ❌ 错误：private 方法事务失效
@Transactional
private void privateMethod() {
    // ...
}

// ✅ 正确：public 方法
@Transactional
public void publicMethod() {
    // ...
}
```

### 2. 同类方法调用

```java
@Service
public class UserServiceImpl implements UserService {
    
    @Override
    public void createUser(UserRequest request) {
        // ❌ 错误：同类方法调用，事务失效
        saveUser(request);
    }
    
    @Transactional
    public void saveUser(UserRequest request) {
        // ...
    }
}
```

**解决方案**：
```java
@Override
public void createUser(UserRequest request) {
    // ✅ 正确：通过 AOP 代理调用
    ((UserService) AopContext.currentProxy()).saveUser(request);
}
```

### 3. 异常被捕获未抛出

```java
@Override
public void createUser(UserRequest request) {
    try {
        // 业务逻辑
        save(request);
    } catch (Exception e) {
        log.error("创建用户失败", e);
        // ❌ 错误：异常被捕获，事务不会回滚
    }
}
```

**解决方案**：
```java
@Override
public void createUser(UserRequest request) {
    try {
        save(request);
    } catch (Exception e) {
        log.error("创建用户失败", e);
        // ✅ 正确：抛出运行时异常
        throw new BusinessException("创建用户失败", e);
    }
}
```

### 4. 异常类型不匹配

```java
// ❌ 错误：只回滚 RuntimeException，不回滚 Exception
@Transactional(rollbackFor = RuntimeException.class)
public void createUser() {
    throw new Exception("业务异常"); // 不会回滚
}

// ✅ 正确：回滚所有 Exception
@Transactional(rollbackFor = Exception.class)
public void createUser() {
    throw new Exception("业务异常"); // 会回滚
}
```

## 📊 事务隔离级别

### 数据库默认隔离级别

```java
// 使用数据库默认隔离级别（推荐）
@Transactional(isolation = Isolation.DEFAULT)
public void defaultIsolation() {
    // ...
}
```

### 读已提交（READ_COMMITTED）

```java
// 防止脏读
@Transactional(isolation = Isolation.READ_COMMITTED)
public void readCommitted() {
    // ...
}
```

### 可重复读（REPEATABLE_READ）

```java
// 防止脏读和不可重复读
@Transactional(isolation = Isolation.REPEATABLE_READ)
public void repeatableRead() {
    // ...
}
```

## 🧪 测试验证

### 单元测试

```java
@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;
    
    @Test
    void testTransactionRollback() {
        // 准备数据
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("test");
        
        // 执行操作（会抛出异常）
        assertThrows(BusinessException.class, () -> {
            userService.createUserWithException(request);
        });
        
        // 验证数据已回滚
        List<SysUser> users = userMapper.selectList(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "test")
        );
        assertTrue(users.isEmpty(), "事务应该回滚，数据库中不应有测试数据");
    }
}
```

### 集成测试

```java
@Test
@Transactional
@DisplayName("事务回滚测试")
void testTransactionRollback() {
    // 1. 创建用户（正常）
    UserCreateRequest request = new UserCreateRequest();
    request.setUsername("test_user");
    Long userId = userService.createUser(request);
    assertNotNull(userId);
    
    // 2. 创建重复用户名的用户（应失败并回滚）
    request.setId(userId);
    assertThrows(BusinessException.class, () -> {
        userService.createUserWithDuplicateCheck(request);
    });
    
    // 3. 验证数据一致性
    SysUser user = userService.getById(userId);
    assertNotNull(user);
    assertEquals("test_user", user.getUsername());
}
```

## 📈 性能优化

### 1. 只读事务优化

```java
// ✅ 推荐：只读查询使用 readOnly = true
@Transactional(readOnly = true)
public List<SysUser> listUsers() {
    return list();
}

// ❌ 不推荐：读写事务用于查询
@Transactional
public List<SysUser> listUsers() {
    return list();
}
```

**性能提升**：
- 减少数据库锁竞争
- 降低事务日志开销
- 提升并发性能 30%-50%

### 2. 大事务拆分

```java
// ❌ 错误：大事务包含无关操作
@Transactional
public void createOrder(OrderRequest request) {
    // 1. 创建订单（数据库操作）
    Order order = create(request);
    
    // 2. 发送短信（耗时操作，不应在事务内）
    smsService.send(order.getPhone(), "订单创建成功");
    
    // 3. 发送邮件（耗时操作，不应在事务内）
    emailService.send(order.getEmail(), "订单创建成功");
}

// ✅ 正确：拆分事务
@Transactional
public Order createOrder(OrderRequest request) {
    Order order = create(request);
    return order;
}

public void notifyUser(Order order) {
    // 异步通知，不在事务内
    CompletableFuture.runAsync(() -> {
        smsService.send(order.getPhone(), "订单创建成功");
        emailService.send(order.getEmail(), "订单创建成功");
    });
}
```

### 3. 批量操作优化

```java
// ❌ 错误：循环内单条插入
@Transactional
public void batchInsert(List<User> users) {
    for (User user : users) {
        save(user); // 每次都要开启/提交事务
    }
}

// ✅ 正确：批量插入
@Transactional
public void batchInsert(List<User> users) {
    saveBatch(users); // 一次事务批量插入
}
```

## 🔍 监控与诊断

### 事务日志

在 `application-dev.yml` 中开启事务日志：

```yaml
logging:
  level:
    org.springframework.transaction: DEBUG
    org.hibernate.engine.transaction: DEBUG
```

### 慢事务监控

```java
@Aspect
@Component
@Slf4j
public class TransactionMonitorAspect {
    
    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object monitor(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            if (duration > 1000) {
                log.warn("慢事务警告：{}.{} 执行时间：{}ms",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    duration);
            }
        }
    }
}
```

## 📝 最佳实践总结

### ✅ 应该做的

1. 所有 Service 实现类添加 `@Transactional(rollbackFor = Exception.class)`
2. 只读查询方法添加 `@Transactional(readOnly = true)`
3. 事务方法内异常必须抛出，不能捕获后吞掉
4. 大事务拆分为小事务
5. 耗时操作（HTTP 请求、文件 IO）放在事务外
6. 批量操作使用 `saveBatch()` 而非循环 `save()`

### ❌ 不应该做的

1. 禁止在 Controller 层使用事务
2. 禁止在事务方法内捕获异常不抛出
3. 禁止事务内执行耗时操作
4. 禁止循环内单条数据库操作
5. 禁止使用 `rollbackFor = RuntimeException.class`
6. 禁止同类方法直接调用事务方法

## 🔗 相关文档

- [ARCH-RECHECK-004.md](../../reports/ARCH-RECHECK-004.md) - 架构代码复核报告
- [IDEMPOTENT-COMPONENT.md](./IDEMPOTENT-COMPONENT.md) - 幂等性组件使用指南

---

**作者**: HiveCloud Team  
**日期**: 2026-04-27  
**版本**: v1.0.0
