/*
 Navicat Premium Dump SQL

 Source Server         : aaa
 Source Server Type    : MySQL
 Source Server Version : 80032 (8.0.32)
 Source Host           : localhost:3306
 Source Schema         : wms_db

 Target Server Type    : MySQL
 Target Server Version : 80032 (8.0.32)
 File Encoding         : 65001

 Date: 09/02/2026 14:24:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_recognition
-- ----------------------------
DROP TABLE IF EXISTS `ai_recognition`;
CREATE TABLE `ai_recognition`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `recognition_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '识别结果JSON',
  `confidence` decimal(5, 2) NULL DEFAULT NULL COMMENT '置信度',
  `corrected` tinyint NULL DEFAULT 0 COMMENT '是否已人工校正：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI识别记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_recognition
-- ----------------------------

-- ----------------------------
-- Table structure for applicable_stage
-- ----------------------------
DROP TABLE IF EXISTS `applicable_stage`;
CREATE TABLE `applicable_stage`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `stage_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `stage_name`(`stage_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of applicable_stage
-- ----------------------------
INSERT INTO `applicable_stage` VALUES (1, '开发/小批/量产', '2026-01-22 16:18:11', '2026-01-22 16:18:11');
INSERT INTO `applicable_stage` VALUES (2, '开发/小批', '2026-01-22 16:18:11', '2026-01-22 16:18:11');
INSERT INTO `applicable_stage` VALUES (3, '小批/量产', '2026-01-22 16:18:11', '2026-01-22 16:18:11');

-- ----------------------------
-- Table structure for effect_layer
-- ----------------------------
DROP TABLE IF EXISTS `effect_layer`;
CREATE TABLE `effect_layer`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '效果层级ID',
  `layer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '效果层级名称',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `layer_name`(`layer_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '效果层级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of effect_layer
-- ----------------------------
INSERT INTO `effect_layer` VALUES (1, '哑光', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (2, '柔软', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (3, '光泽', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (4, '柔软/挺括', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (5, '高弹', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (6, '夜光/高反射', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (7, '光泽/哑光/镀色', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (8, '光泽/柔软', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (9, '哑光/亮光', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (10, '立体纹理', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (11, '透明/彩色', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (12, '装饰/加固', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (13, '平整/高质感', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (14, '装饰', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (15, '防泼水', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (16, '高弹/柔软', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (17, '高反光', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (18, '花纹多样', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (19, '哑光/多色', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (20, '光泽/哑光', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (21, '渐变色', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (22, '平整/立体', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (23, '阻燃', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (24, '抗菌', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (25, '夜光/荧光', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `effect_layer` VALUES (26, '高弹/低回弹', '2026-01-22 16:18:10', '2026-01-22 16:18:10');

-- ----------------------------
-- Table structure for inventory
-- ----------------------------
DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '库存ID',
  `warehouse_id` bigint NOT NULL COMMENT '仓库ID',
  `material_id` bigint NOT NULL COMMENT '辅料ID',
  `quantity` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '库存数量',
  `avg_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '平均单价',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_warehouse_material`(`warehouse_id` ASC, `material_id` ASC) USING BTREE,
  INDEX `idx_material_id`(`material_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '库存表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of inventory
-- ----------------------------
INSERT INTO `inventory` VALUES (1, 1, 3, 6.00, 0.00, '2026-01-11 22:25:46');
INSERT INTO `inventory` VALUES (2, 1, 2, 14.00, 0.00, '2026-01-11 22:25:53');
INSERT INTO `inventory` VALUES (3, 1, 4, 35.00, 0.00, '2026-01-13 21:07:35');

-- ----------------------------
-- Table structure for material
-- ----------------------------
DROP TABLE IF EXISTS `material`;
CREATE TABLE `material`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '???ID',
  `material_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '辅料编码',
  `material_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '辅料名称',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `type_id` bigint NULL DEFAULT NULL COMMENT '材质ID',
  `color` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '颜色',
  `specification` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规格',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '件' COMMENT '单位',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片URL',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `vector_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '向量ID（Milvus）',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '参考价格',
  `supplier_id` bigint NULL DEFAULT NULL COMMENT '主要供应商ID',
  `expected_delivery_days` int NULL DEFAULT 0 COMMENT '预计货期（天）',
  `min_stock` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '最低库存',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-停用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型',
  `style` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '风格',
  `module_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模块编号',
  `module_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模块名称',
  `applicable_stage` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用阶段',
  `process_category_id` bigint NULL DEFAULT NULL COMMENT '工艺大类ID',
  `material_layer_id` bigint NULL DEFAULT NULL COMMENT '材料层级ID',
  `process_layer_id` bigint NULL DEFAULT NULL COMMENT '工艺层级ID',
  `effect_layer_id` bigint NULL DEFAULT NULL COMMENT '效果层级ID',
  `material_category_id` bigint NULL DEFAULT NULL COMMENT '材料分类ID',
  `file_hash` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件哈希值（MD5）',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '多张图片URL，JSON格式',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_material_code`(`material_code` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_supplier_id`(`supplier_id` ASC) USING BTREE,
  INDEX `idx_file_hash`(`file_hash` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '辅料表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of material
-- ----------------------------
INSERT INTO `material` VALUES (5, 'M-001', 'LOGO 滴塑', 7, NULL, NULL, '', '个', NULL, '适用于T恤肩带等', NULL, 2.50, NULL, 3, 0.00, 1, '2026-01-22 14:22:36', '2026-01-22 16:35:21', NULL, NULL, 'M-001', 'LOGO 滴塑', '开发/小批/量产', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `material` VALUES (6, 'M-002', '细绳高密度编织', 8, NULL, NULL, '', '米', NULL, '适用于休闲鞋鞋带', NULL, 1.80, NULL, 3, 0.00, 1, '2026-01-22 14:22:36', '2026-01-22 16:35:13', NULL, NULL, 'M-002', '细绳高密度编织', '开发/小批', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `material` VALUES (7, 'M-003', '五金拉链', 4, NULL, NULL, '', '条', NULL, '适用于牛仔裤拉链', NULL, 8.50, NULL, 3, 0.00, 1, '2026-01-22 14:22:36', '2026-01-22 14:39:54', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `material` VALUES (8, 'M-004', '扁带高密度编织', 8, NULL, NULL, '', '米', NULL, '适用于衣领、鞋带、腰带', NULL, 2.20, NULL, 3, 0.00, 1, '2026-01-22 14:22:36', '2026-01-22 14:40:01', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `material` VALUES (9, 'M-005', '圆绳弹力绳', 8, NULL, NULL, '', '米', NULL, '适用于帽绳、抽绳、腰绳', NULL, 1.50, NULL, 3, 0.00, 1, '2026-01-22 14:22:36', '2026-01-22 14:40:04', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `material` VALUES (15, 'M202602085794', '装饰花--树脂--白色', 11, NULL, NULL, '', '件', '上传成功', '该辅料为花朵造型的装饰性贴片，由多片半透明至白色的圆形花瓣状部件组合而成，中心区域搭配金属质感配件与小珠片，整体以白色为主色调，辅以透明感部件营造轻盈视觉效果。属于装饰辅料范畴，具体为**装饰花**类型。其材质多为树脂（花瓣主体）、金属（中心配件）等，色彩清新雅致。工艺上采用多层组装形式，将花瓣部件与中心结构整合为立体花朵形态，效果层面呈现立体、通透且富有层次感的装饰效果。适用于礼服、婚纱、晚装等需要提升华丽度的服装品类，在生产阶段的最后装饰环节，以缝制或粘贴方式固定于服装面料表面，起到点缀造型、强化艺术（优雅、浪漫）风格与提升整体艺术感的作用，适配正式场合服饰的装饰需求，为成衣增添精致细节与视觉亮点。', NULL, 1.50, NULL, 0, 0.00, 1, '2026-02-08 23:52:01', '2026-02-09 00:29:29', '装饰花', '优雅、华丽、浪漫（适配礼服、婚纱等正式场合））', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '[\"http://localhost:9000/wms-bucket/cbbbae61-e30e-4b32-801c-38e0c00f6d9c.jpg\",\"http://localhost:9000/wms-bucket/cbbbae61-e30e-4b32-801c-38e0c00f6d9c.jpg\"]');
INSERT INTO `material` VALUES (16, 'M202602084151', '蝴蝶结--织带--粉白格子', 11, NULL, NULL, '', '件', 'http://localhost:9000/wms-bucket/6de6ffc4-0778-4179-b534-82b2722d3942.jpg', '该辅料为粉白格子图案的立体蝴蝶结织带制品，由化纤机织织物经裁剪、缝制等工艺形成蝴蝶结造型。其色彩柔和，造型精致可爱，属于典型的装饰性纺织辅料。主要用于服装（如儿童装、女士连衣裙等）、箱包、饰品等产品的表面装饰，通过缝制或粘贴等方式固定于成衣上，能够有效提升产品的美观度与时尚感，增添精致细节；适用于追求甜美、复古风格的服饰及配饰设计场景，是服装行业中常用的装饰辅料之一。', NULL, 2.00, NULL, 0, 0.00, 1, '2026-02-08 23:58:04', '2026-02-09 12:48:21', '蝴蝶结', '甜美、可爱、复古', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '[\"http://localhost:9000/wms-bucket/6de6ffc4-0778-4179-b534-82b2722d3942.jpg\"]');
INSERT INTO `material` VALUES (17, 'M202602093106', '针织装饰花--棉线--绿色', 11, NULL, NULL, '', '件', 'http://localhost:9000/wms-bucket/fdab8ff4-dbec-471c-9170-35e637063aab.jpg', '该辅料为手工钩织而成的四叶草形装饰花，采用棉线为原料，经针织工艺制成。整体以深绿色为主色调，中心区域搭配黑色与白色的环形装饰细节，形成色彩对比与视觉焦点。其通过钩针编织形成立体饱满的造型，兼具手工编织的自然温馨质感与装饰性。该装饰花可作为服装、箱包、家居饰品等的装饰部件，通过缝纫等方式固定在制品表面，起到点缀外观、提升产品手工艺品特色的作用，适用于追求复古、自然风格的制品装饰场景，在成品制作的后期阶段添加使用。', NULL, 1.00, NULL, 0, 0.00, 1, '2026-02-09 00:31:43', '2026-02-09 00:32:12', '针织装饰花', '手工编织风格，自然温馨，带有复古手工艺品特质', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '[\"http://localhost:9000/wms-bucket/fdab8ff4-dbec-471c-9170-35e637063aab.jpg\"]');

-- ----------------------------
-- Table structure for material_backup
-- ----------------------------
DROP TABLE IF EXISTS `material_backup`;
CREATE TABLE `material_backup`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '辅料ID',
  `material_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '辅料编码',
  `material_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '辅料名称',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `type_id` bigint NULL DEFAULT NULL COMMENT '材质ID',
  `color` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '颜色',
  `specification` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规格',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '件' COMMENT '单位',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片URL',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `vector_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '向量ID（Milvus）',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '参考价格',
  `supplier_id` bigint NULL DEFAULT NULL COMMENT '主要供应商ID',
  `expected_delivery_days` int NULL DEFAULT 0 COMMENT '预计货期（天）',
  `min_stock` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '最低库存',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-停用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `style` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_material_code`(`material_code` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_supplier_id`(`supplier_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '辅料表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of material_backup
-- ----------------------------
INSERT INTO `material_backup` VALUES (5, 'M-001', 'LOGO 滴塑', 7, NULL, NULL, '', '个', NULL, '适用于T恤肩带等', NULL, 2.50, NULL, 3, 0.00, 1, '2026-01-22 14:22:36', '2026-01-22 14:39:50', NULL, NULL);
INSERT INTO `material_backup` VALUES (6, 'M-002', '细绳高密度编织', 8, NULL, NULL, '', '米', NULL, '适用于休闲鞋鞋带', NULL, 1.80, NULL, 3, 0.00, 1, '2026-01-22 14:22:36', '2026-01-22 14:39:52', NULL, NULL);
INSERT INTO `material_backup` VALUES (7, 'M-003', '五金拉链', 4, NULL, NULL, '', '条', NULL, '适用于牛仔裤拉链', NULL, 8.50, NULL, 3, 0.00, 1, '2026-01-22 14:22:36', '2026-01-22 14:39:54', NULL, NULL);
INSERT INTO `material_backup` VALUES (8, 'M-004', '扁带高密度编织', 8, NULL, NULL, '', '米', NULL, '适用于衣领、鞋带、腰带', NULL, 2.20, NULL, 3, 0.00, 1, '2026-01-22 14:22:36', '2026-01-22 14:40:01', NULL, NULL);
INSERT INTO `material_backup` VALUES (9, 'M-005', '圆绳弹力绳', 8, NULL, NULL, '', '米', NULL, '适用于帽绳、抽绳、腰绳', NULL, 1.50, NULL, 3, 0.00, 1, '2026-01-22 14:22:36', '2026-01-22 14:40:04', NULL, NULL);

-- ----------------------------
-- Table structure for material_category
-- ----------------------------
DROP TABLE IF EXISTS `material_category`;
CREATE TABLE `material_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父分类ID',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '辅料分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of material_category
-- ----------------------------
INSERT INTO `material_category` VALUES (4, '五金类', 0, 1, '金属类辅料', '2026-01-22 14:22:35');
INSERT INTO `material_category` VALUES (5, '功能性辅料', 0, 2, '具有特定功能的辅料', '2026-01-22 14:22:35');
INSERT INTO `material_category` VALUES (6, '布艺类', 0, 3, '布料相关辅料', '2026-01-22 14:22:35');
INSERT INTO `material_category` VALUES (7, '滴塑类', 0, 4, '滴塑工艺辅料', '2026-01-22 14:22:35');
INSERT INTO `material_category` VALUES (8, '织带类', 0, 5, '各种织带辅料', '2026-01-22 14:22:35');
INSERT INTO `material_category` VALUES (9, '蝴蝶结', 0, 0, NULL, '2026-02-06 19:12:18');
INSERT INTO `material_category` VALUES (10, '胸针', 0, 0, NULL, '2026-02-06 23:22:45');
INSERT INTO `material_category` VALUES (11, '辅料', 0, 0, NULL, '2026-02-08 20:23:49');

-- ----------------------------
-- Table structure for material_category_detail
-- ----------------------------
DROP TABLE IF EXISTS `material_category_detail`;
CREATE TABLE `material_category_detail`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `category_name`(`category_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of material_category_detail
-- ----------------------------
INSERT INTO `material_category_detail` VALUES (1, '滴塑类', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_category_detail` VALUES (2, '织带类', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_category_detail` VALUES (3, '五金类', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_category_detail` VALUES (4, '布艺类', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_category_detail` VALUES (5, '功能性辅料', '2026-01-22 16:18:10', '2026-01-22 16:18:10');

-- ----------------------------
-- Table structure for material_layer
-- ----------------------------
DROP TABLE IF EXISTS `material_layer`;
CREATE TABLE `material_layer`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '材料层级ID',
  `layer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '材料层级名称',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `layer_name`(`layer_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '材料层级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of material_layer
-- ----------------------------
INSERT INTO `material_layer` VALUES (1, '涤纶', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (2, '棉/尼龙', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (3, '金属', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (4, '棉/涤纶/锦纶', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (5, '弹力纱/棉', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (6, '涤纶/反光丝', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (7, '铜/锌合金/不锈钢', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (8, '金属/树脂', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (9, '不锈钢/合金', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (10, 'PVC/TPU', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (11, '橡胶', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (12, '环保树脂', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (13, '棉/涤纶/尼龙', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (14, '涤纶/棉', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (15, '蕾丝/锦纶', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (16, '橡胶+涤纶', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (17, '反光膜/涤纶', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (18, '涤纶/尼龙', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (19, '铜/锌合金', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (20, '合金', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (21, '棉/涤纶', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `material_layer` VALUES (22, '涤纶/反光膜', '2026-01-22 16:18:10', '2026-01-22 16:18:10');

-- ----------------------------
-- Table structure for material_type
-- ----------------------------
DROP TABLE IF EXISTS `material_type`;
CREATE TABLE `material_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '材质ID',
  `type_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '材质名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '材质类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of material_type
-- ----------------------------

-- ----------------------------
-- Table structure for negotiation_audit
-- ----------------------------
DROP TABLE IF EXISTS `negotiation_audit`;
CREATE TABLE `negotiation_audit`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `requirement_id` bigint NOT NULL COMMENT '关联的需求单ID',
  `negotiation_data` json NOT NULL COMMENT '议价数据，包含所有明细的议价信息',
  `negotiated_total` decimal(15, 2) NOT NULL COMMENT '议价总额',
  `submitted_by` bigint NOT NULL COMMENT '提交人ID',
  `submitted_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `audited_by` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `audited_at` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'pending' COMMENT '审核状态：pending(待审核), approved(已通过), rejected(已拒绝)',
  `rejection_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '拒绝原因',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_requirement_id`(`requirement_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `negotiation_audit_ibfk_1` FOREIGN KEY (`requirement_id`) REFERENCES `requirement` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '议价审核记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of negotiation_audit
-- ----------------------------
INSERT INTO `negotiation_audit` VALUES (1, 14, '[{\"id\": null, \"unit\": null, \"price\": null, \"remark\": null, \"purpose\": null, \"quantity\": null, \"materialId\": 3, \"materialCode\": null, \"materialName\": null, \"requirementId\": null, \"specification\": null, \"negotiatedPrice\": 0.78, \"expectedDeliveryDays\": null}, {\"id\": null, \"unit\": null, \"price\": null, \"remark\": null, \"purpose\": null, \"quantity\": null, \"materialId\": 2, \"materialCode\": null, \"materialName\": null, \"requirementId\": null, \"specification\": null, \"negotiatedPrice\": 0.58, \"expectedDeliveryDays\": null}, {\"id\": null, \"unit\": null, \"price\": null, \"remark\": null, \"purpose\": null, \"quantity\": null, \"materialId\": 4, \"materialCode\": null, \"materialName\": null, \"requirementId\": null, \"specification\": null, \"negotiatedPrice\": 2.08, \"expectedDeliveryDays\": null}]', 36.16, 5, '2026-01-15 01:36:53', NULL, '2026-01-15 18:24:26', 'approved', NULL);
INSERT INTO `negotiation_audit` VALUES (2, 19, '[{\"id\": null, \"unit\": null, \"price\": null, \"remark\": null, \"purpose\": null, \"imageUrl\": null, \"quantity\": null, \"materialId\": 3, \"materialCode\": null, \"materialName\": null, \"requirementId\": null, \"specification\": null, \"negotiatedPrice\": 0.8, \"expectedDeliveryDays\": null}, {\"id\": null, \"unit\": null, \"price\": null, \"remark\": null, \"purpose\": null, \"imageUrl\": null, \"quantity\": null, \"materialId\": 4, \"materialCode\": null, \"materialName\": null, \"requirementId\": null, \"specification\": null, \"negotiatedPrice\": 2.1, \"expectedDeliveryDays\": null}]', 13.70, 4, '2026-01-15 19:17:03', 1, '2026-01-15 19:17:14', 'approved', NULL);

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `material_id` bigint NOT NULL COMMENT '辅料ID',
  `quantity` decimal(10, 2) NOT NULL COMMENT '数量',
  `unit_price` decimal(10, 2) NOT NULL COMMENT '单价',
  `total_price` decimal(10, 2) NOT NULL COMMENT '总价',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `material_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '辅料名称',
  `material_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '辅料编码',
  `specification` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规格',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '状态：pending-待采购，purchasing-采购中，completed-已完成，cancelled-已取消',
  `supplier_id` bigint NULL DEFAULT NULL COMMENT '供应商ID',
  `supplier_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '供应商名称',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 3, 5.00, 0.78, 3.90, NULL, '橙色（棕褐色）   皮革   铭牌', 'M202601040168', '', '件', 'pending', NULL, NULL);
INSERT INTO `order_item` VALUES (2, 1, 2, 9.00, 0.58, 5.22, NULL, '米色（浅棕色）   金属（拉链头部分）   拉链  ', 'M202601043438', '', '件', 'pending', NULL, NULL);
INSERT INTO `order_item` VALUES (3, 1, 4, 13.00, 2.08, 27.04, NULL, '白色   蕾丝   蕾丝花边  ', 'M202601133463', '', '件', 'pending', NULL, NULL);
INSERT INTO `order_item` VALUES (4, 2, 3, 4.00, 0.80, 3.20, NULL, '橙色（棕褐色）   皮革   铭牌', 'M202601040168', '', '件', 'pending', NULL, NULL);
INSERT INTO `order_item` VALUES (5, 2, 4, 5.00, 2.10, 10.50, NULL, '白色   蕾丝   蕾丝花边  ', 'M202601133463', '', '件', 'pending', NULL, NULL);

-- ----------------------------
-- Table structure for process_category
-- ----------------------------
DROP TABLE IF EXISTS `process_category`;
CREATE TABLE `process_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '工艺大类ID',
  `category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '工艺大类名称',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `category_name`(`category_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '工艺大类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of process_category
-- ----------------------------
INSERT INTO `process_category` VALUES (1, '滴塑', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_category` VALUES (2, '织带', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_category` VALUES (3, '五金', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_category` VALUES (4, '五金/尼龙', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_category` VALUES (5, '滴塑/橡胶', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_category` VALUES (6, '布艺', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_category` VALUES (7, '功能辅料', '2026-01-22 16:18:10', '2026-01-22 16:18:10');

-- ----------------------------
-- Table structure for process_layer
-- ----------------------------
DROP TABLE IF EXISTS `process_layer`;
CREATE TABLE `process_layer`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '工艺层级ID',
  `layer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '工艺层级名称',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `layer_name`(`layer_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '工艺层级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of process_layer
-- ----------------------------
INSERT INTO `process_layer` VALUES (1, '表面处理', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (2, '编织方式', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (3, '冲压/电镀', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (4, '平纹/斜纹/提花', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (5, '编织/捻线', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (6, '提花/反光涂层', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (7, '压铸/冲压/电镀', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (8, '注塑/冲压/电镀', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (9, '冲压/抛光', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (10, '模具注塑', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (11, '热压/模压', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (12, '滴胶/UV固化', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (13, '缝制/包边', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (14, '提花/印染', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (15, '编织/刺绣', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (16, 'PU涂层', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (17, '编织/包胶', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (18, '热压/缝制', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (19, '提花/印花', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (20, '浮雕编织', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (21, '拉丝/磨砂/多色电镀', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (22, '压铸/冲压', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (23, '滴塑/UV固化', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (24, '刺绣', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (25, '涂层/编织', '2026-01-22 16:18:10', '2026-01-22 16:18:10');
INSERT INTO `process_layer` VALUES (26, '缝制/热压', '2026-01-22 16:18:10', '2026-01-22 16:18:10');

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目编码',
  `project_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '项目描述',
  `start_date` date NULL DEFAULT NULL COMMENT '开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
  `owner_id` bigint NOT NULL COMMENT '项目负责人ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'active' COMMENT '状态：active-进行中，completed-已完成，cancelled-已取消',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_project_code`(`project_code` ASC) USING BTREE,
  INDEX `idx_owner_id`(`owner_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of project
-- ----------------------------
INSERT INTO `project` VALUES (1, 'PROJECT_1767544066760', '管理员项目测试三', '', NULL, NULL, 1, '进行中', '2026-01-05 00:27:46', '2026-01-14 23:59:10');
INSERT INTO `project` VALUES (4, 'PROJECT_1767596085389', '小明项目测试二', '', NULL, NULL, 2, '进行中', '2026-01-05 14:54:45', '2026-01-15 00:58:47');
INSERT INTO `project` VALUES (8, 'PROJECT_1767598485224', '小明项目测试一', '', '2026-01-06', '2026-01-23', 2, '未开始', '2026-01-05 15:34:45', '2026-01-15 00:58:25');
INSERT INTO `project` VALUES (12, 'PROJECT_1768118273680', '小赖项目测试一', '', NULL, NULL, 5, '进行中', '2026-01-11 15:57:53', '2026-01-11 15:58:24');
INSERT INTO `project` VALUES (13, 'PROJECT_1768118290728', '小赖项目测试二', '', NULL, NULL, 5, '进行中', '2026-01-11 15:58:10', '2026-01-11 15:58:44');
INSERT INTO `project` VALUES (14, 'PROJECT_1768403216509', '管理员项目测试一', '', NULL, NULL, 1, '进行中', '2026-01-14 23:06:56', '2026-01-14 23:20:05');
INSERT INTO `project` VALUES (15, 'PROJECT_1768404042449', '管理员项目测试二', '', NULL, NULL, 1, '未开始', '2026-01-14 23:20:42', '2026-01-14 23:20:42');
INSERT INTO `project` VALUES (16, 'PROJECT_1768461257038', '小卢项目测试一', '', NULL, NULL, 4, '进行中', '2026-01-15 15:14:17', '2026-01-15 15:14:43');
INSERT INTO `project` VALUES (17, 'PROJECT_1768461270854', '小卢项目测试二', '', NULL, NULL, 4, '进行中', '2026-01-15 15:14:30', '2026-01-15 15:15:07');
INSERT INTO `project` VALUES (18, 'PROJECT_1768475667671', '小卢项目测试三', '', NULL, NULL, 4, '进行中', '2026-01-15 19:14:27', '2026-01-15 19:14:46');

-- ----------------------------
-- Table structure for project_member
-- ----------------------------
DROP TABLE IF EXISTS `project_member`;
CREATE TABLE `project_member`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'member' COMMENT '角色：owner-负责人，member-成员',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_project_user`(`project_id` ASC, `user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '项目成员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of project_member
-- ----------------------------

-- ----------------------------
-- Table structure for purchase_order
-- ----------------------------
DROP TABLE IF EXISTS `purchase_order`;
CREATE TABLE `purchase_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `requirement_id` bigint NULL DEFAULT NULL COMMENT '需求单ID',
  `supplier_id` bigint NOT NULL COMMENT '供应商ID',
  `total_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '订单总金额',
  `order_date` date NULL DEFAULT NULL COMMENT '订单日期',
  `delivery_date` date NULL DEFAULT NULL COMMENT '交货日期',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '状态：pending-待处理，processing-采购中，completed-已完成，cancelled-已取消',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_code`(`order_code` ASC) USING BTREE,
  INDEX `idx_supplier_id`(`supplier_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '采购订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of purchase_order
-- ----------------------------
INSERT INTO `purchase_order` VALUES (1, 'PO1768472666057', 14, 1, 36.16, '2026-01-15', NULL, 'pending', 5, NULL, '2026-01-15 18:24:26', '2026-01-15 18:24:26');
INSERT INTO `purchase_order` VALUES (2, 'PO1768475833583', 19, 1, 13.70, '2026-01-15', NULL, 'pending', 4, NULL, '2026-01-15 19:17:14', '2026-01-15 19:17:13');

-- ----------------------------
-- Table structure for purchase_urge
-- ----------------------------
DROP TABLE IF EXISTS `purchase_urge`;
CREATE TABLE `purchase_urge`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '催促人ID',
  `urge_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '催促内容',
  `response_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '响应内容',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '状态：pending-待响应，responded-已响应',
  `urge_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '催促时间',
  `response_time` datetime NULL DEFAULT NULL COMMENT '响应时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '采购催促记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of purchase_urge
-- ----------------------------

-- ----------------------------
-- Table structure for requirement
-- ----------------------------
DROP TABLE IF EXISTS `requirement`;
CREATE TABLE `requirement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '需求单ID',
  `requirement_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '需求单号',
  `project_id` bigint NULL DEFAULT NULL COMMENT '项目ID',
  `scheme_id` bigint NULL DEFAULT NULL COMMENT '方案ID',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `purpose` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用途说明',
  `delivery_date` date NULL DEFAULT NULL COMMENT '交付日期',
  `priority` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'normal' COMMENT '优先级：low-低，normal-普通，high-高，urgent-紧急',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'draft' COMMENT '状态：draft-草稿，pending-待审核，confirming-待确定，negotiating-待议价，negotiating_pending-议价待审核，approved-已通过，rejected-已拒绝，cancelled-已取消',
  `auditor_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `audit_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `expected_delivery_date` date NULL DEFAULT NULL COMMENT '预期货期',
  `deadline` date NULL DEFAULT NULL COMMENT '截止日期',
  `total_payment` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '货款总额',
  `expected_delivery_days` int NULL DEFAULT NULL COMMENT '预计货期天数',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_requirement_code`(`requirement_code` ASC) USING BTREE,
  INDEX `idx_creator_id`(`creator_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '采购需求单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of requirement
-- ----------------------------
INSERT INTO `requirement` VALUES (14, 'REQ1768308489309', 12, 27, 5, '采购方案：小赖设计测试一一', NULL, 'normal', 'confirmed', 1, '2026-01-13 22:22:40', NULL, '从方案自动生成', '2026-01-13 20:48:09', '2026-01-15 18:22:14', '2029-08-09', '2026-02-27', 36.16, 17);
INSERT INTO `requirement` VALUES (19, 'REQ1768475746163', 17, 35, 4, '采购方案：小卢设计测试二一', NULL, 'normal', 'confirmed', 1, '2026-01-15 19:16:38', NULL, '从方案自动生成', '2026-01-15 19:15:46', '2026-01-15 19:17:13', '2030-02-25', '2026-02-26', 13.70, 17);

-- ----------------------------
-- Table structure for requirement_audit
-- ----------------------------
DROP TABLE IF EXISTS `requirement_audit`;
CREATE TABLE `requirement_audit`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `requirement_id` bigint NOT NULL,
  `requirement_data` json NOT NULL,
  `total_amount` decimal(15, 2) NOT NULL,
  `submitted_by` bigint NOT NULL,
  `submitted_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `audited_by` bigint NULL DEFAULT NULL,
  `audited_at` datetime NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'pending',
  `rejection_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_requirement_id`(`requirement_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `requirement_audit_ibfk_1` FOREIGN KEY (`requirement_id`) REFERENCES `requirement` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of requirement_audit
-- ----------------------------
INSERT INTO `requirement_audit` VALUES (4, 14, '[{\"id\": 20, \"unit\": \"件\", \"price\": 0.8, \"remark\": \"\", \"purpose\": \"\", \"quantity\": 5.0, \"materialId\": 3, \"materialCode\": \"M202601040168\", \"materialName\": \"橙色（棕褐色）   皮革   铭牌\", \"requirementId\": 14, \"specification\": \"\", \"negotiatedPrice\": 0.0}, {\"id\": 21, \"unit\": \"件\", \"price\": 0.6, \"remark\": \"\", \"purpose\": \"\", \"quantity\": 9.0, \"materialId\": 2, \"materialCode\": \"M202601043438\", \"materialName\": \"米色（浅棕色）   金属（拉链头部分）   拉链  \", \"requirementId\": 14, \"specification\": \"\", \"negotiatedPrice\": 0.0}, {\"id\": 22, \"unit\": \"件\", \"price\": 2.1, \"remark\": \"\", \"purpose\": \"\", \"quantity\": 13.0, \"materialId\": 4, \"materialCode\": \"M202601133463\", \"materialName\": \"白色   蕾丝（通常由棉、涤纶等纺织纤维混纺制成，具有网眼及刺绣装饰结构）   蕾丝花边  \", \"requirementId\": 14, \"specification\": \"\", \"negotiatedPrice\": 0.0}]', 36.70, 5, '2026-01-13 20:48:20', 1, '2026-01-13 22:22:40', 'approved', NULL);
INSERT INTO `requirement_audit` VALUES (8, 19, '[{\"id\": 31, \"unit\": \"件\", \"price\": 0.8, \"remark\": \"\", \"purpose\": \"\", \"imageUrl\": null, \"quantity\": 4.0, \"materialId\": 3, \"materialCode\": \"M202601040168\", \"materialName\": \"橙色（棕褐色）   皮革   铭牌\", \"requirementId\": 19, \"specification\": \"\", \"negotiatedPrice\": 0.0, \"expectedDeliveryDays\": 10}, {\"id\": 32, \"unit\": \"件\", \"price\": 2.1, \"remark\": \"\", \"purpose\": \"\", \"imageUrl\": null, \"quantity\": 5.0, \"materialId\": 4, \"materialCode\": \"M202601133463\", \"materialName\": \"白色   蕾丝   蕾丝花边  \", \"requirementId\": 19, \"specification\": \"\", \"negotiatedPrice\": 0.0, \"expectedDeliveryDays\": 11}]', 13.70, 4, '2026-01-15 19:16:22', 1, '2026-01-15 19:16:38', 'approved', NULL);

-- ----------------------------
-- Table structure for requirement_item
-- ----------------------------
DROP TABLE IF EXISTS `requirement_item`;
CREATE TABLE `requirement_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `requirement_id` bigint NOT NULL COMMENT '需求单ID',
  `material_id` bigint NOT NULL COMMENT '辅料ID',
  `quantity` decimal(10, 2) NOT NULL COMMENT '数量',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位',
  `purpose` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用途说明',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `negotiated_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '议价单价',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_requirement_id`(`requirement_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '需求明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of requirement_item
-- ----------------------------
INSERT INTO `requirement_item` VALUES (1, 4, 2, 3.00, '件', '', '', 0.55);
INSERT INTO `requirement_item` VALUES (2, 4, 3, 1.00, '件', '', '', 0.77);
INSERT INTO `requirement_item` VALUES (9, 9, 3, 10.00, '件', '', '', 0.80);
INSERT INTO `requirement_item` VALUES (10, 9, 2, 5.00, '件', '', '', 0.60);
INSERT INTO `requirement_item` VALUES (11, 9, 1, 2.00, '颗', '', '', 0.70);
INSERT INTO `requirement_item` VALUES (20, 14, 3, 5.00, '件', '', '', 0.78);
INSERT INTO `requirement_item` VALUES (21, 14, 2, 9.00, '件', '', '', 0.58);
INSERT INTO `requirement_item` VALUES (22, 14, 4, 13.00, '件', '', '', 2.08);
INSERT INTO `requirement_item` VALUES (25, 16, 4, 13.00, '件', '', '', 0.00);
INSERT INTO `requirement_item` VALUES (26, 16, 3, 8.00, '件', '', '', 0.00);
INSERT INTO `requirement_item` VALUES (27, 17, 4, 7.00, '件', '', '', 0.00);
INSERT INTO `requirement_item` VALUES (28, 17, 3, 4.00, '件', '', '', 0.00);
INSERT INTO `requirement_item` VALUES (29, 18, 3, 20.00, '件', '', '', 0.00);
INSERT INTO `requirement_item` VALUES (30, 18, 4, 38.00, '件', '', '', 0.00);
INSERT INTO `requirement_item` VALUES (31, 19, 3, 4.00, '件', '', '', 0.80);
INSERT INTO `requirement_item` VALUES (32, 19, 4, 5.00, '件', '', '', 2.10);

-- ----------------------------
-- Table structure for scheme
-- ----------------------------
DROP TABLE IF EXISTS `scheme`;
CREATE TABLE `scheme`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '方案ID',
  `scheme_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '方案编码',
  `scheme_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '方案名称',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `version` int NULL DEFAULT 1 COMMENT '版本号',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '方案描述',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '待确定',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_scheme_code`(`scheme_code` ASC) USING BTREE,
  INDEX `idx_project_id`(`project_id` ASC) USING BTREE,
  CONSTRAINT `chk_scheme_status` CHECK (`status` in (_gbk'??ȷ??',_gbk'??ȷ??',_gbk'???ύ'))
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '方案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of scheme
-- ----------------------------
INSERT INTO `scheme` VALUES (1, 'SCHEME_1767544449413', '割让给', 1, 1, '', 1, '已提交', '2026-01-05 00:34:09', '2026-01-11 23:04:37');
INSERT INTO `scheme` VALUES (3, 'SCHEME_1767544812613', 'dgrdfg', 1, 1, '', 1, '待确定', '2026-01-05 00:40:12', '2026-01-11 22:50:16');
INSERT INTO `scheme` VALUES (4, 'SCHEME_1767593442893', '简约', 1, 1, '', 1, '待确定', '2026-01-05 14:10:42', '2026-01-11 22:50:16');
INSERT INTO `scheme` VALUES (5, 'SCHEME_1767593577967', 'FEIJIAN', 1, 1, '', 1, '待确定', '2026-01-05 14:12:57', '2026-01-11 22:50:16');
INSERT INTO `scheme` VALUES (6, 'SCHEME_1767596359989', '简约', 5, 1, '', 2, '待确定', '2026-01-05 14:59:19', '2026-01-11 22:50:16');
INSERT INTO `scheme` VALUES (9, 'SCHEME_1767598549068', '测试小明简明', 4, 1, '', 2, '待确定', '2026-01-05 15:35:49', '2026-01-11 22:50:16');
INSERT INTO `scheme` VALUES (20, 'SCHEME_1768118324621', '小赖设计测试二一', 13, 1, '', 5, '待确定', '2026-01-11 15:58:44', '2026-01-11 22:50:16');
INSERT INTO `scheme` VALUES (21, 'SCHEME_1768147795093', '小赖设计测试一二', 12, 1, '', 5, '已确定', '2026-01-12 00:09:55', '2026-01-12 10:31:25');
INSERT INTO `scheme` VALUES (22, 'SCHEME_1768186478523', '小赖设计测试一三', 12, 1, '', 5, '待确定', '2026-01-12 10:54:38', '2026-01-12 10:54:38');
INSERT INTO `scheme` VALUES (27, 'SCHEME_1768308240551', '小赖设计测试一一', 12, 1, '', 5, '已提交', '2026-01-13 20:44:00', '2026-01-13 20:48:09');
INSERT INTO `scheme` VALUES (28, 'SCHEME_1768404005062', '管理员设计测试一一', 14, 1, '', 1, '待确定', '2026-01-14 23:20:05', '2026-01-14 23:20:05');
INSERT INTO `scheme` VALUES (29, 'SCHEME_1768406383391', '管理员设计测试一二', 14, 1, '', 1, '待确定', '2026-01-14 23:59:43', '2026-01-14 23:59:43');
INSERT INTO `scheme` VALUES (30, 'SCHEME_1768410259158', '小明设计测试二一', 4, 1, '', 2, '待确定', '2026-01-15 01:04:19', '2026-01-15 01:04:19');
INSERT INTO `scheme` VALUES (32, 'SCHEME_1768410691837', '小明设计测试二二', 4, 1, '', 2, '已提交', '2026-01-15 01:11:31', '2026-01-15 01:13:13');
INSERT INTO `scheme` VALUES (33, 'SCHEME_1768461283899', '小卢设计测试一一', 16, 1, '', 4, '待确定', '2026-01-15 15:14:43', '2026-01-15 15:14:43');
INSERT INTO `scheme` VALUES (34, 'SCHEME_1768461293772', '小卢设计测试一二', 16, 1, '', 4, '已提交', '2026-01-15 15:14:53', '2026-01-15 15:15:53');
INSERT INTO `scheme` VALUES (35, 'SCHEME_1768461307163', '小卢设计测试二一', 17, 1, '', 4, '已提交', '2026-01-15 15:15:07', '2026-01-15 19:15:46');
INSERT INTO `scheme` VALUES (36, 'SCHEME_1768475686582', '小卢设计测试三一', 18, 1, '', 4, '待确定', '2026-01-15 19:14:46', '2026-01-15 19:14:46');
INSERT INTO `scheme` VALUES (37, 'SCHEME_1768475699112', '小卢设计测试三二', 18, 1, '', 4, '待确定', '2026-01-15 19:14:59', '2026-01-15 19:14:59');
INSERT INTO `scheme` VALUES (38, 'SCHEME_1768475705422', '小卢设计测试三三', 18, 1, '', 4, '待确定', '2026-01-15 19:15:05', '2026-01-15 19:15:05');
INSERT INTO `scheme` VALUES (39, 'SCHEME_1768475713998', '小卢设计测试二二', 17, 1, '', 4, '待确定', '2026-01-15 19:15:14', '2026-01-15 19:15:14');

-- ----------------------------
-- Table structure for scheme_item
-- ----------------------------
DROP TABLE IF EXISTS `scheme_item`;
CREATE TABLE `scheme_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `scheme_id` bigint NOT NULL COMMENT '方案ID',
  `material_id` bigint NOT NULL COMMENT '辅料ID',
  `quantity` decimal(10, 2) NOT NULL COMMENT '数量',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位',
  `purpose` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用途说明',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_scheme_id`(`scheme_id` ASC) USING BTREE,
  INDEX `idx_material_id`(`material_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '方案明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of scheme_item
-- ----------------------------
INSERT INTO `scheme_item` VALUES (8, 9, 3, 1.00, '件', '', '', NULL);
INSERT INTO `scheme_item` VALUES (10, 10, 2, 8.00, '件', '', '', NULL);
INSERT INTO `scheme_item` VALUES (11, 10, 1, 1.00, '颗', '', '', NULL);
INSERT INTO `scheme_item` VALUES (12, 1, 2, 10.00, '件', '', '', NULL);
INSERT INTO `scheme_item` VALUES (17, 21, 3, 10.00, '件', '', '', NULL);
INSERT INTO `scheme_item` VALUES (18, 21, 2, 5.00, '件', '', '', NULL);
INSERT INTO `scheme_item` VALUES (19, 21, 1, 2.00, '颗', '', '', NULL);
INSERT INTO `scheme_item` VALUES (20, 20, 3, 1.00, '件', '', '', NULL);
INSERT INTO `scheme_item` VALUES (31, 27, 3, 5.00, '件', '', '', NULL);
INSERT INTO `scheme_item` VALUES (32, 27, 2, 9.00, '件', '', '', NULL);
INSERT INTO `scheme_item` VALUES (33, 27, 4, 13.00, '件', '', '', NULL);
INSERT INTO `scheme_item` VALUES (36, 32, 4, 7.00, '件', '', '', NULL);
INSERT INTO `scheme_item` VALUES (37, 32, 3, 4.00, '件', '', '', NULL);
INSERT INTO `scheme_item` VALUES (38, 34, 3, 20.00, '件', '', '', NULL);
INSERT INTO `scheme_item` VALUES (39, 34, 4, 38.00, '件', '', '', NULL);
INSERT INTO `scheme_item` VALUES (40, 35, 3, 4.00, '件', '', '', NULL);
INSERT INTO `scheme_item` VALUES (41, 35, 4, 5.00, '件', '', '', NULL);

-- ----------------------------
-- Table structure for supplier
-- ----------------------------
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '供应商ID',
  `supplier_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '供应商编码',
  `supplier_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '供应商名称',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `rating` int NULL DEFAULT 5 COMMENT '评级：1-5',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-停用，1-启用',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `cooperation_start_time` datetime NULL DEFAULT NULL COMMENT '合作开始时间',
  `process_module_match` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工艺模块匹配',
  `core_expertise` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '核心擅长工艺',
  `weaknesses` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '不擅长/风险',
  `supplier_role` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '供应商角色',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_supplier_code`(`supplier_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '供应商表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of supplier
-- ----------------------------
INSERT INTO `supplier` VALUES (1, 'SUPP1768472666062', '默认供应商', '管理员', '13800138000', 'admin@example.com', '默认地址', 5, 1, NULL, '2026-01-15 18:24:26', '2026-01-15 18:24:26', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `supplier` VALUES (3, 'SUPP1768560563365', '中大旺达', '管理员', '13913913913', '', '中大', 3, 1, '', '2026-01-16 18:49:23', '2026-01-16 18:49:40', '2025-01-01 00:00:00', 'M-003', '五金冲压/电镀', '薄镀层易脱落', '稳定量产/快反型');
INSERT INTO `supplier` VALUES (4, 'SUPP1768574960886', '中大明大', '管理员', '13913913913', '', '中大', 3, 1, '', '2026-01-16 22:49:21', '2026-01-16 22:49:20', '2025-01-01 00:00:00', 'M-001/M-003', 'LOGO滴塑/五金冲压', '大批量交期慢', '稳定量产/快反型');
INSERT INTO `supplier` VALUES (5, 'SUPP1769869131091', '阿什杜官方', '爱上丢包', '12312312312', '', '', 5, 1, '', '2026-01-16 22:49:21', '2026-01-31 22:18:51', NULL, '', '', '', '');
INSERT INTO `supplier` VALUES (6, 'SUPP1769869147729', '俺是个丢包我', '奥收到比u比', '12312312312', '', '', 5, 1, '', '2026-01-16 22:49:21', '2026-01-31 22:19:07', NULL, '', '', '', '');

-- ----------------------------
-- Table structure for supplier_product
-- ----------------------------
DROP TABLE IF EXISTS `supplier_product`;
CREATE TABLE `supplier_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `supplier_id` bigint NOT NULL COMMENT '供应商ID',
  `product_id` bigint NOT NULL COMMENT '辅料ID',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '价格',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-停用，1-启用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_supplier_product`(`supplier_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `idx_supplier_id`(`supplier_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  CONSTRAINT `supplier_product_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `supplier_product_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `material` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '供应商产品关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of supplier_product
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'designer' COMMENT '角色：admin-管理员，designer-设计师，buyer-采购员',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-待审核，1-启用，2-禁用',
  `avatar` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `company` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属公司',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', 'admin123', '系统管理员', NULL, NULL, 'admin', 1, NULL, '2025-12-28 23:14:37', '2025-12-28 23:14:37', NULL);
INSERT INTO `sys_user` VALUES (2, 'xiaoming', '12345', '小明', '13913913966', '1234587@156.com', 'designer', 1, NULL, '2025-12-30 14:32:00', '2026-01-15 00:28:10', '明记');
INSERT INTO `sys_user` VALUES (3, 'xiaojin', '12345', '小金', '13713713799', 'ndqiown@ans.com', 'admin', 1, NULL, '2025-12-30 15:56:04', '2026-01-07 22:32:52', '金九有限');
INSERT INTO `sys_user` VALUES (4, 'xiaolu', '12345', '小卢', '13713713795', 'sasfdiown@ans.com', 'designer', 1, NULL, '2025-12-30 15:56:45', '2026-01-11 15:48:43', '卢克明有限');
INSERT INTO `sys_user` VALUES (5, 'xiaolai', '12345', '赖清德', '13913913913', 'asdhoai@asdn.com', 'buyer', 1, NULL, '2026-01-07 15:30:34', '2026-01-15 01:31:50', '清莱有限');
INSERT INTO `sys_user` VALUES (6, 'xiaoying', '12345', '蔡英文', '13613613613', 'caiyingwen@qq.com', 'buyer', 0, NULL, '2026-01-07 16:43:22', '2026-01-07 16:43:22', '文英蔡有限公司');

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '配置值',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_config
-- ----------------------------

-- ----------------------------
-- Table structure for warehouse
-- ----------------------------
DROP TABLE IF EXISTS `warehouse`;
CREATE TABLE `warehouse`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '仓库ID',
  `warehouse_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '仓库编码',
  `warehouse_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '仓库名称',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `manager_id` bigint NULL DEFAULT NULL COMMENT '管理员ID',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_warehouse_code`(`warehouse_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '仓库表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of warehouse
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
