# 辅料采购平台

基于 Spring Boot + MyBatis + MySQL + Vue3 + Element Plus 构建的现代化辅料采购管理平台，提供完整的采购管理、供应商管理和库存管理功能。

## 项目结构

```
material-purchase-platform/
├── material-purchase-backend/   # 后端项目（Spring Boot）
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/     # Java 源代码
│   │   │   └── resources/ # 配置文件和SQL脚本
│   │   └── test/         # 测试代码
│   └── pom.xml           # Maven 配置
└── material-purchase-frontend/  # 前端项目（Vue3）
    ├── src/
    │   ├── api/          # API 接口封装
    │   ├── views/        # 页面组件
    │   ├── router/       # 路由配置
    │   └── stores/       # 状态管理
    └── package.json      # NPM 配置
```

## 功能模块

### 1. 系统管理
- 用户管理：用户登录、注册、信息管理
- 权限管理：基于角色的权限控制

### 2. 基础数据管理
- 辅料管理：辅料信息的增删改查
- 供应商管理：供应商信息的增删改查
- 供应商-辅料关系管理：管理供应商与辅料的关联关系
- 分类管理：辅料分类的增删改查

### 3. 项目管理
- 项目管理：项目信息的增删改查
- 方案管理：项目方案的创建和管理
- 方案明细管理：方案中辅料的明细管理

### 4. 采购管理
- 需求管理：采购需求的创建、审核和管理
- 需求明细管理：采购需求中辅料的明细管理
- 采购订单管理：采购订单的创建和管理
- 采购订单项管理：采购订单中辅料的明细管理
- 采购催单管理：采购催单的创建和管理

### 5. 库存管理
- 入库管理：商品入库操作和记录查询
- 出库管理：商品出库操作和记录查询
- 库存查询：实时库存信息查询
- 库存预警：库存不足时的预警功能

### 6. AI 智能功能
- AI 智能辅料识别：基于智谱大模型的辅料自动识别
- 识别历史管理：管理AI识别的历史记录

## 技术栈

### 后端
- Spring Boot 4.0.1
- MyBatis 3.0.3
- MySQL 8.0+
- Java 17
- JWT 认证
- Maven

### 前端
- Vue 3
- Element Plus
- Vue Router
- Pinia
- Axios
- Vite
-智谱大模型 API（AI 识别功能）

## 快速开始

### 1. 环境要求
- JDK 17+
- MySQL 8.0+
- Node.js 16.0+
- npm 7.0+
- Maven 3.6+

### 2. 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE wms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

2. 执行 SQL 脚本：
```bash
# 导入 src/main/resources/db/schema_purchase_platform.sql
mysql -u root -p wms_db < src/main/resources/db/schema_purchase_platform.sql
```

3. 修复表注释（如果出现乱码）：
```bash
# 执行修复脚本
mysql -u root -p wms_db < fix_supplier_product_comments.sql
```

### 3. 配置修改

1. 后端配置（`material-purchase-backend/src/main/resources/application.yml`）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wms_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&autoReconnect=true
    username: root
    password: 123456  # 修改为你的数据库密码
```

2. 前端 API 配置（`material-purchase-frontend/src/config/api.js`）：
```javascript
const config = {
  // 智谱大模型API配置
  zhipu: {
    apiKey: 'your_api_key',  // 修改为你的智谱大模型API密钥
    apiUrl: 'https://open.bigmodel.cn/api/paas/v4/chat/completions',
    model: 'glm-4.6v-flash'
  }
};
```

### 4. 启动后端

```bash
cd material-purchase-backend
mvn clean install
mvn spring-boot:run
```

后端服务将运行在 `http://localhost:8081`

### 5. 启动前端

```bash
cd material-purchase-frontend
npm install
npm run dev
```

前端服务将运行在 `http://localhost:5173`

### 6. 默认账号

- 用户名：`admin`
- 密码：`admin123`

## API 接口

### 用户相关
- `POST /user/login` - 用户登录
- `POST /user/register` - 用户注册
- `GET /user/list` - 用户列表
- `POST /user` - 新增用户
- `PUT /user` - 更新用户
- `DELETE /user/{id}` - 删除用户
- `GET /user/current` - 获取当前用户

### 辅料相关
- `GET /product/list` - 辅料列表
- `POST /product/search` - 搜索辅料
- `POST /product` - 新增辅料
- `PUT /product` - 更新辅料
- `DELETE /product/{id}` - 删除辅料

### 供应商相关
- `GET /supplier/list` - 供应商列表
- `GET /supplier/{id}` - 供应商详情
- `POST /supplier` - 新增供应商
- `PUT /supplier` - 更新供应商
- `DELETE /supplier/{id}` - 删除供应商
- `GET /supplier-product/list` - 供应商-辅料关系列表
- `POST /supplier-product` - 新增供应商-辅料关系
- `PUT /supplier-product` - 更新供应商-辅料关系
- `DELETE /supplier-product/{id}` - 删除供应商-辅料关系

### 项目相关
- `GET /project/list` - 项目列表
- `GET /project/{id}` - 项目详情
- `POST /project` - 新增项目
- `PUT /project` - 更新项目
- `DELETE /project/{id}` - 删除项目
- `GET /scheme/list/{projectId}` - 方案列表
- `POST /scheme` - 新增方案
- `PUT /scheme` - 更新方案
- `DELETE /scheme/{id}` - 删除方案
- `GET /scheme-item/list/{schemeId}` - 方案明细列表
- `POST /scheme-item` - 新增方案明细
- `PUT /scheme-item` - 更新方案明细
- `DELETE /scheme-item/{id}` - 删除方案明细

### 采购需求相关
- `GET /requirement/list` - 需求列表
- `GET /requirement/{id}` - 需求详情
- `POST /requirement` - 新增需求
- `PUT /requirement` - 更新需求
- `DELETE /requirement/{id}` - 删除需求
- `POST /requirement/from-scheme/{schemeId}` - 从方案创建需求
- `POST /requirement/submit/{id}` - 提交需求审核
- `POST /requirement/audit` - 审核需求
- `POST /requirement/confirm` - 确认需求的货期和货款
- `POST /requirement/negotiate` - 提交议价
- `POST /requirement/negotiate/audit` - 审核议价

### 采购订单相关
- `GET /purchase-order/list` - 采购订单列表
- `GET /purchase-order/detail/{id}` - 采购订单详情
- `POST /purchase-order/create-from-requirement/{requirementId}` - 从需求创建采购订单
- `PUT /purchase-order/status/{id}` - 更新采购订单状态
- `PUT /purchase-order/item/status/{itemId}` - 更新采购订单项状态
- `PUT /purchase-order/item/supplier/{itemId}` - 更新采购订单项供应商
- `DELETE /purchase-order/delete/{id}` - 删除采购订单
- `GET /purchase-urge/list/{orderId}` - 采购催单列表
- `POST /purchase-urge/create` - 新增采购催单
- `POST /purchase-urge/respond/{urgeId}` - 响应采购催单
- `DELETE /purchase-urge/delete/{id}` - 删除采购催单

### 库存相关
- `GET /inventory/list` - 库存列表
- `GET /inventory/warehouse/{warehouseId}` - 按仓库查询库存
- `GET /inventory/product/{productId}` - 按商品查询库存
- `POST /inventory/update` - 更新库存

### 分类相关
- `GET /category/list` - 分类列表
- `POST /category` - 新增分类
- `PUT /category` - 更新分类
- `DELETE /category/{id}` - 删除分类

### AI 识别相关
- `POST /material/recognize` - AI 辅料识别
- `POST /material/search-by-image` - 图片搜索辅料

## 技术实现

### 后端架构
- **Controller层**：处理 HTTP 请求和响应
- **Service层**：实现业务逻辑
- **Mapper层**：处理数据库操作
- **Entity层**：定义数据模型
- **DTO层**：定义数据传输对象
- **Interceptor层**：实现请求拦截和认证
- **ExceptionHandler层**：统一处理异常

### 前端架构
- **组件化开发**：基于 Vue 3 Composition API
- **路由管理**：基于 Vue Router
- **状态管理**：基于 Pinia
- **API封装**：基于 Axios
- **表单验证**：基于 Element Plus 表单验证
- **响应式设计**：适配不同屏幕尺寸

## 开发说明

### 后端开发
1. **环境搭建**：安装 JDK 17+、Maven 3.6+、MySQL 8.0+
2. **IDE推荐**：IntelliJ IDEA、Eclipse
3. **代码规范**：遵循 Java 代码规范，使用 Lombok 简化代码
4. **测试**：编写单元测试和集成测试

### 前端开发
1. **环境搭建**：安装 Node.js 16+、npm 7+
2. **IDE推荐**：Visual Studio Code
3. **代码规范**：遵循 Vue 代码规范，使用 ESLint 和 Prettier
4. **测试**：编写单元测试和端到端测试

### 数据库开发
1. **表结构设计**：遵循数据库设计规范，使用 InnoDB 引擎
2. **索引优化**：为常用查询字段创建索引
3. **事务管理**：使用 Spring 事务管理确保数据一致性
4. **备份策略**：定期备份数据库

## 部署说明

### 生产环境部署

1. **后端部署**：
   - 打包成 Jar 文件：`mvn clean package`
   - 使用 Docker 容器化部署
   - 使用 Nginx 做反向代理

2. **前端部署**：
   - 构建生产版本：`npm run build`
   - 将 `dist` 目录部署到 Nginx 或 CDN
   - 配置 Nginx 反向代理

3. **数据库部署**：
   - 使用 MySQL 8.0+
   - 配置主从复制确保高可用
   - 配置定期备份

### 环境变量配置

**后端环境变量**：
- `SPRING_DATASOURCE_URL`：数据库连接 URL
- `SPRING_DATASOURCE_USERNAME`：数据库用户名
- `SPRING_DATASOURCE_PASSWORD`：数据库密码
- `JWT_SECRET`：JWT 密钥

**前端环境变量**：
- `VITE_API_BASE_URL`：API 基础 URL
- `VITE_ZHIPU_API_KEY`：智谱大模型 API 密钥

## 性能优化

### 后端优化
- 使用 MyBatis 缓存提升查询性能
- 使用 HikariCP 连接池管理数据库连接
- 使用 Redis 缓存热点数据
- 优化 SQL 查询，避免全表扫描
- 使用异步处理提升并发性能

### 前端优化
- 使用 Vite 构建工具提升构建速度
- 使用 Vue 3 组合式 API 提升运行性能
- 使用虚拟列表处理大量数据
- 使用图片懒加载提升页面加载速度
- 使用 CDN 加速静态资源加载

## 安全措施

### 后端安全
- 使用 JWT 进行身份认证
- 使用 Spring Security 进行权限控制
- 对密码进行加密存储
- 防止 SQL 注入攻击
- 防止 XSS 攻击
- 防止 CSRF 攻击

### 前端安全
- 对用户输入进行验证
- 防止 XSS 攻击
- 防止 CSRF 攻击
- 使用 HTTPS 加密传输
- 合理设置 CORS 策略

## 许可证

MIT License

## 联系方式

- 项目地址：[https://github.com/zhenglinleo-netizen/wms-project](https://github.com/zhenglinleo-netizen/wms-project)
- 作者：Leo
- 邮箱：1161607439@qq.com

## 更新日志

### v1.0.0 (2026-01-17)
- 初始版本发布
- 实现完整的辅料采购管理平台功能
- 集成 AI 智能辅料识别功能
- 支持完整的采购流程管理
