package com.mobilelearning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mobilelearning.dto.CourseRequest;
import com.mobilelearning.entity.EduCourse;

import java.util.List;

public interface EduCourseService extends IService<EduCourse> {
    Page<EduCourse> getCourseList(Integer pageNum, Integer pageSize, String courseName, Long teacherId, Integer status);
    void addCourse(CourseRequest request);
    void updateCourse(CourseRequest request);
    void updateStatus(Long id, Integer status);
}