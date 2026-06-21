package com.library.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.common.Result;
import com.library.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            writeError(response, 401, "未登录，请先登录");
            return false;
        }

        if (!jwtUtil.isValid(token)) {
            writeError(response, 401, "登录已过期，请重新登录");
            return false;
        }

        // 将 userId / role 存入 request 属性，Controller 可直接取
        request.setAttribute("userId", jwtUtil.getUserId(token));
        request.setAttribute("role", jwtUtil.getRole(token));
        request.setAttribute("username", jwtUtil.getUsername(token));
        return true;
    }

    private void writeError(HttpServletResponse response, int code, String msg) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(code);
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(code, msg)));
    }
}
