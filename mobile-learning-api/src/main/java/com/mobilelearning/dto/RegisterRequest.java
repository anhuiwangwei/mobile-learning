package com.mobilelearning.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String phone;
    private String realName;
    private String password;
}
