package com.mobilelearning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("exam_question")
public class ExamQuestion implements Serializable {

    @TableId(type = IdType.AUTO)
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

    private Integer isMultiple;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
