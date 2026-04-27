# HiveCloud API 文档

> **版本**: 1.0.0  
> **更新日期**: 2026-04-27  
> **基础路径**: `/api`

本文档介绍 HiveCloud 提供的所有 RESTful API 接口。

## 📋 目录

- [认证授权](#认证授权)
- [用户管理](#用户管理)
- [支付服务](#支付服务)
- [理赔服务](#理赔服务)
- [活动服务](#活动服务)
- [通知服务](#通知服务)
- [通用响应格式](#通用响应格式)
- [错误码说明](#错误码说明)

## 🔐 认证授权

### JWT Token 获取

**接口**: `POST /api/auth/login`

**请求参数**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200,
    "tokenType": "Bearer"
  }
}
```

**使用说明**:
在请求头中添加 Authorization：
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 👤 用户管理

### 获取用户列表

**接口**: `GET /api/system/users`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| size | int | 否 | 每页数量，默认 10 |
| username | string | 否 | 用户名模糊查询 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "list": [
      {
        "id": 1,
        "username": "admin",
        "email": "admin@example.com",
        "phone": "13800138000",
        "status": 1,
        "createTime": "2026-04-27 10:00:00"
      }
    ]
  }
}
```

### 创建用户

**接口**: `POST /api/system/users`

**请求参数**:
```json
{
  "username": "testuser",
  "password": "test123",
  "email": "test@example.com",
  "phone": "13800138000",
  "status": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 101,
    "username": "testuser"
  }
}
```

### 更新用户

**接口**: `PUT /api/system/users/{id}`

**请求参数**:
```json
{
  "email": "newemail@example.com",
  "phone": "13900139000",
  "status": 1
}
```

### 删除用户

**接口**: `DELETE /api/system/users/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

## 💳 支付服务

### 创建支付订单

**接口**: `POST /api/payment/create`

**请求参数**:
```json
{
  "orderNo": "ORDER20260427001",
  "amount": 100.00,
  "subject": "测试商品",
  "body": "这是一个测试商品",
  "paymentMethod": "alipay",
  "notifyUrl": "http://example.com/notify",
  "returnUrl": "http://example.com/return",
  "userId": 1001
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "paymentId": 1234567890,
    "orderNo": "ORDER20260427001",
    "amount": 100.00,
    "paymentMethod": "alipay",
    "paymentStatus": 0,
    "paymentStatusDesc": "待支付",
    "payUrl": "https://openapi.alipay.com/gateway.do?xxx"
  }
}
```

### 查询支付订单

**接口**: `GET /api/payment/query/{orderNo}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "paymentId": 1234567890,
    "orderNo": "ORDER20260427001",
    "transactionId": "202604270001",
    "amount": 100.00,
    "paymentMethod": "alipay",
    "paymentStatus": 2,
    "paymentStatusDesc": "支付成功",
    "paymentTime": "2026-04-27 10:30:00"
  }
}
```

### 退款

**接口**: `POST /api/payment/refund/{orderNo}`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| amount | decimal | 是 | 退款金额 |
| reason | string | 否 | 退款原因 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "paymentId": 1234567890,
    "orderNo": "ORDER20260427001",
    "paymentStatus": 5,
    "paymentStatusDesc": "已退款"
  }
}
```

### 关闭订单

**接口**: `POST /api/payment/close/{orderNo}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "paymentId": 1234567890,
    "orderNo": "ORDER20260427001",
    "paymentStatus": 4,
    "paymentStatusDesc": "已关闭"
  }
}
```

## 🏥 理赔服务

### 创建理赔申请

**接口**: `POST /api/claim/create`

**请求参数**:
```json
{
  "policyNo": "POLICY20260427001",
  "userId": 1001,
  "claimType": "medical",
  "claimAmount": 5000.00,
  "accidentTime": "2026-04-26 10:00:00",
  "remark": "因病住院治疗"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": "CLAIM20260427001"
}
```

### 查询理赔申请

**接口**: `GET /api/claim/query/{claimNo}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "claimNo": "CLAIM20260427001",
    "policyNo": "POLICY20260427001",
    "userId": 1001,
    "claimType": "medical",
    "claimAmount": 5000.00,
    "claimStatus": 0,
    "claimStatusDesc": "待审核",
    "applyTime": "2026-04-27 10:00:00"
  }
}
```

### 审核理赔申请

**接口**: `POST /api/claim/audit/{claimNo}`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| auditorId | long | 是 | 审核人 ID |
| opinion | string | 是 | 审核意见 |
| approved | boolean | 是 | 是否通过 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 理赔打款

**接口**: `POST /api/claim/payment/{claimNo}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 查询用户理赔列表

**接口**: `GET /api/claim/list/{userId}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "claimNo": "CLAIM20260427001",
      "claimType": "medical",
      "claimAmount": 5000.00,
      "claimStatus": 2,
      "claimStatusDesc": "审核通过"
    }
  ]
}
```

## 🎁 活动服务

### 创建优惠券

**接口**: `POST /api/activity/coupons`

**请求参数**:
```json
{
  "couponNo": "COUPON20260427001",
  "couponName": "新人优惠券",
  "amount": 100.00,
  "type": 1,
  "validFrom": "2026-04-27 00:00:00",
  "validTo": "2026-12-31 23:59:59",
  "status": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "couponNo": "COUPON20260427001"
  }
}
```

### 领取优惠券

**接口**: `POST /api/activity/coupons/{couponId}/claim`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | long | 是 | 用户 ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

## 📱 通知服务

### 发送短信

**接口**: `POST /api/notify/sms/send`

**请求参数**:
```json
{
  "phone": "13800138000",
  "message": "您的验证码是：123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 发送验证码

**接口**: `POST /api/notify/sms/code`

**请求参数**:
```json
{
  "phone": "13800138000"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "code": "123456",
    "expiresIn": 300
  }
}
```

## 📊 通用响应格式

### 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1682582400000
}
```

### 错误响应

```json
{
  "code": 500,
  "message": "系统内部错误",
  "data": null,
  "timestamp": 1682582400000
}
```

### 分页响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "list": [],
    "page": 1,
    "size": 10
  },
  "timestamp": 1682582400000
}
```

## 🔢 错误码说明

### 通用错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 系统内部错误 |
| 503 | 服务不可用 |

### 业务错误码

| 错误码 | 说明 |
|--------|------|
| 1001 | 用户不存在 |
| 1002 | 密码错误 |
| 1003 | 用户已被禁用 |
| 2001 | 订单不存在 |
| 2002 | 订单状态异常 |
| 2003 | 订单已支付 |
| 3001 | 理赔申请不存在 |
| 3002 | 理赔状态不允许操作 |

## 🔍 Swagger UI

所有 API 接口都提供了 Swagger UI 文档，访问地址：

```
http://localhost:8081/swagger-ui.html
```

## 📝 使用示例

### cURL 示例

```bash
# 获取 JWT Token
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 创建支付订单
curl -X POST http://localhost:8081/api/payment/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "orderNo": "ORDER20260427001",
    "amount": 100.00,
    "subject": "测试商品",
    "paymentMethod": "alipay",
    "userId": 1001
  }'

# 查询支付订单
curl -X GET http://localhost:8081/api/payment/query/ORDER20260427001 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### JavaScript 示例

```javascript
// 获取 Token
const login = async () => {
  const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      username: 'admin',
      password: 'admin123'
    })
  });
  const data = await response.json();
  return data.data.token;
};

// 创建支付订单
const createPayment = async (token, orderData) => {
  const response = await fetch('/api/payment/create', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(orderData)
  });
  return await response.json();
};

// 使用示例
const token = await login();
const result = await createPayment(token, {
  orderNo: 'ORDER20260427001',
  amount: 100.00,
  subject: '测试商品',
  paymentMethod: 'alipay',
  userId: 1001
});
```

### Java 示例

```java
@RestController
@RequestMapping("/api/example")
public class ExampleController {

    @Resource
    private RestTemplate restTemplate;

    @Value("${hivecloud.api.base-url}")
    private String baseUrl;

    public PaymentResponse createPayment(PaymentRequest request) {
        // 获取 Token
        String token = login();

        // 创建支付订单
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<PaymentRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<Result<PaymentResponse>> response = restTemplate.exchange(
            baseUrl + "/api/payment/create",
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<Result<PaymentResponse>>() {}
        );

        return response.getBody().getData();
    }

    private String login() {
        // 实现登录逻辑
        return "your_token";
    }
}
```

## 📚 参考资料

- [RESTful API 设计最佳实践](https://restfulapi.net/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [JSON API](https://jsonapi.org/)

---

**如有问题，请查看 Swagger UI 或联系开发团队。**
