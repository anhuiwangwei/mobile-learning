package com.mobilelearning.dto;

import lombok.Data;

@Data
public class PaperRequest {
    private Long id;
    private String paperName;
    private Integer totalScore;
    private Integer passScore;
    private Integer duration;
    private Integer status;
}
