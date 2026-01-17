-- 更新供应商表结构，添加新字段

-- 添加合作开始时间字段
ALTER TABLE supplier ADD COLUMN cooperation_start_time DATE COMMENT '合作开始时间';

-- 添加工艺模块匹配字段
ALTER TABLE supplier ADD COLUMN process_module_match VARCHAR(255) COMMENT '工艺模块匹配';

-- 添加核心擅长工艺字段
ALTER TABLE supplier ADD COLUMN core_expertise VARCHAR(255) COMMENT '核心擅长工艺';

-- 添加不擅长/风险字段
ALTER TABLE supplier ADD COLUMN weaknesses VARCHAR(255) COMMENT '不擅长/风险';

-- 添加供应商角色字段
ALTER TABLE supplier ADD COLUMN supplier_role VARCHAR(255) COMMENT '供应商角色';
