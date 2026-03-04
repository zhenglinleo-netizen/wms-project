-- 用户行为数据表
-- 用于记录用户在智能辅料库中的关键行为，为协同过滤推荐提供数据支撑

CREATE TABLE IF NOT EXISTS `user_behavior` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '行为ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `material_id` BIGINT NOT NULL COMMENT '辅料ID（对应product表）',
  `behavior_type` VARCHAR(20) NOT NULL COMMENT '行为类型：browse-浏览，favorite-收藏，add_to_scheme-添加到方案，purchase-采购',
  `weight` DECIMAL(3,2) DEFAULT 1.00 COMMENT '行为权重：浏览1.0，收藏2.0，添加到方案3.0，采购5.0',
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '浏览器UA',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '行为发生时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_behavior_type` (`behavior_type`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_user_material` (`user_id`, `material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为记录表';

-- 创建用户-辅料行为汇总表（用于提高查询效率）
CREATE TABLE IF NOT EXISTS `user_material_score` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `material_id` BIGINT NOT NULL COMMENT '辅料ID',
  `score` DECIMAL(10,2) DEFAULT 0.00 COMMENT '行为综合得分',
  `browse_count` INT DEFAULT 0 COMMENT '浏览次数',
  `favorite_count` INT DEFAULT 0 COMMENT '收藏次数',
  `add_to_scheme_count` INT DEFAULT 0 COMMENT '添加到方案次数',
  `purchase_count` INT DEFAULT 0 COMMENT '采购次数',
  `last_behavior_time` DATETIME DEFAULT NULL COMMENT '最后行为时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_material` (`user_id`, `material_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_score` (`score` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-辅料行为得分汇总表';

-- 创建物料相似度表（基于协同过滤的物品相似度）
CREATE TABLE IF NOT EXISTS `material_similarity` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `material_id_1` BIGINT NOT NULL COMMENT '辅料ID 1',
  `material_id_2` BIGINT NOT NULL COMMENT '辅料ID 2',
  `similarity_score` DECIMAL(5,4) DEFAULT 0.0000 COMMENT '相似度分数（0-1）',
  `co_occurrence_count` INT DEFAULT 0 COMMENT '共同出现次数',
  `calculate_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '计算时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_material_pair` (`material_id_1`, `material_id_2`),
  KEY `idx_material_1` (`material_id_1`),
  KEY `idx_material_2` (`material_id_2`),
  KEY `idx_similarity` (`similarity_score` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料相似度表';

-- 初始化一些测试数据（可选）
-- INSERT INTO `user_behavior` (`user_id`, `material_id`, `behavior_type`, `weight`) VALUES
-- (1, 101, 'browse', 1.00),
-- (1, 102, 'favorite', 2.00),
-- (2, 101, 'purchase', 5.00),
-- (2, 103, 'add_to_scheme', 3.00);
