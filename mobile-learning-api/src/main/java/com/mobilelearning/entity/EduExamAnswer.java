package com.mobilelearning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("edu_exam_answer")
public class EduExamAnswer implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long recordId;

    private Long questionId;

    private String userAnswer;

    private Integer isCorrect;

    private Integer score;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
