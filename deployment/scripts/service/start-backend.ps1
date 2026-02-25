# WMS系统后端服务启动脚本
# 功能：编译和启动Spring Boot后端服务

Write-Host "==============================================="
Write-Host "WMS系统后端服务启动脚本"
Write-Host "==============================================="

# 定义后端项目路径
$backendPath = "..\..\wms-project"

# 检查后端项目是否存在
if (-not (Test-Path $backendPath)) {
    Write-Host "错误：后端项目路径不存在：$backendPath" -ForegroundColor Red
    exit 1
}

# 切换到后端项目目录
Set-Location $backendPath

# 检查Maven是否可用
$mvnVersion = & mvn --version 2>$null
if (-not $mvnVersion) {
    Write-Host "错误：Maven未安装或未添加到环境变量" -ForegroundColor Red
    exit 1
}

Write-Host "Maven版本：$($mvnVersion -split '\n' | Select-Object -First 1)" -ForegroundColor Green

# 清理项目
Write-Host "\n1. 清理项目..." -ForegroundColor Yellow
& mvn clean

# 编译项目
Write-Host "\n2. 编译项目..." -ForegroundColor Yellow
& mvn compile

# 运行项目
Write-Host "\n3. 启动后端服务..." -ForegroundColor Yellow
& mvn spring-boot:run
