# WMS系统前端服务启动脚本
# 功能：安装依赖和启动Vue前端服务

Write-Host "==============================================="
Write-Host "WMS系统前端服务启动脚本"
Write-Host "==============================================="

# 定义前端项目路径
$frontendPath = "..\..\wms-frontend"

# 检查前端项目是否存在
if (-not (Test-Path $frontendPath)) {
    Write-Host "错误：前端项目路径不存在：$frontendPath" -ForegroundColor Red
    exit 1
}

# 切换到前端项目目录
Set-Location $frontendPath

# 检查Node.js是否可用
$nodeVersion = & node --version 2>$null
if (-not $nodeVersion) {
    Write-Host "错误：Node.js未安装或未添加到环境变量" -ForegroundColor Red
    exit 1
}

Write-Host "Node.js版本：$nodeVersion" -ForegroundColor Green

# 检查npm是否可用
$npmVersion = & npm --version 2>$null
if (-not $npmVersion) {
    Write-Host "错误：npm未安装或未添加到环境变量" -ForegroundColor Red
    exit 1
}

Write-Host "npm版本：$npmVersion" -ForegroundColor Green

# 安装依赖
Write-Host "\n1. 安装依赖..." -ForegroundColor Yellow
& npm install

# 启动前端服务
Write-Host "\n2. 启动前端服务..." -ForegroundColor Yellow
& npm run dev
