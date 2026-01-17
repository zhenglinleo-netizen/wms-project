-- 更新供应商表结构
ALTER TABLE supplier
ADD COLUMN cooperation_start_time DATETIME DEFAULT NULL COMMENT '合作开始时间',
ADD COLUMN process_module_match VARCHAR(500) DEFAULT NULL COMMENT '工艺模块匹配',
ADD COLUMN core_expertise VARCHAR(500) DEFAULT NULL COMMENT '核心擅长工艺',
ADD COLUMN weaknesses VARCHAR(500) DEFAULT NULL COMMENT '不擅长/风险',
ADD COLUMN supplier_role VARCHAR(100) DEFAULT NULL COMMENT '供应商角色';

-- 验证更新结果
SHOW COLUMNS FROM supplier;

-- 创建议应商产品关系表
CREATE TABLE IF NOT EXISTS supplier_product (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  supplier_id BIGINT NOT NULL COMMENT '供应商ID',
  product_id BIGINT NOT NULL COMMENT '辅料ID',
  price DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格',
  status TINYINT DEFAULT 1 COMMENT '状态：0-停用，1-启用',
  PRIMARY KEY (id),
  UNIQUE KEY uk_supplier_product (supplier_id, product_id),
  KEY idx_supplier_id (supplier_id),
  KEY idx_product_id (product_id),
  FOREIGN KEY (supplier_id) REFERENCES supplier (id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES material (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商产品关系表';

-- 验证表创建结果
SHOW TABLES LIKE 'supplier_product';
SHOW COLUMNS FROM supplier_product;