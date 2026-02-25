# WMS系统部署指南

## 1. 系统概述

WMS（Warehouse Management System）是一个仓库管理系统，用于管理辅料库存、项目方案、需求单等业务流程。系统使用Spring Boot作为后端框架，Vue 3作为前端框架，MySQL作为业务数据库，Milvus作为向量数据库，MinIO作为对象存储服务，Redis作为缓存服务，RabbitMQ作为消息队列。

## 2. 环境要求

### 2.1 硬件要求
- CPU: 至少4核
- 内存: 至少8GB
- 磁盘: 至少50GB可用空间
- 网络: 稳定的网络连接

### 2.2 软件要求
- Windows 10/11 64位操作系统
- PowerShell 5.0或更高版本
- 管理员权限

## 3. 部署步骤

### 3.1 步骤一：环境搭建

1. **打开PowerShell**：以管理员身份运行PowerShell
2. **导航到部署脚本目录**：
   ```powershell
   cd d:\Graduate_design\application\wms-project-main\deployment\scripts\environment
   ```
3. **运行环境搭建脚本**：
   ```powershell
   .\setup-environment.ps1
   ```
4. **等待脚本执行完成**：脚本会自动下载并安装Java JDK、Node.js和Docker
5. **重启计算机**：确保环境变量生效

### 3.2 步骤二：数据库配置

1. **启动Docker Desktop**：确保Docker服务运行
2. **打开PowerShell**：以管理员身份运行PowerShell
3. **导航到数据库配置脚本目录**：
   ```powershell
   cd d:\Graduate_design\application\wms-project-main\deployment\scripts\database
   ```
4. **运行数据库配置脚本**：
   ```powershell
   .\setup-database.ps1
   ```
5. **等待脚本执行完成**：脚本会自动启动和配置MySQL、Milvus、MinIO、Redis和RabbitMQ服务

### 3.3 步骤三：启动后端服务

1. **打开PowerShell**：以管理员身份运行PowerShell
2. **导航到后端服务启动脚本目录**：
   ```powershell
   cd d:\Graduate_design\application\wms-project-main\deployment\scripts\service
   ```
3. **运行后端服务启动脚本**：
   ```powershell
   .\start-backend.ps1
   ```
4. **等待服务启动**：后端服务会在端口9090上运行

### 3.4 步骤四：启动前端服务

1. **打开新的PowerShell**：以管理员身份运行PowerShell
2. **导航到前端服务启动脚本目录**：
   ```powershell
   cd d:\Graduate_design\application\wms-project-main\deployment\scripts\service
   ```
3. **运行前端服务启动脚本**：
   ```powershell
   .\start-frontend.ps1
   ```
4. **等待服务启动**：前端服务会在端口8080上运行

## 4. 系统验证

### 4.1 验证服务状态

1. **后端服务**：访问 http://localhost:9090，应该看到后端服务的响应
2. **前端服务**：访问 http://localhost:8080，应该看到系统登录页面
3. **数据库服务**：
   - MySQL: http://localhost:3306
   - Milvus: http://localhost:19530
   - MinIO: http://localhost:9001
   - Redis: http://localhost:6379
   - RabbitMQ: http://localhost:15672

### 4.2 验证功能

1. **登录系统**：使用默认账号登录（如果有）
2. **访问热门辅料推荐**：验证热门辅料推荐功能是否能够正确显示图片
3. **测试其他功能**：验证系统的其他功能是否正常

## 5. 常见问题和解决方法

### 5.1 Docker相关问题

**问题**：Docker Desktop启动失败
**解决方法**：
- 检查系统是否启用了Hyper-V
- 检查系统是否安装了WSL 2
- 重启计算机后重新尝试

**问题**：Docker容器启动失败
**解决方法**：
- 检查端口是否被占用
- 检查Docker服务是否运行
- 查看容器日志：`docker logs <容器名称>`

### 5.2 数据库相关问题

**问题**：MySQL连接失败
**解决方法**：
- 检查MySQL容器是否运行：`docker ps | grep wms-mysql`
- 检查MySQL端口是否开放：`netstat -ano | findstr :3306`
- 检查数据库配置是否正确

**问题**：Milvus服务启动失败
**解决方法**：
- 检查Milvus容器是否运行：`docker ps | grep wms-milvus`
- 检查Milvus端口是否开放：`netstat -ano | findstr :19530`
- 查看Milvus日志：`docker logs wms-milvus`

### 5.3 服务启动相关问题

**问题**：后端服务启动失败
**解决方法**：
- 检查数据库服务是否运行
- 检查端口是否被占用
- 查看后端服务日志

**问题**：前端服务启动失败
**解决方法**：
- 检查Node.js是否安装正确
- 检查依赖是否安装成功
- 查看前端服务日志

## 6. 系统维护

### 6.1 服务管理

**启动所有服务**：
```powershell
# 启动数据库服务
cd d:\Graduate_design\application\wms-project-main\deployment\scripts\database
.\setup-database.ps1

# 启动后端服务
cd d:\Graduate_design\application\wms-project-main\deployment\scripts\service
.\start-backend.ps1

# 启动前端服务
cd d:\Graduate_design\application\wms-project-main\deployment\scripts\service
.\start-frontend.ps1
```

**停止所有服务**：
```powershell
# 停止数据库服务
docker stop wms-mysql wms-milvus wms-minio wms-redis wms-rabbitmq

# 停止后端服务：按Ctrl+C

# 停止前端服务：按Ctrl+C
```

### 6.2 数据备份

**MySQL数据备份**：
```powershell
docker exec wms-mysql mysqldump -u root -p123456 wms_db > wms_db_backup.sql
```

**MinIO数据备份**：
- 定期备份MinIO存储的图片和文件

### 6.3 日志管理

**查看后端服务日志**：
- 后端服务启动控制台

**查看前端服务日志**：
- 前端服务启动控制台

**查看数据库服务日志**：
```powershell
docker logs wms-mysql
```

## 7. 配置文件说明

### 7.1 后端配置文件

`wms-project/src/main/resources/application.yml`：
- 数据库连接配置
- MinIO配置
- Milvus配置
- Redis配置
- RabbitMQ配置
- API密钥配置

### 7.2 前端配置文件

`wms-frontend/src/utils/request.js`：
- API请求基础配置

## 8. 技术支持

如果在部署过程中遇到问题，请参考以下资源：

- **Docker官方文档**：https://docs.docker.com/
- **Spring Boot官方文档**：https://spring.io/projects/spring-boot
- **Vue官方文档**：https://vuejs.org/
- **Milvus官方文档**：https://milvus.io/docs/
- **MinIO官方文档**：https://min.io/docs/

## 9. 版本信息

- **系统版本**：v2.0.0
- **后端框架**：Spring Boot 2.7.18
- **前端框架**：Vue 3 + Element Plus
- **数据库**：MySQL 8.0
- **向量数据库**：Milvus 2.3.4
- **对象存储**：MinIO
- **缓存服务**：Redis 6.0
- **消息队列**：RabbitMQ 3.8

---

**部署完成后，系统应该能够正常运行，热门辅料推荐功能能够正确显示图片。**
