# 数据库字段注释修复指南

## 问题描述
数据库的supplier-product数据表中各个字段的备注出现乱码，需要重新设置正确的注释。

## 解决方案
由于PowerShell执行MySQL命令的限制，建议使用以下方法手动执行修复：

### 方法一：使用MySQL命令行
1. **打开命令提示符**（CMD）
2. **执行以下命令**：
   ```bash
   mysql -u root -p wms
   ```
3. **输入密码**后进入MySQL命令行
4. **执行修复语句**：
   ```sql
   ALTER TABLE supplier_product MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID';
   ALTER TABLE supplier_product MODIFY COLUMN supplier_id BIGINT NOT NULL COMMENT '供应商ID';
   ALTER TABLE supplier_product MODIFY COLUMN product_id BIGINT NOT NULL COMMENT '辅料ID';
   ALTER TABLE supplier_product MODIFY COLUMN price DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格';
   ALTER TABLE supplier_product MODIFY COLUMN status TINYINT DEFAULT 1 COMMENT '状态：0-停用，1-启用';
   ALTER TABLE supplier_product COMMENT '供应商产品关系表';
   ```
5. **验证修复结果**：
   ```sql
   SHOW FULL COLUMNS FROM supplier_product;
   ```

### 方法二：使用MySQL Workbench
1. 打开MySQL Workbench
2. 连接到本地MySQL服务器
3. 选择wms数据库
4. 在SQL编辑器中执行上述修复语句
5. 查看执行结果

## 修复后的字段说明
| 字段名 | 数据类型 | 默认值 | 注释 |
|-------|---------|--------|------|
| id | bigint | 自增 | ID |
| supplier_id | bigint | 无 | 供应商ID |
| product_id | bigint | 无 | 辅料ID |
| price | decimal(10,2) | 0.00 | 价格 |
| status | tinyint | 1 | 状态：0-停用，1-启用 |

## 注意事项
- 确保MySQL服务正在运行
- 使用正确的数据库用户名和密码
- 确保当前用户有修改表结构的权限
- 如果遇到权限问题，请使用具有足够权限的用户登录

## 技术支持
如果遇到任何问题，请联系技术支持人员。