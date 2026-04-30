# Nacos 集成验证脚本
# 用于验证 Nacos 配置是否正确

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Nacos 集成验证脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Nacos 配置
$nacosServer = "172.10.6.194:8848"
$nacosUser = "nacos"
$nacosPassword = "nacos"

Write-Host "Nacos 服务器：$nacosServer" -ForegroundColor Yellow
Write-Host "用户名：$nacosUser" -ForegroundColor Yellow
Write-Host ""

# 1. 检查 Nacos 服务是否可访问
Write-Host "[1/5] 检查 Nacos 服务连通性..." -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "http://$nacosServer/nacos/" -TimeoutSec 5 -UseBasicParsing
    if ($response.StatusCode -eq 200) {
        Write-Host "✓ Nacos 服务可访问" -ForegroundColor Green
    } else {
        Write-Host "✗ Nacos 服务响应异常：$($response.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 无法连接到 Nacos 服务：$($_.Exception.Message)" -ForegroundColor Red
    Write-Host "请检查：" -ForegroundColor Yellow
    Write-Host "  1. Nacos 服务是否启动" -ForegroundColor Yellow
    Write-Host "  2. 网络是否连通" -ForegroundColor Yellow
    Write-Host "  3. 防火墙是否开放 8848 端口" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# 2. 检查 Nacos 登录接口
Write-Host "[2/5] 检查 Nacos 认证配置..." -ForegroundColor Cyan
try {
    $loginUrl = "http://$nacosServer/nacos/v1/auth/users/login"
    $body = "username=$nacosUser&password=$nacosPassword"
    $headers = @{
        "Content-Type" = "application/x-www-form-urlencoded"
    }
    $response = Invoke-WebRequest -Uri $loginUrl -Method POST -Body $body -Headers $headers -TimeoutSec 5 -UseBasicParsing
    if ($response.StatusCode -eq 200) {
        Write-Host "✓ Nacos 用户名密码正确" -ForegroundColor Green
    } else {
        Write-Host "✗ Nacos 认证失败：$($response.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Nacos 认证异常：$($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 3. 检查项目配置文件
Write-Host "[3/5] 检查项目配置文件..." -ForegroundColor Cyan
$configFiles = @(
    "hivecloud-gateway\src\main\resources\bootstrap.yml",
    "hivecloud-modules\hivecloud-module-system\src\main\resources\bootstrap.yml"
)

foreach ($file in $configFiles) {
    $filePath = Join-Path $PSScriptRoot ".." $file
    if (Test-Path $filePath) {
        Write-Host "✓ 配置文件存在：$file" -ForegroundColor Green
        
        # 检查是否包含 Nacos 配置
        $content = Get-Content $filePath -Raw
        if ($content -match "172\.10\.6\.194:8848") {
            Write-Host "  ✓ 包含正确的 Nacos 地址" -ForegroundColor Green
        } else {
            Write-Host "  ✗ 未找到 Nacos 地址配置" -ForegroundColor Red
        }
    } else {
        Write-Host "✗ 配置文件不存在：$file" -ForegroundColor Red
    }
}

Write-Host ""

# 4. 检查 Nacos 配置示例文件
Write-Host "[4/5] 检查 Nacos 配置示例文件..." -ForegroundColor Cyan
$nacosConfigs = @(
    "deploy\nacos-configs\hivecloud-common.yaml",
    "deploy\nacos-configs\hivecloud-gateway.yaml",
    "deploy\nacos-configs\hivecloud-system.yaml"
)

foreach ($file in $nacosConfigs) {
    $filePath = Join-Path $PSScriptRoot ".." $file
    if (Test-Path $filePath) {
        Write-Host "✓ 配置示例存在：$file" -ForegroundColor Green
    } else {
        Write-Host "✗ 配置示例不存在：$file" -ForegroundColor Red
    }
}

Write-Host ""

# 5. 检查 Maven 依赖
Write-Host "[5/5] 检查 Maven 依赖配置..." -ForegroundColor Cyan
$pomFiles = @(
    "hivecloud-gateway\pom.xml",
    "hivecloud-modules\hivecloud-module-system\pom.xml"
)

foreach ($file in $pomFiles) {
    $filePath = Join-Path $PSScriptRoot ".." $file
    if (Test-Path $filePath) {
        $content = Get-Content $filePath -Raw
        if ($content -match "spring-cloud-starter-alibaba-nacos") {
            Write-Host "✓ 已添加 Nacos 依赖：$file" -ForegroundColor Green
        } else {
            Write-Host "✗ 未添加 Nacos 依赖：$file" -ForegroundColor Red
        }
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "验证完成！" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "下一步操作：" -ForegroundColor Yellow
Write-Host "1. 登录 Nacos 控制台：http://172.10.6.194:8848/nacos" -ForegroundColor White
Write-Host "2. 在配置管理中导入 deploy\nacos-configs 目录下的配置文件" -ForegroundColor White
Write-Host "3. 启动 Gateway 服务：cd hivecloud-gateway && mvn spring-boot:run" -ForegroundColor White
Write-Host "4. 启动 System 服务：cd hivecloud-modules\hivecloud-module-system && mvn spring-boot:run" -ForegroundColor White
Write-Host "5. 在 Nacos 控制台查看服务注册情况" -ForegroundColor White
Write-Host ""
