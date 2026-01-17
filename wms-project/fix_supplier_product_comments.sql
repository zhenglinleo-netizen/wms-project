# 修复supplier_product表字段注释乱码问题

## 执行步骤
1. 打开MySQL命令行工具
2. 执行以下SQL语句：

## 修复SQL语句
USE wms_db;

-- 修复字段注释
ALTER TABLE supplier_product MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID';
ALTER TABLE supplier_product MODIFY COLUMN supplier_id BIGINT NOT NULL COMMENT '供应商ID';
ALTER TABLE supplier_product MODIFY COLUMN product_id BIGINT NOT NULL COMMENT '辅料ID';
ALTER TABLE supplier_product MODIFY COLUMN price DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格';
ALTER TABLE supplier_product MODIFY COLUMN status TINYINT DEFAULT 1 COMMENT '状态：0-停用，1-启用';

-- 修复表注释
ALTER TABLE supplier_product COMMENT '供应商产品关系表';

-- 验证修复结果
SHOW FULL COLUMNS FROM supplier_product;