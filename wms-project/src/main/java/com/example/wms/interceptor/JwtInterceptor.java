package com.example.wms.interceptor;

import com.example.wms.annotation.RequireAdmin;
import com.example.wms.common.Result;
import com.example.wms.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

/**
 * JWT认证拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是方法处理器，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 检查方法或类上是否有 @RequireAdmin 注解
        RequireAdmin requireAdmin = handlerMethod.getMethodAnnotation(RequireAdmin.class);
        if (requireAdmin == null) {
            requireAdmin = handlerMethod.getBeanType().getAnnotation(RequireAdmin.class);
        }

        // 获取token
        String token = getTokenFromRequest(request);

        // 如果没有token，检查是否需要管理员权限
        if (token == null || token.isEmpty()) {
            if (requireAdmin != null) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter out = response.getWriter();
                Result<Object> result = Result.error(401, "未登录或登录已过期");
                ObjectMapper mapper = new ObjectMapper();
                out.write(mapper.writeValueAsString(result));
                out.flush();
                return false;
            }
            // 不需要权限的接口，允许访问
            return true;
        }

        // 验证token
        try {
            jwtUtil.getClaimsFromToken(token);
        } catch (Exception e) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter out = response.getWriter();
            Result<Object> result = Result.error(401, "token无效或已过期");
            ObjectMapper mapper = new ObjectMapper();
            out.write(mapper.writeValueAsString(result));
            out.flush();
            return false;
        }

        // 如果需要管理员权限，验证角色
        if (requireAdmin != null) {
            String role = jwtUtil.getRoleFromToken(token);
            if (role == null || !"admin".equals(role)) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                PrintWriter out = response.getWriter();
                Result<Object> result = Result.error(403, "权限不足，需要管理员权限");
                ObjectMapper mapper = new ObjectMapper();
                out.write(mapper.writeValueAsString(result));
                out.flush();
                return false;
            }
        }

        // 将用户信息存入request属性，供后续使用
        request.setAttribute("userId", jwtUtil.getUserIdFromToken(token));
        request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
        request.setAttribute("role", jwtUtil.getRoleFromToken(token));

        return true;
    }

    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}




