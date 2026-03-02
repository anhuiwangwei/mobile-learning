package com.mobilelearning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("edu_section")
public class EduSection implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long chapterId;

    private String sectionName;

    private String sectionType;  // video, pdf, exam

    private String contentUrl;

    private Integer duration;

    private Integer pdfReadTime;

    private Integer isAllowSeek;

    private Integer isStepLearning;

    private Integer isFree;
    
    private Integer sort;
    
    private Integer status;
    
    private Long examId;  // 如果是考试类型，关联 exam_paper 表

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
