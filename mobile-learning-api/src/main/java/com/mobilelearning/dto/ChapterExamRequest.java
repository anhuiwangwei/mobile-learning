package com.mobilelearning.dto;

import lombok.Data;

@Data
public class ChapterExamRequest {
    private Long id;
    private Long chapterId;
    private Long examId;
    private Integer examOrder;
    private Integer isRequired;
    private Integer passScore;
}
