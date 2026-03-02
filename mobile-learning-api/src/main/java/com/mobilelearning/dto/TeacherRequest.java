package com.mobilelearning.dto;

import lombok.Data;

@Data
public class TeacherRequest {
    private Long id;
    private String realName;
    private String phone;
    private String teacherNo;
    private String avatar;
    private String intro;
}
