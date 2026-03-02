package com.mobilelearning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("edu_learning_progress")
public class EduLearningProgress implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long sectionId;

    private Integer progress;

    private Integer watchTime;

    private Integer currentPage;

    private Date readStartTime;

    private Integer isCompleted;

    private Date completeTime;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
