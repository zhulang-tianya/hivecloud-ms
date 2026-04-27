# HiveCloud 部署指南

> **版本**: 1.0.0  
> **更新日期**: 2026-04-27  
> **适用环境**: 开发、测试、生产

本指南详细介绍 HiveCloud 在不同环境下的部署方案。

## 📋 目录

- [环境要求](#环境要求)
- [本地开发环境部署](#本地开发环境部署)
- [Docker 容器化部署](#docker 容器化部署)
- [Kubernetes 集群部署](#kubernetes 集群部署)
- [生产环境部署](#生产环境部署)
- [监控与告警](#监控与告警)
- [常见问题](#常见问题)

## 🔧 环境要求

### 基础软件

| 软件 | 版本要求 | 用途 |
|------|---------|------|
| JDK | 17+ | Java 运行环境 |
| Maven | 3.8+ | 项目构建 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 6.0+ | 缓存和服务注册 |

### 中间件

| 中间件 | 版本要求 | 用途 |
|--------|---------|------|
| Prometheus | 2.40+ | 监控指标收集 |
| Grafana | 9.0+ | 监控仪表盘 |
| Nginx | 1.20+ | 反向代理（可选） |

### 硬件要求

| 环境 | CPU | 内存 | 磁盘 |
|------|-----|------|------|
| 开发环境 | 2 核 | 4GB | 20GB |
| 测试环境 | 4 核 | 8GB | 50GB |
| 生产环境 | 8 核+ | 16GB+ | 100GB+ |

## 🖥️ 本地开发环境部署

### 1. 安装依赖

```bash
# 安装 JDK 17
# Ubuntu/Debian
sudo apt install openjdk-17-jdk

# macOS
brew install openjdk@17

# 安装 Maven
# Ubuntu/Debian
sudo apt install maven

# macOS
brew install maven
```

### 2. 克隆项目

```bash
git clone https://github.com/zhulang-tianya/hivecloud-ms.git
cd hivecloud-ms
```

### 3. 初始化数据库

```bash
mysql -u root -p << EOF
CREATE DATABASE hivecloud DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hivecloud;
source docs/guides/schema.sql;
EOF
```

### 4. 启动 Redis

```bash
# Linux/Mac
redis-server

# Windows
redis-server.exe
```

### 5. 配置环境

编辑 `application-dev.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hivecloud?useSSL=false&serverTimezone=UTC
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
    password:
```

### 6. 编译项目

```bash
mvn clean package -DskipTests
```

### 7. 启动服务

```bash
# 服务注册中心
java -jar hivecloud-framework/hivecloud-service-registry/target/*.jar

# 网关
java -jar hivecloud-gateway/target/*.jar

# 业务模块
java -jar hivecloud-modules/hivecloud-module-system/target/*.jar
```

## 🐳 Docker 容器化部署

### 1. 安装 Docker

```bash
# Ubuntu/Debian
curl -fsSL https://get.docker.com | sh
sudo systemctl enable docker
sudo systemctl start docker

# macOS
# 下载 Docker Desktop: https://www.docker.com/products/docker-desktop
```

### 2. 准备部署文件

```bash
cd deploy
```

### 3. 修改配置

编辑 `docker-compose.yml`，修改数据库密码等配置：

```yaml
services:
  mysql:
    environment:
      MYSQL_ROOT_PASSWORD: your_secure_password
```

### 4. 启动所有服务

```bash
docker-compose up -d
```

### 5. 查看服务状态

```bash
docker-compose ps
```

### 6. 查看日志

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f hivecloud-ms
```

### 7. 停止服务

```bash
docker-compose down
```

### 8. 清理数据（谨慎使用）

```bash
# 停止并删除所有容器、网络
docker-compose down -v
```

## ☸️ Kubernetes 集群部署

### 1. 准备 K8s 集群

```bash
# 使用 Minikube 本地测试
minikube start

# 或使用云服务商 K8s 集群
```

### 2. 创建命名空间

```bash
kubectl create namespace hivecloud
```

### 3. 创建 ConfigMap

```bash
kubectl create configmap hivecloud-config \
  --from-file=application.yml=./config/application.yml \
  -n hivecloud
```

### 4. 创建 Secret

```bash
kubectl create secret generic hivecloud-secrets \
  --from-literal=mysql-password='your_password' \
  --from-literal=redis-password='your_password' \
  -n hivecloud
```

### 5. 部署应用

```bash
# 部署 MySQL
kubectl apply -f k8s/mysql-deployment.yaml -n hivecloud

# 部署 Redis
kubectl apply -f k8s/redis-deployment.yaml -n hivecloud

# 部署服务注册中心
kubectl apply -f k8s/service-registry-deployment.yaml -n hivecloud

# 部署网关
kubectl apply -f k8s/gateway-deployment.yaml -n hivecloud

# 部署业务服务
kubectl apply -f k8s/module-deployment.yaml -n hivecloud
```

### 6. 查看部署状态

```bash
kubectl get pods -n hivecloud
kubectl get services -n hivecloud
kubectl get deployments -n hivecloud
```

### 7. 扩缩容

```bash
# 扩容到 3 个副本
kubectl scale deployment hivecloud-ms --replicas=3 -n hivecloud
```

### 8. 滚动更新

```bash
# 更新镜像
kubectl set image deployment/hivecloud-ms \
  hivecloud-ms=registry.example.com/hivecloud-ms:v1.1.0 \
  -n hivecloud

# 查看更新状态
kubectl rollout status deployment/hivecloud-ms -n hivecloud
```

## 🏭 生产环境部署

### 1. 服务器规划

| 服务器 | 用途 | 配置 | 数量 |
|--------|------|------|------|
| app-server-01 | 应用服务 | 8 核 16GB | 3 |
| db-server-01 | MySQL 主库 | 16 核 32GB | 1 |
| db-server-02 | MySQL 从库 | 16 核 32GB | 1 |
| cache-server-01 | Redis 主节点 | 8 核 16GB | 3 |
| monitor-server-01 | 监控服务 | 4 核 8GB | 1 |

### 2. 高可用配置

#### MySQL 主从复制

```sql
-- 主库配置
CHANGE MASTER TO
  MASTER_HOST='db-server-01',
  MASTER_USER='repl',
  MASTER_PASSWORD='repl_password',
  MASTER_LOG_FILE='mysql-bin.000001',
  MASTER_LOG_POS=0;

START SLAVE;
```

#### Redis 哨兵模式

```conf
sentinel monitor mymaster 127.0.0.1 6379 2
sentinel down-after-milliseconds mymaster 5000
sentinel failover-timeout mymaster 10000
```

### 3. 负载均衡配置

#### Nginx 配置

```nginx
upstream hivecloud_backend {
    server app-server-01:8080 weight=3;
    server app-server-02:8080 weight=3;
    server app-server-03:8080 weight=3;
}

server {
    listen 80;
    server_name api.example.com;

    location / {
        proxy_pass http://hivecloud_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 4. SSL 证书配置

```nginx
server {
    listen 443 ssl;
    server_name api.example.com;

    ssl_certificate /etc/nginx/ssl/api.example.com.crt;
    ssl_certificate_key /etc/nginx/ssl/api.example.com.key;

    location / {
        proxy_pass http://hivecloud_backend;
    }
}
```

## 📊 监控与告警

### 1. Prometheus 配置

已配置在 `deploy/prometheus.yml`：

```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'hivecloud-ms'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
```

### 2. 告警规则

已配置在 `deploy/alerts.yml`：

```yaml
groups:
  - name: hivecloud_alerts
    rules:
      - alert: HighCPUUsage
        expr: process_cpu_usage > 0.8
        for: 5m
        labels:
          severity: warning
```

### 3. Grafana 仪表盘

导入以下 Dashboard ID：
- JVM 监控：4701
- Spring Boot 监控：10280
- MySQL 监控：7362
- Redis 监控：11835

### 4. 告警通知

配置 Alertmanager 通知渠道：
- 邮件通知
- 钉钉通知
- 企业微信通知
- Slack 通知

## 🔍 常见问题

### 1. 服务启动失败

**检查项**：
- 端口是否被占用：`netstat -tlnp | grep 8080`
- 数据库连接是否正常：`mysql -h localhost -u root -p`
- Redis 连接是否正常：`redis-cli ping`

**解决方案**：
```bash
# 查看应用日志
tail -f logs/application.log

# 检查端口占用
lsof -i :8080

# 杀死占用端口的进程
kill -9 <PID>
```

### 2. 数据库连接池耗尽

**症状**：
```
HikariPool-1 - Connection is not available, request timed out after 30000ms
```

**解决方案**：
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20  # 增加最大连接数
      minimum-idle: 10       # 增加最小空闲连接
      connection-timeout: 30000
```

### 3. Redis 内存溢出

**症状**：
```
OOM command not allowed when used memory > 'maxmemory'
```

**解决方案**：
```conf
# redis.conf
maxmemory 2gb
maxmemory-policy allkeys-lru
```

### 4. 服务注册失败

**检查项**：
- Redis 是否正常运行
- 网络是否通畅
- 配置文件是否正确

**解决方案**：
```bash
# 重启 Redis
redis-cli shutdown
redis-server

# 重启服务
systemctl restart hivecloud-ms
```

### 5. 网关超时

**症状**：
```
504 Gateway Timeout
```

**解决方案**：
```yaml
spring:
  cloud:
    gateway:
      httpclient:
        connect-timeout: 10000
        response-timeout: 30s
```

## 📝 部署检查清单

### 部署前检查

- [ ] 服务器资源充足
- [ ] 网络配置正确
- [ ] 数据库已初始化
- [ ] Redis 已启动
- [ ] 配置文件已修改
- [ ] 备份策略已配置
- [ ] 监控告警已配置

### 部署后检查

- [ ] 所有服务正常运行
- [ ] 健康检查接口返回正常
- [ ] 日志无 ERROR 级别错误
- [ ] 监控指标正常收集
- [ ] 告警通知正常发送
- [ ] 性能测试通过

## 🎯 最佳实践

1. **使用环境变量**：敏感信息使用环境变量或配置中心
2. **日志轮转**：配置日志轮转避免磁盘占满
3. **健康检查**：配置健康检查端点
4. **优雅停机**：配置优雅停机避免数据丢失
5. **限流降级**：配置限流和降级策略
6. **备份恢复**：定期备份数据库和配置文件

## 📚 参考资料

- [Docker 官方文档](https://docs.docker.com/)
- [Kubernetes 官方文档](https://kubernetes.io/docs/)
- [Prometheus 官方文档](https://prometheus.io/docs/)
- [Spring Boot 部署指南](https://spring.io/guides/gs/spring-boot/)

---

**祝您部署成功！** 🎉
