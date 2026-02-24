# 辅料采购管理平台

基于 Spring Boot + MyBatis + MySQL + Vue3 + Element Plus + MinIO + Milvus 构建的现代化辅料采购管理平台，提供完整的采购管理、供应商管理、库存管理、AI智能辅料识别和数据分析功能。

## 项目结构

```
wms-project/
├── wms-project/           # 后端项目（Spring Boot）
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/     # Java 源代码
│   │   │   └── resources/ # 配置文件和SQL脚本
│   │   └── test/         # 测试代码
│   └── pom.xml           # Maven 配置
└── wms-frontend/          # 前端项目（Vue3）
    ├── src/
    │   ├── api/          # API 接口封装
    │   ├── views/        # 页面组件
    │   ├── router/       # 路由配置
    │   ├── stores/       # 状态管理
    │   └── utils/        # 工具函数
    └── package.json      # NPM 配置
```

## 功能模块

### 1. 系统管理
- **用户管理**：用户登录、注册、信息管理
- **权限管理**：基于角色的权限控制

### 2. 基础数据管理
- **辅料管理**：辅料信息的增删改查
- **供应商管理**：供应商信息的增删改查
- **供应商-辅料关系管理**：管理供应商与辅料的关联关系
- **分类管理**：辅料分类的增删改查

### 3. 项目管理
- **项目管理**：项目信息的增删改查
- **方案管理**：项目方案的创建和管理
- **方案明细管理**：方案中辅料的明细管理

### 4. 采购管理
- **需求管理**：采购需求的创建、审核和管理
- **需求明细管理**：采购需求中辅料的明细管理
- **采购订单管理**：采购订单的创建和管理
- **采购订单项管理**：采购订单中辅料的明细管理
- **采购催单管理**：采购催单的创建和管理

### 5. 库存管理
- **入库管理**：商品入库操作和记录查询
- **出库管理**：商品出库操作和记录查询
- **库存查询**：实时库存信息查询
- **库存预警**：库存不足时的预警功能
- **库存监控**：实时监控库存数量变化

### 6. AI 智能功能
- **AI 智能辅料识别**：基于智谱大模型的辅料自动识别
- **人工校正**：对AI识别结果进行人工校正
- **图片搜索**：基于图片的辅料搜索
- **识别历史管理**：管理AI识别的历史记录
- **相似辅料推荐**：基于向量相似度的辅料推荐

### 7. 智能辅料库
- **辅料展示**：以网格或列表形式展示辅料
- **辅料搜索**：支持关键词搜索和筛选
- **辅料详情**：查看辅料详细信息
- **辅料收藏**：收藏常用辅料
- **辅料推荐**：基于AI的辅料推荐

### 8. 文件管理
- **图片存储**：使用MinIO对象存储服务存储辅料图片
- **图片预览**：支持辅料图片预览
- **图片上传**：支持批量上传辅料图片

### 9. 数据分析与报表
- **采购分析**：采购趋势、类别分布、数量分布分析
- **库存分析**：库存水平趋势、价值分布、周转率分析
- **供应商分析**：供应商绩效排名、分布、合作趋势分析
- **报表导出**：支持将分析结果导出为Excel文件

## 技术栈

### 后端
- **Spring Boot 2.7.18**：现代化Java企业级应用框架
- **MyBatis 2.3.1**：Java持久层框架
- **MySQL 8.0+**：关系型数据库
- **MinIO 8.5.2**：对象存储服务，用于存储辅料图片
- **Milvus 2.6.0**：向量数据库，用于相似辅料推荐
- **Redis**：缓存服务，提高系统性能
- **RabbitMQ**：消息队列，用于异步处理
- **JWT**：JSON Web Token，用于用户认证

### 前端
- **Vue 3**：现代化前端框架
- **Element Plus**：UI组件库
- **Vue Router**：前端路由
- **Pinia**：状态管理
- **Axios**：HTTP客户端，用于API调用
- **ECharts**：数据可视化图表库
- **Vue Lazyload**：图片懒加载
- **XLSX**：Excel文件处理库
- **Vite**：前端构建工具

## 快速开始

### 1. 环境要求
- **JDK 8+**
- **MySQL 8.0+**
- **Node.js 16.0+**
- **npm 7.0+**
- **Maven 3.6+**（或使用Maven Wrapper）
- **MinIO**：用于存储辅料图片
- **Milvus**：用于相似辅料推荐
- **Redis**：用于缓存
- **RabbitMQ**：用于消息队列

### 2. 数据库配置
1. **创建数据库**：
   ```sql
   CREATE DATABASE wms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
   ```

2. **执行SQL脚本**：
   - 导入 `wms-project/src/main/resources/db/schema_purchase_platform.sql`

### 3. MinIO 配置
1. **下载并安装MinIO**：
   - 从 [MinIO官网](https://min.io/download) 下载适合您操作系统的MinIO版本

2. **启动MinIO服务**：
   ```bash
   # Windows
   .\minio.exe server D:\MinIO\data --console-address ":9001"
   
   # Linux/Mac
   minio server ~/MinIO/data --console-address ":9001"
   ```

3. **创建MinIO bucket**：
   - 访问 MinIO 控制台：http://localhost:9001
   - 登录（默认账号：minioadmin，默认密码：minioadmin）
   - 创建名为 `wms-bucket` 的 bucket

### 4. Milvus 配置
1. **下载并安装Milvus**：
   - 从 [Milvus官网](https://milvus.io/docs/install_standalone-docker.md) 下载并安装Milvus

2. **启动Milvus服务**：
   - 按照Milvus文档启动服务
   - 默认服务地址：http://localhost:19530

### 5. 配置修改

#### 后端配置（wms-project/src/main/resources/application.yml）
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wms_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&autoReconnect=true
    username: root
    password: 123456  # 修改为你的数据库密码

# MinIO 配置
minio:
  url: http://localhost:9000
  accessKey: minioadmin
  secretKey: minioadmin
  bucketName: wms-bucket

# Milvus 配置
milvus:
  host: localhost
  port: 19530
  collectionName: product_vectors

# Redis 配置
spring:
  redis:
    host: localhost
    port: 6379
    password:  # 如果有密码，填写密码

# RabbitMQ 配置
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

#### 前端配置（wms-frontend/vite.config.js）
```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8081',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    },
    '/minio': {
      target: 'http://localhost:9000',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/minio/, '')
    }
  }
}
```

### 6. 启动后端
```bash
cd wms-project

# 使用 Maven Wrapper（推荐，无需安装Maven）
./mvnw.cmd spring-boot:run

# 或使用已安装的Maven
mvn spring-boot:run
```

后端服务将运行在 http://localhost:8081

### 7. 启动前端
```bash
cd wms-frontend
npm install
npm run dev
```

前端服务将运行在 http://localhost:3000

### 8. 默认账号
- **用户名**：admin
- **密码**：admin123

## API 接口

### 用户相关
- **POST /user/login** - 用户登录
- **POST /user/register** - 用户注册
- **GET /user/list** - 用户列表
- **POST /user** - 新增用户
- **PUT /user** - 更新用户
- **DELETE /user/{id}** - 删除用户
- **GET /user/current** - 获取当前用户

### 辅料相关
- **GET /product/list** - 辅料列表
- **POST /product/search** - 搜索辅料
- **POST /product** - 新增辅料
- **PUT /product** - 更新辅料
- **DELETE /product/{id}** - 删除辅料

### 供应商相关
- **GET /supplier/list** - 供应商列表
- **GET /supplier/{id}** - 供应商详情
- **POST /supplier** - 新增供应商
- **PUT /supplier** - 更新供应商
- **DELETE /supplier/{id}** - 删除供应商
- **GET /supplier-product/list** - 供应商-辅料关系列表
- **POST /supplier-product** - 新增供应商-辅料关系
- **PUT /supplier-product** - 更新供应商-辅料关系
- **DELETE /supplier-product/{id}** - 删除供应商-辅料关系

### 项目相关
- **GET /project/list** - 项目列表
- **GET /project/{id}** - 项目详情
- **POST /project** - 新增项目
- **PUT /project** - 更新项目
- **DELETE /project/{id}** - 删除项目
- **GET /scheme/list/{projectId}** - 方案列表
- **POST /scheme** - 新增方案
- **PUT /scheme** - 更新方案
- **DELETE /scheme/{id}** - 删除方案
- **GET /scheme-item/list/{schemeId}** - 方案明细列表
- **POST /scheme-item** - 新增方案明细
- **PUT /scheme-item** - 更新方案明细
- **DELETE /scheme-item/{id}** - 删除方案明细

### 采购需求相关
- **GET /requirement/list** - 需求列表
- **GET /requirement/{id}** - 需求详情
- **POST /requirement** - 新增需求
- **PUT /requirement** - 更新需求
- **DELETE /requirement/{id}** - 删除需求

### 文件相关
- **POST /file/upload** - 上传文件
- **POST /file/upload-multiple** - 批量上传文件
- **GET /file/get-image** - 获取图片
- **POST /file/check-exists** - 检查文件是否存在
- **DELETE /file/delete** - 删除文件

### AI 智能识别相关
- **POST /ai/recognize** - 辅料智能识别
- **GET /ai/history** - 识别历史记录
- **DELETE /ai/history/{id}** - 删除识别历史

### 推荐相关
- **GET /recommend/{productId}** - 基于辅料ID推荐相似辅料
- **POST /recommend-by-image** - 基于图片推荐相似辅料
- **POST /recommend-by-text** - 基于文本描述推荐相似辅料
- **POST /recommend-by-multimodal** - 基于多模态（文本+图片）推荐相似辅料

## 核心功能

### 1. AI 智能辅料识别
- 上传辅料图片，AI自动识别辅料类型、材质、颜色等信息
- 支持人工校正识别结果，确保信息准确性
- 识别结果可直接添加到辅料库

### 2. 智能辅料库
- 以网格或列表形式展示辅料
- 支持关键词搜索和多维度筛选
- 点击辅料卡片查看详细信息
- 支持辅料收藏和推荐

### 3. 相似辅料推荐
- 基于向量相似度的辅料推荐
- 支持多模态（文本+图片）推荐
- 显示相似度评分，帮助用户快速判断

### 4. 库存管理
- 实时监控库存数量变化
- 支持库存增加和减少操作
- 显示辅料缩略图，直观查看辅料信息

### 5. 数据分析与报表
- 采购分析：趋势、类别分布、数量分布
- 库存分析：水平趋势、价值分布、周转率
- 供应商分析：绩效排名、分布、合作趋势
- 支持将分析结果导出为Excel文件

### 6. 文件管理
- 使用MinIO对象存储服务存储辅料图片
- 支持批量上传和预览辅料图片
- 后端提供图片获取接口，避免MinIO认证问题

## 技术亮点

1. **现代化技术栈**：使用Spring Boot 2.7.18和Vue 3构建，采用最新的技术标准
2. **AI 智能识别**：集成智谱大模型，实现辅料自动识别
3. **向量数据库**：使用Milvus实现相似辅料推荐
4. **对象存储**：使用MinIO存储辅料图片，提高系统扩展性
5. **响应式设计**：基于Element Plus的响应式UI，适配各种设备
6. **权限管理**：基于角色的权限控制，确保系统安全
7. **实时数据**：使用Vue 3的响应式特性，实现数据实时更新
8. **数据可视化**：使用ECharts实现数据分析与报表功能
9. **缓存优化**：使用Redis提高系统性能
10. **消息队列**：使用RabbitMQ实现异步处理

## 注意事项

1. **MinIO配置**：确保MinIO服务已启动，并且创建了名为`wms-bucket`的bucket
2. **Milvus配置**：确保Milvus服务已启动，默认服务地址为http://localhost:19530
3. **数据库配置**：确保MySQL服务已启动，并且创建了名为`wms_db`的数据库
4. **API调用**：前端通过代理访问后端API，确保代理配置正确
5. **图片处理**：系统使用后端接口获取图片，避免MinIO认证问题
6. **AI识别**：AI识别功能需要配置智谱大模型API密钥
7. **相似推荐**：相似辅料推荐功能依赖Milvus向量数据库

## 更新日志

### v2.0.0 (2026-02-25)
- **新增功能**：
  - 数据分析与报表功能，使用ECharts实现数据可视化
  - 相似辅料推荐功能，基于Milvus向量数据库
  - 多模态推荐支持，可基于文本、图片或两者结合进行推荐
  - 报表导出功能，支持将分析结果导出为Excel文件

- **技术升级**：
  - 集成Milvus 2.6.0向量数据库
  - 前端集成ECharts 6.0.0数据可视化库
  - 前端集成XLSX库用于Excel导出
  - 优化图片懒加载实现

- **Bug修复**：
  - 修复相似辅料推荐中图片显示问题
  - 优化图片处理逻辑，确保图片正确加载
  - 修复懒加载机制，提高页面性能

### v1.0.0 (2026-01-17)
- **初始版本发布**
- **实现完整的辅料采购管理平台功能**
- **集成AI智能辅料识别功能**
- **支持完整的采购流程管理**

## 许可证

MIT License