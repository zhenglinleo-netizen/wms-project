@echo off
echo 检查数据库状态并修复字段注释...

:: 检查数据库是否存在
mysql -u root -p123456 -e "SHOW DATABASES;" > databases.txt
findstr /i "wms" databases.txt > nul
if %errorlevel% equ 0 (
    echo 找到wms数据库，开始修复字段注释...
    
    :: 执行修复操作
    mysql -u root -p123456 -D wms -e "ALTER TABLE supplier_product MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID';"
    mysql -u root -p123456 -D wms -e "ALTER TABLE supplier_product MODIFY COLUMN supplier_id BIGINT NOT NULL COMMENT '供应商ID';"
    mysql -u root -p123456 -D wms -e "ALTER TABLE supplier_product MODIFY COLUMN product_id BIGINT NOT NULL COMMENT '辅料ID';"
    mysql -u root -p123456 -D wms -e "ALTER TABLE supplier_product MODIFY COLUMN price DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格';"
    mysql -u root -p123456 -D wms -e "ALTER TABLE supplier_product MODIFY COLUMN status TINYINT DEFAULT 1 COMMENT '状态：0-停用，1-启用';"
    mysql -u root -p123456 -D wms -e "ALTER TABLE supplier_product COMMENT '供应商产品关系表';"
    
    :: 验证修复结果
    echo 修复完成，验证结果：
    mysql -u root -p123456 -D wms -e "SHOW FULL COLUMNS FROM supplier_product;"
    
) else (
    echo 未找到wms数据库，请检查数据库是否存在。
    echo 当前数据库列表：
    type databases.txt
)

del databases.txt
pause