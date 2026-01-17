package com.example.wms.common;

import lombok.Data;

/**
 * JWT响应类
 */
@Data
public class JwtResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String realName;
    private String company;
    private String role;

    public JwtResponse(String token, Long userId, String username, String realName, String company, String role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.realName = realName;
        this.company = company;
        this.role = role;
    }
}




