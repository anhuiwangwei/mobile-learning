package com.mobilelearning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("edu_course")
public class EduCourse implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String courseName;

    private String courseDesc;

    private String coverImage;

    private Long categoryId;

    private Long teacherId;

    private Integer difficulty;

    private Integer duration;

    private Integer status;

    private Integer viewCount;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
