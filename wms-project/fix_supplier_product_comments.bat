@echo off
echo 修复supplier_product表字段注释乱码问题...

:: 执行修复操作
echo 修复id字段注释...
mysql -u root -p123456 -D wms_db -e "ALTER TABLE supplier_product MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID';"

echo 修复supplier_id字段注释...
mysql -u root -p123456 -D wms_db -e "ALTER TABLE supplier_product MODIFY COLUMN supplier_id BIGINT NOT NULL COMMENT '供应商ID';"

echo 修复product_id字段注释...
mysql -u root -p123456 -D wms_db -e "ALTER TABLE supplier_product MODIFY COLUMN product_id BIGINT NOT NULL COMMENT '辅料ID';"

echo 修复price字段注释...
mysql -u root -p123456 -D wms_db -e "ALTER TABLE supplier_product MODIFY COLUMN price DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格';"

echo 修复status字段注释...
mysql -u root -p123456 -D wms_db -e "ALTER TABLE supplier_product MODIFY COLUMN status TINYINT DEFAULT 1 COMMENT '状态：0-停用，1-启用';"

echo 修复表注释...
mysql -u root -p123456 -D wms_db -e "ALTER TABLE supplier_product COMMENT '供应商产品关系表';"

:: 验证修复结果
echo 验证修复结果...
mysql -u root -p123456 -D wms_db -e "SHOW FULL COLUMNS FROM supplier_product;"

echo 修复完成！
pause