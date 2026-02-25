# WMS系统数据库配置脚本
# 功能：启动和配置MySQL、Milvus、MinIO、Redis和RabbitMQ服务

Write-Host "==============================================="
Write-Host "WMS系统数据库配置脚本"
Write-Host "==============================================="

# 检查Docker是否运行
$dockerStatus = & docker info 2>$null
if (-not $dockerStatus) {
    Write-Host "错误：Docker未运行，请先启动Docker Desktop" -ForegroundColor Red
    exit 1
}

# 定义网络名称
$networkName = "wms-network"

# 创建Docker网络
Write-Host "\n1. 创建Docker网络..." -ForegroundColor Yellow
& docker network create $networkName 2>$null
Write-Host "Docker网络创建成功：$networkName" -ForegroundColor Green

# 启动MySQL服务
Write-Host "\n2. 启动MySQL服务..." -ForegroundColor Yellow

$mysqlContainer = "wms-mysql"
$mysqlPort = 3306
$mysqlRootPassword = "123456"
$mysqlDatabase = "wms_db"

# 停止并删除旧容器
& docker stop $mysqlContainer 2>$null
& docker rm $mysqlContainer 2>$null

# 启动MySQL容器
& docker run -d --name $mysqlContainer --network $networkName `
    -p $mysqlPort:3306 `
    -e MYSQL_ROOT_PASSWORD=$mysqlRootPassword `
    -e MYSQL_DATABASE=$mysqlDatabase `
    mysql:8.0

Write-Host "MySQL服务启动中..." -ForegroundColor Cyan
Start-Sleep -Seconds 10

# 验证MySQL服务
$mysqlStatus = & docker ps -a | Select-String $mysqlContainer
if ($mysqlStatus) {
    Write-Host "MySQL服务启动成功" -ForegroundColor Green
} else {
    Write-Host "MySQL服务启动失败" -ForegroundColor Red
}

# 启动Milvus服务
Write-Host "\n3. 启动Milvus服务..." -ForegroundColor Yellow

$milvusContainer = "wms-milvus"
$milvusPort = 19530
$milvusWebPort = 19121

# 停止并删除旧容器
& docker stop $milvusContainer 2>$null
& docker rm $milvusContainer 2>$null

# 启动Milvus容器
& docker run -d --name $milvusContainer --network $networkName `
    -p $milvusPort:19530 `
    -p $milvusWebPort:19121 `
    milvusdb/milvus:v2.3.4

Write-Host "Milvus服务启动中..." -ForegroundColor Cyan
Start-Sleep -Seconds 20

# 验证Milvus服务
$milvusStatus = & docker ps -a | Select-String $milvusContainer
if ($milvusStatus) {
    Write-Host "Milvus服务启动成功" -ForegroundColor Green
} else {
    Write-Host "Milvus服务启动失败" -ForegroundColor Red
}

# 启动MinIO服务
Write-Host "\n4. 启动MinIO服务..." -ForegroundColor Yellow

$minioContainer = "wms-minio"
$minioPort = 9004
$minioWebPort = 9001
$minioAccessKey = "minioadmin"
$minioSecretKey = "minioadmin"
$minioBucket = "wms-bucket"

# 停止并删除旧容器
& docker stop $minioContainer 2>$null
& docker rm $minioContainer 2>$null

# 启动MinIO容器
& docker run -d --name $minioContainer --network $networkName `
    -p $minioPort:9000 `
    -p $minioWebPort:9001 `
    -e MINIO_ROOT_USER=$minioAccessKey `
    -e MINIO_ROOT_PASSWORD=$minioSecretKey `
    minio/minio server /data --console-address ":9001"

Write-Host "MinIO服务启动中..." -ForegroundColor Cyan
Start-Sleep -Seconds 10

# 验证MinIO服务
$minioStatus = & docker ps -a | Select-String $minioContainer
if ($minioStatus) {
    Write-Host "MinIO服务启动成功" -ForegroundColor Green
} else {
    Write-Host "MinIO服务启动失败" -ForegroundColor Red
}

# 创建MinIO桶
Write-Host "正在创建MinIO桶：$minioBucket..." -ForegroundColor Cyan
& docker exec $minioContainer mc mb local/$minioBucket 2>$null
Write-Host "MinIO桶创建成功" -ForegroundColor Green

# 启动Redis服务
Write-Host "\n5. 启动Redis服务..." -ForegroundColor Yellow

$redisContainer = "wms-redis"
$redisPort = 6379

# 停止并删除旧容器
& docker stop $redisContainer 2>$null
& docker rm $redisContainer 2>$null

# 启动Redis容器
& docker run -d --name $redisContainer --network $networkName `
    -p $redisPort:6379 `
    redis:6.0

Write-Host "Redis服务启动中..." -ForegroundColor Cyan
Start-Sleep -Seconds 5

# 验证Redis服务
$redisStatus = & docker ps -a | Select-String $redisContainer
if ($redisStatus) {
    Write-Host "Redis服务启动成功" -ForegroundColor Green
} else {
    Write-Host "Redis服务启动失败" -ForegroundColor Red
}

# 启动RabbitMQ服务
Write-Host "\n6. 启动RabbitMQ服务..." -ForegroundColor Yellow

$rabbitmqContainer = "wms-rabbitmq"
$rabbitmqPort = 5672
$rabbitmqWebPort = 15672
$rabbitmqUser = "guest"
$rabbitmqPassword = "guest"

# 停止并删除旧容器
& docker stop $rabbitmqContainer 2>$null
& docker rm $rabbitmqContainer 2>$null

# 启动RabbitMQ容器
& docker run -d --name $rabbitmqContainer --network $networkName `
    -p $rabbitmqPort:5672 `
    -p $rabbitmqWebPort:15672 `
    -e RABBITMQ_DEFAULT_USER=$rabbitmqUser `
    -e RABBITMQ_DEFAULT_PASS=$rabbitmqPassword `
    rabbitmq:3.8-management

Write-Host "RabbitMQ服务启动中..." -ForegroundColor Cyan
Start-Sleep -Seconds 15

# 验证RabbitMQ服务
$rabbitmqStatus = & docker ps -a | Select-String $rabbitmqContainer
if ($rabbitmqStatus) {
    Write-Host "RabbitMQ服务启动成功" -ForegroundColor Green
} else {
    Write-Host "RabbitMQ服务启动失败" -ForegroundColor Red
}

Write-Host "\n==============================================="
Write-Host "数据库配置完成！" -ForegroundColor Green
Write-Host "服务运行状态："
Write-Host "- MySQL: http://localhost:$mysqlPort"
Write-Host "- Milvus: http://localhost:$milvusPort"
Write-Host "- MinIO: http://localhost:$minioWebPort"
Write-Host "- Redis: http://localhost:$redisPort"
Write-Host "- RabbitMQ: http://localhost:$rabbitmqWebPort"
Write-Host "==============================================="
