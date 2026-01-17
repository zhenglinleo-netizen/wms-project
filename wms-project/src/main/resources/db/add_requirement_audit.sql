CREATE TABLE IF NOT EXISTS `requirement_audit` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `requirement_id` BIGINT NOT NULL,
  `requirement_data` JSON NOT NULL,
  `total_amount` DECIMAL(15, 2) NOT NULL,
  `submitted_by` BIGINT NOT NULL,
  `submitted_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `audited_by` BIGINT DEFAULT NULL,
  `audited_at` DATETIME DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending',
  `rejection_reason` TEXT,
  PRIMARY KEY (`id`),
  INDEX `idx_requirement_id` (`requirement_id`),
  INDEX `idx_status` (`status`),
  FOREIGN KEY (`requirement_id`) REFERENCES `requirement` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `requirement` MODIFY COLUMN `status` VARCHAR(20) DEFAULT 'draft';
