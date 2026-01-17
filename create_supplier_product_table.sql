-- 创建供应商-辅料关联表
CREATE TABLE IF NOT EXISTS supplier_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    product_id BIGINT NOT NULL COMMENT '辅料ID',
    price DECIMAL(10, 2) NOT NULL COMMENT '供应商提供该辅料的价格',
    status INT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    FOREIGN KEY (supplier_id) REFERENCES supplier(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES material(id) ON DELETE CASCADE,
    UNIQUE KEY uk_supplier_product (supplier_id, product_id) COMMENT '唯一约束：一个供应商不能重复关联同一个辅料'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商-辅料关联表';
