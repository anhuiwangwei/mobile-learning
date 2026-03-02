package com.mobilelearning.dto;

import lombok.Data;

@Data
public class ChapterRequest {
    private Long id;
    private Long courseId;
    private String chapterName;
    private Integer chapterOrder;
}
