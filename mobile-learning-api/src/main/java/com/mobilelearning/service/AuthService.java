package com.mobilelearning.service;

import com.mobilelearning.dto.LoginRequest;
import com.mobilelearning.dto.LoginResponse;
import com.mobilelearning.dto.RegisterRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse register(RegisterRequest request);
    void logout();
}
