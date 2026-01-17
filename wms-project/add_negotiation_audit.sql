-- 创建议价审核记录表
CREATE TABLE `negotiation_audit` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `requirement_id` BIGINT NOT NULL COMMENT '关联的需求单ID',
  `negotiation_data` JSON NOT NULL COMMENT '议价数据，包含所有明细的议价信息',
  `negotiated_total` DECIMAL(15, 2) NOT NULL COMMENT '议价总额',
  `submitted_by` BIGINT NOT NULL COMMENT '提交人ID',
  `submitted_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `audited_by` BIGINT COMMENT '审核人ID',
  `audited_at` DATETIME COMMENT '审核时间',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '审核状态：pending(待审核), approved(已通过), rejected(已拒绝)',
  `rejection_reason` TEXT COMMENT '驳回原因',
  PRIMARY KEY (`id`),
  INDEX `idx_requirement_id` (`requirement_id`),
  INDEX `idx_status` (`status`),
  FOREIGN KEY (`requirement_id`) REFERENCES `requirement` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='议价审核记录表';

-- 更新requirement表的status字段，添加negotiating状态
ALTER TABLE `requirement` MODIFY COLUMN `status` VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态：draft(草稿), pending(待审核), approved(已通过), rejected(已驳回), cancelled(已取消), confirming(待确定), negotiating(议价中), negotiating_pending(议价待审核), confirmed(已确定)';
