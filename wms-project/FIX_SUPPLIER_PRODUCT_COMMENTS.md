# 修复 supplier_product 表字段注释乱码问题

## 问题描述
数据库的 supplier_product 数据表中各个字段的备注出现乱码，需要重新设置字段注释。

## 解决方案
使用以下SQL语句来修复字段注释：

### 步骤1：打开MySQL命令行工具
```bash
mysql -u root -p
```

### 步骤2：选择数据库
```sql
USE wms;
```

### 步骤3：执行修复语句
```sql
-- 修复字段注释
ALTER TABLE `supplier_product`
MODIFY COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
MODIFY COLUMN `supplier_id` BIGINT NOT NULL COMMENT '供应商ID',
MODIFY COLUMN `product_id` BIGINT NOT NULL COMMENT '辅料ID',
MODIFY COLUMN `price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格',
MODIFY COLUMN `status` TINYINT DEFAULT 1 COMMENT '状态：0-停用，1-启用';

-- 修复表注释
ALTER TABLE `supplier_product` COMMENT '供应商产品关系表';

-- 验证修复结果
SHOW FULL COLUMNS FROM `supplier_product`;
```

### 步骤4：检查修复结果
执行完上述命令后，查看 `supplier_product` 表的字段注释是否已经正确显示。

## 字段说明
- **id**: 主键ID
- **supplier_id**: 供应商ID
- **product_id**: 辅料ID
- **price**: 价格
- **status**: 状态（0-停用，1-启用）

## 注意事项
- 确保MySQL数据库使用的是UTF-8编码（utf8mb4）
- 执行命令时，确保当前用户有修改表结构的权限
- 如果遇到权限问题，请使用具有足够权限的用户登录MySQL