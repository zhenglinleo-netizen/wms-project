# 采购平台系统开发说明

## 项目概述

这是一个从仓库管理系统改造的**智能辅料采购平台**系统，采用 Spring Boot + MyBatis + MySQL + Vue3 + Element Plus 技术栈。

## 已完成功能

### ✅ 1. 数据库设计
- 完整的数据库表结构已创建（`schema_purchase_platform.sql`）
- 包含：用户、辅料、项目、方案、需求单、订单、供应商、库存等核心表

### ✅ 2. JWT认证系统
- JWT工具类（`JwtUtil`）
- JWT拦截器（`JwtInterceptor`）
- 支持登录、注册、修改密码
- 权限控制（管理员权限注解）

### ✅ 3. 用户管理模块
- 用户登录（JWT认证）
- 用户注册（待审核状态）
- 密码修改
- 获取当前用户信息
- 用户列表管理（管理员）

## 待完成功能

### 📋 核心功能模块

#### 1. 仪表盘（Dashboard）
- [ ] 今日待办/待审任务统计
- [ ] 近期需求单状态一览
- [ ] 库存预警提示
- [ ] 热门辅料推荐卡片
- [ ] 系统使用数据概览（图表）

#### 2. 智能辅料库
- [ ] 辅料浏览与搜索（分类、筛选）
- [ ] 辅料详情页面
- [ ] AI图片识别接口（保留接口，基础实现）
- [ ] 相似辅料推荐（向量检索接口）
- [ ] 收藏功能

#### 3. 项目与方案管理
- [ ] 项目管理（CRUD）
- [ ] 方案管理（CRUD）
- [ ] 方案明细管理
- [ ] 方案导出（PDF/Excel）

#### 4. 采购需求管理
- [ ] 需求单创建（从方案导入或手动创建）
- [ ] 需求单列表与筛选
- [ ] 需求单审核流程
- [ ] 需求单状态跟踪

#### 5. 采购管理
- [ ] 需求审核（管理员）
- [ ] 订单管理（从需求单生成）
- [ ] 供应商管理（CRUD）
- [ ] 订单状态跟踪

#### 6. 库存管理
- [ ] 库存监控与预警
- [ ] 入库管理（关联订单）
- [ ] 出库管理（关联需求单）
- [ ] 库存调拨

#### 7. 数据分析与报表
- [ ] 数据可视化（库存趋势、热门辅料等）
- [ ] 报表导出功能

#### 8. 系统管理
- [ ] 基础数据管理（分类、材质、颜色等）
- [ ] 系统配置（AI服务配置等）

## 技术架构

### 后端
- Spring Boot 3.2.0
- MyBatis 3.0.3
- MySQL 8.0+
- JWT认证
- Java 17

### 前端
- Vue 3
- Element Plus
- Vue Router
- Pinia
- Axios
- Vite

## 数据库结构

核心表：
- `sys_user` - 用户表
- `material` - 辅料表
- `material_category` - 辅料分类表
- `supplier` - 供应商表
- `project` - 项目表
- `scheme` - 方案表
- `requirement` - 采购需求单表
- `purchase_order` - 采购订单表
- `inventory` - 库存表
- `stock_in` / `stock_out` - 入库/出库表

## 下一步开发计划

1. **创建核心实体类**
   - Material（辅料）
   - Project（项目）
   - Scheme（方案）
   - Requirement（需求单）
   - PurchaseOrder（订单）
   - Supplier（供应商）

2. **实现Mapper和Service层**
   - 各模块的CRUD操作
   - 业务逻辑处理

3. **实现Controller层**
   - RESTful API接口
   - 权限控制

4. **重构前端**
   - 更新路由配置
   - 创建新页面组件
   - 实现数据交互

5. **AI功能接口**
   - 图片识别接口（保留，基础实现）
   - 向量检索接口（保留，基础实现）

## 开发注意事项

1. **JWT认证**：所有需要认证的接口都需要在请求头中携带 `Authorization: Bearer <token>`
2. **权限控制**：使用 `@RequireAdmin` 注解标记需要管理员权限的接口
3. **数据验证**：使用 `@Valid` 注解进行参数验证
4. **统一响应**：使用 `Result<T>` 统一响应格式

## API接口规范

### 认证相关
- `POST /api/user/login` - 用户登录（返回JWT token）
- `POST /api/user/register` - 用户注册
- `POST /api/user/change-password` - 修改密码
- `GET /api/user/current` - 获取当前用户信息

### 用户管理（管理员）
- `GET /api/user/list` - 用户列表
- `POST /api/user` - 添加用户
- `PUT /api/user` - 更新用户
- `DELETE /api/user/{id}` - 删除用户

## 部署说明

1. 执行数据库脚本 `schema_purchase_platform.sql`
2. 配置 `application.yml` 中的数据库连接
3. 启动后端服务
4. 启动前端服务

默认管理员账号：admin / admin123




