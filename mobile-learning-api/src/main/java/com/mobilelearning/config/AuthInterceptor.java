package com.mobilelearning.config;

import com.mobilelearning.common.AuthContext;
import com.mobilelearning.common.Result;
import com.mobilelearning.common.exception.BusinessException;
import com.mobilelearning.entity.SysUser;
import com.mobilelearning.mapper.SysUserMapper;
import com.mobilelearning.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final SysUserMapper sysUserMapper;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        
        if (StringUtils.isBlank(token)) {
            sendUnauthorizedResponse(response, "请先登录");
            return false;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            if (jwtUtil.isTokenExpired(token)) {
                sendUnauthorizedResponse(response, "登录已过期，请重新登录");
                return false;
            }

            Long userId = jwtUtil.getUserId(token);
            SysUser user = sysUserMapper.selectById(userId);
            
            if (user == null || user.getStatus() == 0) {
                sendUnauthorizedResponse(response, "用户不存在或已被禁用");
                return false;
            }

            AuthContext.setUser(user);
            return true;
        } catch (Exception e) {
            log.error("Token解析失败: {}", e.getMessage());
            sendUnauthorizedResponse(response, "Token无效，请重新登录");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AuthContext.clear();
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws Exception {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        Result<Object> result = Result.error(401, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
