package com.example.wms.interceptor;

import com.example.wms.annotation.RequireAdmin;
import com.example.wms.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

/**
 * 权限验证拦截器
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

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

        // 如果没有需要管理员权限的注解，直接放行
        if (requireAdmin == null) {
            return true;
        }

        // 从 Session 中获取用户角色
        HttpSession session = request.getSession(false);
        if (session == null) {
            // 没有 Session，说明未登录
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter out = response.getWriter();
            Result<Object> result = Result.error(401, "未登录或登录已过期");
            ObjectMapper mapper = new ObjectMapper();
            out.write(mapper.writeValueAsString(result));
            out.flush();
            return false;
        }

        String role = (String) session.getAttribute("userRole");

        // 验证是否为管理员
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

        return true;
    }
}

