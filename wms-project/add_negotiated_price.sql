-- 添加negotiated_price字段到requirement_item表
ALTER TABLE requirement_item ADD COLUMN negotiated_price DECIMAL(10, 2) DEFAULT 0 COMMENT '议价单价';
