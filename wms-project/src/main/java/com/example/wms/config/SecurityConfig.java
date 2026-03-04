package com.example.wms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类
 * 禁用默认安全配置，使用自定义 JWT 拦截器
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（使用 JWT 不需要 CSRF 保护）
            .csrf().disable()
            // 禁用 Session（使用 JWT 无状态认证）
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // 禁用 HTTP Basic 认证（使用 JWT 认证）
            .httpBasic().disable()
            // 禁用表单登录（使用 JWT 认证）
            .formLogin().disable()
            // 禁用登出
            .logout().disable()
            // 所有请求都放行，由 JwtInterceptor 进行权限控制
            .authorizeRequests()
            .anyRequest().permitAll();
        
        return http.build();
    }
}
