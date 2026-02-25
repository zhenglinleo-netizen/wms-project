# WMS系统环境搭建脚本
# 功能：安装和配置Java JDK、Node.js和Docker

Write-Host "==============================================="
Write-Host "WMS系统环境搭建脚本"
Write-Host "==============================================="

# 检查PowerShell版本
if ($PSVersionTable.PSVersion.Major -lt 5) {
    Write-Host "错误：需要PowerShell 5.0或更高版本" -ForegroundColor Red
    exit 1
}

# 检查管理员权限
if (-not ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")) {
    Write-Host "错误：需要以管理员身份运行此脚本" -ForegroundColor Red
    exit 1
}

# 定义安装目录
$installDir = "C:\WMS\tools"
if (-not (Test-Path $installDir)) {
    New-Item -ItemType Directory -Path $installDir -Force | Out-Null
}

Write-Host "安装目录: $installDir" -ForegroundColor Green

# 下载和安装Java JDK
Write-Host "\n1. 安装Java JDK 1.8..." -ForegroundColor Yellow

$jdkUrl = "https://download.oracle.com/java/18/latest/jdk-18_windows-x64_bin.exe"
$jdkInstaller = "$installDir\jdk-18_windows-x64_bin.exe"

if (-not (Test-Path $jdkInstaller)) {
    Write-Host "正在下载Java JDK..." -ForegroundColor Cyan
    Invoke-WebRequest -Uri $jdkUrl -OutFile $jdkInstaller
}

Write-Host "正在安装Java JDK..." -ForegroundColor Cyan
Start-Process -FilePath $jdkInstaller -ArgumentList "/s" -Wait

# 配置Java环境变量
$javaHome = "C:\Program Files\Java\jdk-18"
if (Test-Path $javaHome) {
    [Environment]::SetEnvironmentVariable("JAVA_HOME", $javaHome, "Machine")
    $path = [Environment]::GetEnvironmentVariable("PATH", "Machine")
    if (-not $path.Contains("%JAVA_HOME%\bin")) {
        $newPath = "%JAVA_HOME%\bin;" + $path
        [Environment]::SetEnvironmentVariable("PATH", $newPath, "Machine")
    }
    Write-Host "Java JDK安装成功" -ForegroundColor Green
} else {
    Write-Host "Java JDK安装失败：未找到安装目录" -ForegroundColor Red
}

# 下载和安装Node.js
Write-Host "\n2. 安装Node.js 16..." -ForegroundColor Yellow

$nodeUrl = "https://nodejs.org/dist/v16.20.0/node-v16.20.0-x64.msi"
$nodeInstaller = "$installDir\node-v16.20.0-x64.msi"

if (-not (Test-Path $nodeInstaller)) {
    Write-Host "正在下载Node.js..." -ForegroundColor Cyan
    Invoke-WebRequest -Uri $nodeUrl -OutFile $nodeInstaller
}

Write-Host "正在安装Node.js..." -ForegroundColor Cyan
Start-Process -FilePath "msiexec.exe" -ArgumentList "/i $nodeInstaller /qn" -Wait

# 验证Node.js安装
$nodeVersion = & node --version 2>$null
if ($nodeVersion) {
    Write-Host "Node.js安装成功，版本：$nodeVersion" -ForegroundColor Green
} else {
    Write-Host "Node.js安装失败" -ForegroundColor Red
}

# 下载和安装Docker Desktop
Write-Host "\n3. 安装Docker Desktop..." -ForegroundColor Yellow

$dockerUrl = "https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe"
$dockerInstaller = "$installDir\Docker Desktop Installer.exe"

if (-not (Test-Path $dockerInstaller)) {
    Write-Host "正在下载Docker Desktop..." -ForegroundColor Cyan
    Invoke-WebRequest -Uri $dockerUrl -OutFile $dockerInstaller
}

Write-Host "正在安装Docker Desktop..." -ForegroundColor Cyan
Start-Process -FilePath $dockerInstaller -ArgumentList "install" -Wait

# 启动Docker服务
Write-Host "正在启动Docker服务..." -ForegroundColor Cyan
Start-Service -Name "com.docker.service" -ErrorAction SilentlyContinue

# 验证Docker安装
$dockerVersion = & docker --version 2>$null
if ($dockerVersion) {
    Write-Host "Docker安装成功，版本：$dockerVersion" -ForegroundColor Green
} else {
    Write-Host "Docker安装失败" -ForegroundColor Red
}

Write-Host "\n==============================================="
Write-Host "环境搭建完成！" -ForegroundColor Green
Write-Host "请重启计算机以确保环境变量生效。" -ForegroundColor Yellow
Write-Host "==============================================="
