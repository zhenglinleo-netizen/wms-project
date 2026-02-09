-- 为material表添加文件哈希字段，用于图片查重
ALTER TABLE `material` ADD COLUMN IF NOT EXISTS `file_hash` VARCHAR(100) DEFAULT NULL COMMENT '文件哈希值（MD5）';

-- 为file_hash字段添加索引，提高查询效率
CREATE INDEX IF NOT EXISTS `idx_file_hash` ON `material` (`file_hash`);