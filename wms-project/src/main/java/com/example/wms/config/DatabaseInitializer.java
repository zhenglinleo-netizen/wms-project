package com.example.wms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    public ApplicationRunner initDatabase() {
        return args -> {
            // 创建user_notice表
            String createTableSql = "CREATE TABLE IF NOT EXISTS user_notice " +
                    "(id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID', " +
                    "user_id bigint(20) NOT NULL COMMENT '用户ID', " +
                    "title varchar(200) NOT NULL COMMENT '通知标题', " +
                    "content text NOT NULL COMMENT '通知内容', " +
                    "type varchar(50) NOT NULL COMMENT '通知类型：logistics-物流更新, activity-活动推送, expiry-过期提醒, system-系统通知', " +
                    "is_read tinyint(1) DEFAULT 0 COMMENT '是否已读：0-未读，1-已读', " +
                    "related_id bigint(20) DEFAULT NULL COMMENT '关联ID（如订单ID、活动ID等）', " +
                    "related_type varchar(50) DEFAULT NULL COMMENT '关联类型（如order、activity等）', " +
                    "priority tinyint(1) DEFAULT 0 COMMENT '优先级：0-普通，1-重要，2-紧急', " +
                    "create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', " +
                    "update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', " +
                    "PRIMARY KEY (id), " +
                    "KEY idx_user_id (user_id), " +
                    "KEY idx_is_read (is_read), " +
                    "KEY idx_create_time (create_time), " +
                    "KEY idx_type (type)) " +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户通知表';";

            try {
                jdbcTemplate.execute(createTableSql);
                System.out.println("user_notice表创建成功");
            } catch (Exception e) {
                System.err.println("创建user_notice表时出错：" + e.getMessage());
            }

            // 创建user_behavior表（用户行为记录表）
            String createUserBehaviorSql = "CREATE TABLE IF NOT EXISTS user_behavior " +
                    "(id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '行为ID', " +
                    "user_id bigint(20) NOT NULL COMMENT '用户ID', " +
                    "material_id bigint(20) NOT NULL COMMENT '辅料ID（对应product表）', " +
                    "behavior_type varchar(20) NOT NULL COMMENT '行为类型：browse-浏览，favorite-收藏，add_to_scheme-添加到方案，purchase-采购', " +
                    "weight decimal(3,2) DEFAULT 1.00 COMMENT '行为权重：浏览1.0，收藏2.0，添加到方案3.0，采购5.0', " +
                    "ip_address varchar(50) DEFAULT NULL COMMENT 'IP地址', " +
                    "user_agent varchar(500) DEFAULT NULL COMMENT '浏览器UA', " +
                    "create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT '行为发生时间', " +
                    "PRIMARY KEY (id), " +
                    "KEY idx_user_id (user_id), " +
                    "KEY idx_material_id (material_id), " +
                    "KEY idx_behavior_type (behavior_type), " +
                    "KEY idx_create_time (create_time), " +
                    "KEY idx_user_material (user_id, material_id)) " +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为记录表';";

            try {
                jdbcTemplate.execute(createUserBehaviorSql);
                System.out.println("user_behavior表创建成功");
            } catch (Exception e) {
                System.err.println("创建user_behavior表时出错：" + e.getMessage());
            }
        };
    }
}
