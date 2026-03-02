package com.mobilelearning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("edu_chapter")
public class EduChapter implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId;

    private String chapterName;

    private Integer chapterOrder;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
