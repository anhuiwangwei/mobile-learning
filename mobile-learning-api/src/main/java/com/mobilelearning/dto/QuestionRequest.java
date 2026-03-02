package com.mobilelearning.dto;

import lombok.Data;

@Data
public class QuestionRequest {
    private Long id;
    private Long paperId;
    private String questionType;
    private String questionContent;
    private String options;
    private String answer;
    private String analysis;
    private Integer difficulty;
    private Integer score;
    private Integer questionOrder;
}
