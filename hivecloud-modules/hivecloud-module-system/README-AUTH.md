# 用户认证中心 - 快速使用指南

## 🚀 快速开始

### 1. 启动应用

```bash
cd hivecloud-modules/hivecloud-module-system
mvn clean install
java -jar target/hivecloud-module-system.jar
```

### 2. 访问 Knife4j 在线文档

打开浏览器访问：
```
http://localhost:8080/doc.html
```

**注意**：项目使用 Knife4j（Swagger 增强 UI），不是 Swagger UI。

### 3. 测试登录接口

#### 步骤 1：获取验证码
```bash
curl -X GET http://localhost:8080/auth/captcha
```

响应示例：
```json
{
  "captchaKey": "550e8400-e29b-41d4-a716-446655440000",
  "captchaImage": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHgAAAAoCAYAAAA...",
  "expiration": 300
}
```

#### 步骤 2：登录
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456",
    "captcha": "10+5=",
    "captchaKey": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

响应示例：
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJ0ZW5hbnRJZCI6MSwiaWF0IjoxNzE1MjM0NTY3LCJleHAiOjE3MTUyMzYzNjd9.abc123",
  "expiresIn": 1800,
  "userInfo": {
    "id": 1,
    "username": "admin",
    "nickname": "管理员",
    "avatar": "https://example.com/avatar.jpg",
    "roles": ["admin"],
    "permissions": ["system:user:query", "system:user:add", "system:role:query"]
  }
}
```

#### 步骤 3：访问受保护接口
```bash
curl -X GET http://localhost:8080/system/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 📋 API 清单

### 认证管理

| 接口 | 方法 | 描述 | 需要认证 |
|------|------|------|---------|
| `/auth/captcha` | GET | 获取验证码 | ❌ |
| `/auth/login` | POST | 用户登录 | ❌ |
| `/auth/logout` | POST | 用户注销 | ✅ |
| `/auth/refresh` | POST | 刷新 Token | ✅ |

### 个人中心

| 接口 | 方法 | 描述 | 需要认证 |
|------|------|------|---------|
| `/system/profile` | GET | 获取个人信息 | ✅ |
| `/system/profile` | PUT | 更新个人信息 | ✅ |
| `/system/profile/password` | PUT | 修改密码 | ✅ |
| `/system/profile/avatar` | POST | 上传头像 | ✅ |

---

## 🔧 配置说明

### JWT 配置（application.yml）

```yaml
jwt:
  secret: HiveCloud-Secret-Key-2026-For-JWT-Token-Signing  # 生产环境请使用环境变量
  expiration: 1800000  # 30 分钟
  refresh-expiration: 300000  # 刷新窗口 5 分钟
```

### Redis 配置

确保 Redis 可用，用于：
- 验证码存储（5 分钟过期）
- Token 黑名单（剩余有效期）

---

## 💻 前端集成示例

### Vue 3 示例

```javascript
// src/api/auth.js
import request from '@/utils/request'

// 获取验证码
export function getCaptcha() {
  return request({
    url: '/auth/captcha',
    method: 'get'
  })
}

// 登录
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 注销
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

// 获取个人信息
export function getProfile() {
  return request({
    url: '/system/profile',
    method: 'get'
  })
}
```

```javascript
// src/utils/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 检查是否需要刷新 Token
    const newToken = response.headers['x-new-token']
    if (newToken) {
      localStorage.setItem('token', newToken)
    }
    
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.msg || 'Error')
      return Promise.reject(new Error(res.msg || 'Error'))
    }
    return res
  },
  error => {
    if (error.response.status === 401) {
      ElMessage.error('Token 无效或已过期')
      localStorage.removeItem('token')
      location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default service
```

```vue
<!-- src/views/login/index.vue -->
<template>
  <div class="login-container">
    <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef">
      <el-form-item prop="username">
        <el-input v-model="loginForm.username" placeholder="用户名" />
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="loginForm.password" type="password" placeholder="密码" />
      </el-form-item>
      <el-form-item prop="captcha">
        <el-input v-model="loginForm.captcha" placeholder="验证码" style="width: 60%">
          <template #append>
            <img :src="captchaImage" @click="refreshCaptcha" style="cursor: pointer" />
          </template>
        </el-input>
      </el-form-item>
      <el-button type="primary" @click="handleLogin" :loading="loading">
        登录
      </el-button>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCaptcha, login } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const captchaImage = ref('')

const loginForm = reactive({
  username: '',
  password: '',
  captcha: '',
  captchaKey: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

// 获取验证码
const refreshCaptcha = async () => {
  const res = await getCaptcha()
  loginForm.captchaKey = res.captchaKey
  captchaImage.value = res.captchaImage
}

// 登录
const handleLogin = async () => {
  loading.value = true
  try {
    const res = await login(loginForm)
    localStorage.setItem('token', res.token)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error(error)
    refreshCaptcha() // 登录失败刷新验证码
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>
```

---

## 🔐 安全建议

### 1. 生产环境配置

```yaml
# 使用环境变量
jwt:
  secret: ${JWT_SECRET:}  # 从环境变量读取
  
# 启用 HTTPS
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: your-password
```

### 2. 密码策略

- 最小长度 6 位（建议 8 位以上）
- 包含大小写字母、数字、特殊字符
- 3 个月强制过期
- 登录失败 5 次锁定 30 分钟（待实现）

### 3. Token 安全

- 使用 HTTPS 传输
- Token 存储在 localStorage（或 sessionStorage）
- 注销时加入黑名单
- 定期刷新 Token

---

## ❓ 常见问题

### Q: 验证码不显示？
A: 检查 Redis 是否正常运行，验证码依赖 Redis 存储。

### Q: 登录后访问接口提示 401？
A: 检查 Authorization 请求头格式是否正确（Bearer {token}）。

### Q: Token 如何自动刷新？
A: 监听响应头 `X-New-Token`，如果存在则替换 localStorage 中的旧 Token。

### Q: 如何禁用验证码？
A: 修改配置 `captcha.enabled: false`（不推荐生产环境）。

---

## 📚 更多文档

- [完整设计文档](../../.trae/changes/20260509-user-auth-center/02-DESIGN.md)
- [测试报告](../../.trae/changes/20260509-user-auth-center/05-TEST.md)
- [集成文档](../../.trae/changes/20260509-user-auth-center/99-INTEGRATION.md)
