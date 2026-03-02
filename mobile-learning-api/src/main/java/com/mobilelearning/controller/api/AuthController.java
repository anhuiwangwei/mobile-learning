package com.mobilelearning.controller.api;

import com.mobilelearning.common.AuthContext;
import com.mobilelearning.common.Result;
import com.mobilelearning.dto.LoginRequest;
import com.mobilelearning.dto.LoginResponse;
import com.mobilelearning.dto.RegisterRequest;
import com.mobilelearning.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @PostMapping("/register")
    public Result<LoginResponse> register(@RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @GetMapping("/info")
    public Result<LoginResponse> getUserInfo() {
        LoginResponse response = new LoginResponse();
        response.setUserId(AuthContext.getUserId());
        response.setUsername(AuthContext.getUser().getUsername());
        response.setNickname(AuthContext.getUser().getNickname());
        response.setRealName(AuthContext.getUser().getRealName());
        response.setRole(AuthContext.getUser().getRole());
        return Result.success(response);
    }
}
