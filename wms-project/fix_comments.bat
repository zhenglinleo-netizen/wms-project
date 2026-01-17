@echo off
echo 修复supplier_product表字段注释...

:: 设置MySQL连接信息
set MYSQL_USER=root
set MYSQL_PASSWORD=123456
set MYSQL_DB=wms

:: 执行SQL修复命令
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% -D %MYSQL_DB% -e ^
"ALTER TABLE supplier_product ^
MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID', ^
MODIFY COLUMN supplier_id BIGINT NOT NULL COMMENT '供应商ID', ^
MODIFY COLUMN product_id BIGINT NOT NULL COMMENT '辅料ID', ^
MODIFY COLUMN price DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格', ^
MODIFY COLUMN status TINYINT DEFAULT 1 COMMENT '状态：0-停用，1-启用'; ^
ALTER TABLE supplier_product COMMENT '供应商产品关系表'; ^
SHOW FULL COLUMNS FROM supplier_product;"

echo 修复完成！
pause