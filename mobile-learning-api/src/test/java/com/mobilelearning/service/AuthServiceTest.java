package com.mobilelearning.service;

import com.mobilelearning.dto.LoginRequest;
import com.mobilelearning.dto.LoginResponse;
import com.mobilelearning.dto.RegisterRequest;
import com.mobilelearning.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    public void testLogin() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");
        LoginResponse response = authService.login(request);
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("admin", response.getRole());
    }

    @Test
    public void testLoginFailure() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrongpassword");
        assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    public void testRegister() {
        RegisterRequest request = new RegisterRequest();
        request.setPhone("13800138001");
        request.setRealName("测试用户 2");
        request.setPassword("123456");
        LoginResponse response = authService.register(request);
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("user", response.getRole());
    }
}