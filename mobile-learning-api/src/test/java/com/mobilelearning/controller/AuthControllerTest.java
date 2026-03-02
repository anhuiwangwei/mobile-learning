package com.mobilelearning.controller;

import com.mobilelearning.common.Result;
import com.mobilelearning.controller.api.AuthController;
import com.mobilelearning.dto.LoginRequest;
import com.mobilelearning.dto.LoginResponse;
import com.mobilelearning.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @Test
    public void testLogin() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");
        Result<LoginResponse> result = authController.login(request);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertNotNull(result.getData().getToken());
    }

    @Test
    public void testGetUserInfo() {
        Result<LoginResponse> result = authController.getUserInfo();
        assertNotNull(result);
    }
}