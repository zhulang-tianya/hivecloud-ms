# HiveCloud MS 编译脚本
# 使用指定的 JDK 17 和 Maven 3.9.6

$env:JAVA_HOME = "C:\work\java\jdk-17\jdk-17.0.9"
$env:PATH = "C:\work\java\jdk-17\jdk-17.0.9\bin;C:\work\apache-maven-3.9.6\bin;" + $env:PATH

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "HiveCloud MS 编译脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Java 版本：" -NoNewline
& "$env:JAVA_HOME\bin\java.exe" -version
Write-Host "Maven 版本：" -NoNewline
& "mvn" -version

Write-Host ""
Write-Host "开始编译项目..." -ForegroundColor Green
Write-Host ""

# 切换到脚本所在目录
Set-Location $PSScriptRoot

# 执行 Maven 编译
& mvn clean install -DskipTests

Write-Host ""
Write-Host "编译完成！" -ForegroundColor Green
