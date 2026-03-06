package com.example.wms.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private String company;
    private String role;
    private Integer status;
    private Integer isFirstLogin; // 1表示首次登录，0表示非首次登录
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}




