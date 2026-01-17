# 修复supplier_product表字段注释乱码问题
$mysqlCommand = "mysql -u root -p -D wms -e `"
$mysqlCommand += "ALTER TABLE `supplier_product` "
$mysqlCommand += "MODIFY COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID', "
$mysqlCommand += "MODIFY COLUMN `supplier_id` BIGINT NOT NULL COMMENT '供应商ID', "
$mysqlCommand += "MODIFY COLUMN `product_id` BIGINT NOT NULL COMMENT '辅料ID', "
$mysqlCommand += "MODIFY COLUMN `price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格', "
$mysqlCommand += "MODIFY COLUMN `status` TINYINT DEFAULT 1 COMMENT '状态：0-停用，1-启用'; "
$mysqlCommand += "ALTER TABLE `supplier_product` COMMENT '供应商产品关系表'; "
$mysqlCommand += "SHOW FULL COLUMNS FROM `supplier_product`;`""

Write-Host "执行MySQL命令修复字段注释..."
Write-Host $mysqlCommand

# 执行命令
Invoke-Expression $mysqlCommand