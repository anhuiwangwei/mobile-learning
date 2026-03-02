package com.mobilelearning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("edu_chapter_exam")
public class EduChapterExam implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long chapterId;

    private Long examId;

    private Integer examOrder;

    private Integer isRequired;

    private Integer passScore;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
