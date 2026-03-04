package com.mobilelearning.dto;

import lombok.Data;

@Data
public class CourseRequest {
    private Long id;
    private String courseName;
    private String courseDesc;
    private String coverImage;
    private Long categoryId;
    private Long teacherId;
    private String teacherName;
    private Integer difficulty;
    private Integer pageTurnTime;
    private Integer isOrderLearning;
    private Integer status;
}
