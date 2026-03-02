package com.mobilelearning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("edu_exam_record")
public class EduExamRecord implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long examId;

    private Long courseId;

    private Long chapterId;

    private Integer score;

    private Integer status;

    private Date startTime;

    private Date submitTime;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
