-- 修复supplier_product表字段注释乱码问题
ALTER TABLE `supplier_product`
MODIFY COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
MODIFY COLUMN `supplier_id` BIGINT NOT NULL COMMENT '供应商ID',
MODIFY COLUMN `product_id` BIGINT NOT NULL COMMENT '辅料ID',
MODIFY COLUMN `price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格',
MODIFY COLUMN `status` TINYINT DEFAULT 1 COMMENT '状态：0-停用，1-启用';

-- 修改表注释
ALTER TABLE `supplier_product` COMMENT '供应商产品关系表';

-- 验证修复结果
SHOW FULL COLUMNS FROM `supplier_product`;

-- 显示表注释
SHOW CREATE TABLE `supplier_product`;
