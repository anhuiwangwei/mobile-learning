package com.mobilelearning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("exam_paper")
public class ExamPaper implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String paperName;

    private Integer totalScore;

    private Integer passScore;

    private Integer duration;

    private Integer questionCount;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
