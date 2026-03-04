package com.mobilelearning.dto;

import lombok.Data;

@Data
public class SectionRequest {
    private Long id;
    private Long chapterId;
    private String sectionName;
    private String sectionType;  // video, pdf, exam
    private String contentUrl;
    private Integer duration;
    private Integer pdfReadTime;
    private Integer isAllowSeek;
    private Integer isNoDrag;
    private Integer isStepLearning;
    private Integer isFree;
    private Integer sort;
    private Integer status;
    
    // 如果是考试类型，需要 examId
    private Long examId;
}
