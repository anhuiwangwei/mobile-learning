package com.mobilelearning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("edu_teacher")
public class EduTeacher implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String teacherNo;

    private String avatar;

    private String intro;

    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
