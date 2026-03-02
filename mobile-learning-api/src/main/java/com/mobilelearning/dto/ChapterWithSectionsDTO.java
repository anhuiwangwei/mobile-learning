package com.mobilelearning.dto;

import com.mobilelearning.entity.EduSection;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ChapterWithSectionsDTO {
    private Long id;
    private Long courseId;
    private String chapterName;
    private Integer chapterOrder;
    private Date createTime;
    private List<EduSection> sections = new ArrayList<>();
}
