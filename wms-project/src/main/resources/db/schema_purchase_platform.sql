-- 采购平台系统数据库表结构

-- 用户表（扩展）
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `company` VARCHAR(100) DEFAULT NULL COMMENT '所属公司',
  `role` VARCHAR(20) DEFAULT 'designer' COMMENT '角色：admin-管理员，designer-设计师，buyer-采购员',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审核，1-启用，2-禁用',
  `avatar` VARCHAR(200) DEFAULT NULL COMMENT '头像',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 辅料分类表
CREATE TABLE IF NOT EXISTS `material_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` VARCHAR(100) NOT NULL COMMENT '分类名称',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='辅料分类表';

-- 材质类型表
CREATE TABLE IF NOT EXISTS `material_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '材质ID',
  `type_name` VARCHAR(100) NOT NULL COMMENT '材质名称',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='材质类型表';

-- 供应商表
CREATE TABLE IF NOT EXISTS `supplier` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '供应商ID',
  `supplier_code` VARCHAR(50) NOT NULL COMMENT '供应商编码',
  `supplier_name` VARCHAR(100) NOT NULL COMMENT '供应商名称',
  `contact_person` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
  `rating` INT DEFAULT 5 COMMENT '评级：1-5',
  `status` TINYINT DEFAULT 2 COMMENT '状态：0-下架，1-上架，2-待审核',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_supplier_code` (`supplier_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- 辅料表
CREATE TABLE IF NOT EXISTS `material` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '辅料ID',
  `material_code` VARCHAR(50) NOT NULL COMMENT '辅料编码',
  `material_name` VARCHAR(100) NOT NULL COMMENT '辅料名称',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `type_id` BIGINT DEFAULT NULL COMMENT '材质ID',
  `color` VARCHAR(50) DEFAULT NULL COMMENT '颜色',
  `specification` VARCHAR(100) DEFAULT NULL COMMENT '规格',
  `unit` VARCHAR(20) DEFAULT '件' COMMENT '单位',
  `image_url` VARCHAR(500) DEFAULT NULL COMMENT '图片URL',
  `description` TEXT DEFAULT NULL COMMENT '描述',
  `vector_id` VARCHAR(100) DEFAULT NULL COMMENT '向量ID（Milvus）',
  `price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '参考价格',
  `supplier_id` BIGINT DEFAULT NULL COMMENT '主要供应商ID',
  `expected_delivery_days` INT DEFAULT 0 COMMENT '预计货期（天）',
  `min_stock` DECIMAL(10,2) DEFAULT 0.00 COMMENT '最低库存',
  `status` TINYINT DEFAULT 2 COMMENT '状态：0-下架，1-上架，2-待审核',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_material_code` (`material_code`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_supplier_id` (`supplier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='辅料表';

-- 项目表
CREATE TABLE IF NOT EXISTS `project` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_code` VARCHAR(50) NOT NULL COMMENT '项目编码',
  `project_name` VARCHAR(100) NOT NULL COMMENT '项目名称',
  `description` TEXT DEFAULT NULL COMMENT '项目描述',
  `start_date` DATE DEFAULT NULL COMMENT '开始日期',
  `end_date` DATE DEFAULT NULL COMMENT '结束日期',
  `owner_id` BIGINT NOT NULL COMMENT '项目负责人ID',
  `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-进行中，completed-已完成，cancelled-已取消',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_code` (`project_code`),
  KEY `idx_owner_id` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';

-- 项目成员表
CREATE TABLE IF NOT EXISTS `project_member` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `project_id` BIGINT NOT NULL COMMENT '项目ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role` VARCHAR(20) DEFAULT 'member' COMMENT '角色：owner-负责人，member-成员',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_user` (`project_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目成员表';

-- 方案表
CREATE TABLE IF NOT EXISTS `scheme` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '方案ID',
  `scheme_code` VARCHAR(50) NOT NULL COMMENT '方案编码',
  `scheme_name` VARCHAR(100) NOT NULL COMMENT '方案名称',
  `project_id` BIGINT NOT NULL COMMENT '项目ID',
  `version` INT DEFAULT 1 COMMENT '版本号',
  `description` TEXT DEFAULT NULL COMMENT '方案描述',
  `creator_id` BIGINT NOT NULL COMMENT '创建人ID',
  `status` VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft-草稿，published-已发布',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scheme_code` (`scheme_code`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='方案表';

-- 方案明细表
CREATE TABLE IF NOT EXISTS `scheme_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `scheme_id` BIGINT NOT NULL COMMENT '方案ID',
  `material_id` BIGINT NOT NULL COMMENT '辅料ID',
  `quantity` DECIMAL(10,2) NOT NULL COMMENT '数量',
  `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
  `purpose` VARCHAR(200) DEFAULT NULL COMMENT '用途说明',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`),
  KEY `idx_scheme_id` (`scheme_id`),
  KEY `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='方案明细表';

-- 采购需求单表
CREATE TABLE IF NOT EXISTS `requirement` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '需求单ID',
  `requirement_code` VARCHAR(50) NOT NULL COMMENT '需求单号',
  `project_id` BIGINT DEFAULT NULL COMMENT '项目ID',
  `scheme_id` BIGINT DEFAULT NULL COMMENT '方案ID',
  `creator_id` BIGINT NOT NULL COMMENT '创建人ID',
  `purpose` VARCHAR(200) DEFAULT NULL COMMENT '用途说明',
  `delivery_date` DATE DEFAULT NULL COMMENT '交付日期',
  `expected_delivery_date` DATE DEFAULT NULL COMMENT '预期货期',
  `deadline` DATE DEFAULT NULL COMMENT '截止日期',
  `priority` VARCHAR(20) DEFAULT 'normal' COMMENT '优先级：low-低，normal-普通，high-高，urgent-紧急',
  `status` VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft-草稿，pending-待审核，confirming-待确定，negotiating-待议价，negotiating_pending-议价待审核，approved-已通过，rejected-已拒绝，cancelled-已取消',
  `total_payment` DECIMAL(10,2) DEFAULT 0.00 COMMENT '货款总额',
  `confirmed_time` DATETIME DEFAULT NULL COMMENT '确认时间',
  `confirmed_by` BIGINT DEFAULT NULL COMMENT '确认人ID',
  `auditor_id` BIGINT DEFAULT NULL COMMENT '审核人ID',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `audit_remark` VARCHAR(500) DEFAULT NULL COMMENT '审核意见',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_requirement_code` (`requirement_code`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购需求单表';

-- 需求明细表
CREATE TABLE IF NOT EXISTS `requirement_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `requirement_id` BIGINT NOT NULL COMMENT '需求单ID',
  `material_id` BIGINT NOT NULL COMMENT '辅料ID',
  `quantity` DECIMAL(10,2) NOT NULL COMMENT '数量',
  `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
  `purpose` VARCHAR(200) DEFAULT NULL COMMENT '用途说明',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_requirement_id` (`requirement_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='需求明细表';

-- 添加议价单价字段
ALTER TABLE `requirement_item` ADD COLUMN IF NOT EXISTS `negotiated_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '议价单价';

-- 创建议价审核记录表
CREATE TABLE IF NOT EXISTS `negotiation_audit` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `requirement_id` BIGINT NOT NULL COMMENT '关联的需求单ID',
  `negotiation_data` JSON NOT NULL COMMENT '议价数据，包含所有明细的议价信息',
  `negotiated_total` DECIMAL(15, 2) NOT NULL COMMENT '议价总额',
  `submitted_by` BIGINT NOT NULL COMMENT '提交人ID',
  `submitted_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `audited_by` BIGINT DEFAULT NULL COMMENT '审核人ID',
  `audited_at` DATETIME DEFAULT NULL COMMENT '审核时间',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '审核状态：pending(待审核), approved(已通过), rejected(已拒绝)',
  `rejection_reason` TEXT COMMENT '驳回原因',
  PRIMARY KEY (`id`),
  INDEX `idx_requirement_id` (`requirement_id`),
  INDEX `idx_status` (`status`),
  FOREIGN KEY (`requirement_id`) REFERENCES `requirement` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='议价审核记录表';

-- 采购订单表
CREATE TABLE IF NOT EXISTS `purchase_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_code` VARCHAR(50) NOT NULL COMMENT '订单号',
  `requirement_id` BIGINT DEFAULT NULL COMMENT '需求单ID',
  `supplier_id` BIGINT NOT NULL COMMENT '供应商ID',
  `total_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '订单总金额',
  `order_date` DATE DEFAULT NULL COMMENT '订单日期',
  `delivery_date` DATE DEFAULT NULL COMMENT '交货日期',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待处理，processing-采购中，completed-已完成，cancelled-已取消',
  `creator_id` BIGINT NOT NULL COMMENT '创建人ID',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_code` (`order_code`),
  KEY `idx_supplier_id` (`supplier_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS `order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `material_id` BIGINT NOT NULL COMMENT '辅料ID',
  `material_name` VARCHAR(100) DEFAULT NULL COMMENT '辅料名称',
  `material_code` VARCHAR(50) DEFAULT NULL COMMENT '辅料编码',
  `specification` VARCHAR(100) DEFAULT NULL COMMENT '规格',
  `quantity` DECIMAL(10,2) NOT NULL COMMENT '数量',
  `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
  `unit_price` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `total_price` DECIMAL(10,2) NOT NULL COMMENT '总价',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待采购，purchasing-采购中，completed-已完成，cancelled-已取消',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 采购催促记录表
CREATE TABLE IF NOT EXISTS `purchase_urge` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `user_id` BIGINT NOT NULL COMMENT '催促人ID',
  `urge_content` VARCHAR(500) DEFAULT NULL COMMENT '催促内容',
  `response_content` VARCHAR(500) DEFAULT NULL COMMENT '响应内容',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待响应，responded-已响应',
  `urge_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '催促时间',
  `response_time` DATETIME DEFAULT NULL COMMENT '响应时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购催促记录表';

-- 仓库表
CREATE TABLE IF NOT EXISTS `warehouse` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '仓库ID',
  `warehouse_code` VARCHAR(50) NOT NULL COMMENT '仓库编码',
  `warehouse_name` VARCHAR(100) NOT NULL COMMENT '仓库名称',
  `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
  `manager_id` BIGINT DEFAULT NULL COMMENT '管理员ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_warehouse_code` (`warehouse_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

-- 库存表
CREATE TABLE IF NOT EXISTS `inventory` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '库存ID',
  `warehouse_id` BIGINT NOT NULL COMMENT '仓库ID',
  `material_id` BIGINT NOT NULL COMMENT '辅料ID',
  `quantity` DECIMAL(10,2) DEFAULT 0.00 COMMENT '库存数量',
  `avg_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '平均单价',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_warehouse_material` (`warehouse_id`, `material_id`),
  KEY `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 入库单表
CREATE TABLE IF NOT EXISTS `stock_in` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '入库单ID',
  `in_code` VARCHAR(50) NOT NULL COMMENT '入库单号',
  `warehouse_id` BIGINT NOT NULL COMMENT '仓库ID',
  `order_id` BIGINT DEFAULT NULL COMMENT '订单ID',
  `in_type` VARCHAR(20) DEFAULT 'purchase' COMMENT '入库类型：purchase-采购入库，return-退货入库，transfer-调拨入库',
  `operator_id` BIGINT NOT NULL COMMENT '操作人ID',
  `in_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-已取消，1-已完成',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_in_code` (`in_code`),
  KEY `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单表';

-- 入库明细表
CREATE TABLE IF NOT EXISTS `stock_in_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stock_in_id` BIGINT NOT NULL COMMENT '入库单ID',
  `material_id` BIGINT NOT NULL COMMENT '辅料ID',
  `quantity` DECIMAL(10,2) NOT NULL COMMENT '数量',
  `price` DECIMAL(10,2) DEFAULT NULL COMMENT '单价',
  PRIMARY KEY (`id`),
  KEY `idx_stock_in_id` (`stock_in_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库明细表';

-- 出库单表
CREATE TABLE IF NOT EXISTS `stock_out` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '出库单ID',
  `out_code` VARCHAR(50) NOT NULL COMMENT '出库单号',
  `warehouse_id` BIGINT NOT NULL COMMENT '仓库ID',
  `requirement_id` BIGINT DEFAULT NULL COMMENT '需求单ID',
  `out_type` VARCHAR(20) DEFAULT 'requirement' COMMENT '出库类型：requirement-需求出库，transfer-调拨出库',
  `operator_id` BIGINT NOT NULL COMMENT '操作人ID',
  `out_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '出库时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审核，1-已通过，2-已驳回，3-已取消',
  `auditor_id` BIGINT DEFAULT NULL COMMENT '审核人ID',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_out_code` (`out_code`),
  KEY `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单表';

-- 出库明细表
CREATE TABLE IF NOT EXISTS `stock_out_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stock_out_id` BIGINT NOT NULL COMMENT '出库单ID',
  `material_id` BIGINT NOT NULL COMMENT '辅料ID',
  `quantity` DECIMAL(10,2) NOT NULL COMMENT '数量',
  PRIMARY KEY (`id`),
  KEY `idx_stock_out_id` (`stock_out_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库明细表';

-- AI识别记录表
CREATE TABLE IF NOT EXISTS `ai_recognition` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
  `recognition_result` TEXT DEFAULT NULL COMMENT '识别结果JSON',
  `confidence` DECIMAL(5,2) DEFAULT NULL COMMENT '置信度',
  `corrected` TINYINT DEFAULT 0 COMMENT '是否已人工校正：0-否，1-是',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI识别记录表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS `system_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT DEFAULT NULL COMMENT '配置值',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 插入初始管理员用户（密码：admin123）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `status`) VALUES
('admin', 'admin123', '系统管理员', 'admin', 1);




