-- 更新material表的status字段，支持三种状态
ALTER TABLE `material` 
MODIFY COLUMN `status` TINYINT DEFAULT 2 COMMENT '状态：0-下架，1-上架，2-待审核';
