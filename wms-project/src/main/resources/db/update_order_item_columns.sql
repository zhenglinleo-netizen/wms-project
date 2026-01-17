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

-- 修改order_item表添加新字段（兼容MySQL 5.7及以下版本）
-- 先查询字段是否存在，不存在再添加

-- 1. 添加material_name字段
DELIMITER $$
CREATE PROCEDURE AddMaterialNameColumn()
BEGIN
    DECLARE column_exists INT DEFAULT 0;
    SELECT COUNT(*) INTO column_exists FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'order_item' AND column_name = 'material_name';
    IF column_exists = 0 THEN
        ALTER TABLE `order_item` ADD COLUMN `material_name` VARCHAR(100) DEFAULT NULL COMMENT '辅料名称';
    END IF;
END$$
DELIMITER ;
CALL AddMaterialNameColumn();
DROP PROCEDURE AddMaterialNameColumn;

-- 2. 添加material_code字段
DELIMITER $$
CREATE PROCEDURE AddMaterialCodeColumn()
BEGIN
    DECLARE column_exists INT DEFAULT 0;
    SELECT COUNT(*) INTO column_exists FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'order_item' AND column_name = 'material_code';
    IF column_exists = 0 THEN
        ALTER TABLE `order_item` ADD COLUMN `material_code` VARCHAR(50) DEFAULT NULL COMMENT '辅料编码';
    END IF;
END$$
DELIMITER ;
CALL AddMaterialCodeColumn();
DROP PROCEDURE AddMaterialCodeColumn;

-- 3. 添加specification字段
DELIMITER $$
CREATE PROCEDURE AddSpecificationColumn()
BEGIN
    DECLARE column_exists INT DEFAULT 0;
    SELECT COUNT(*) INTO column_exists FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'order_item' AND column_name = 'specification';
    IF column_exists = 0 THEN
        ALTER TABLE `order_item` ADD COLUMN `specification` VARCHAR(100) DEFAULT NULL COMMENT '规格';
    END IF;
END$$
DELIMITER ;
CALL AddSpecificationColumn();
DROP PROCEDURE AddSpecificationColumn;

-- 4. 添加unit字段
DELIMITER $$
CREATE PROCEDURE AddUnitColumn()
BEGIN
    DECLARE column_exists INT DEFAULT 0;
    SELECT COUNT(*) INTO column_exists FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'order_item' AND column_name = 'unit';
    IF column_exists = 0 THEN
        ALTER TABLE `order_item` ADD COLUMN `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位';
    END IF;
END$$
DELIMITER ;
CALL AddUnitColumn();
DROP PROCEDURE AddUnitColumn;

-- 5. 添加status字段
DELIMITER $$
CREATE PROCEDURE AddStatusColumn()
BEGIN
    DECLARE column_exists INT DEFAULT 0;
    SELECT COUNT(*) INTO column_exists FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'order_item' AND column_name = 'status';
    IF column_exists = 0 THEN
        ALTER TABLE `order_item` ADD COLUMN `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待采购，purchasing-采购中，completed-已完成，cancelled-已取消';
    END IF;
END$$
DELIMITER ;
CALL AddStatusColumn();
DROP PROCEDURE AddStatusColumn;