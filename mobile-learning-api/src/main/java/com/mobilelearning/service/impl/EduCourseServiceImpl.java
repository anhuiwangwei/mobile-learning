package com.mobilelearning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobilelearning.dto.CourseRequest;
import com.mobilelearning.entity.EduCourse;
import com.mobilelearning.mapper.EduCourseMapper;
import com.mobilelearning.service.EduCourseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    @Override
    public Page<EduCourse> getCourseList(Integer pageNum, Integer pageSize, String courseName, Long teacherId, Integer status) {
        Page<EduCourse> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EduCourse> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(courseName)) {
            wrapper.like(EduCourse::getCourseName, courseName);
        }
        if (teacherId != null) {
            wrapper.eq(EduCourse::getTeacherId, teacherId);
        }
        if (status != null) {
            wrapper.eq(EduCourse::getStatus, status);
        }
        wrapper.orderByDesc(EduCourse::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public void addCourse(CourseRequest request) {
        EduCourse course = new EduCourse();
        course.setCourseName(request.getCourseName());
        course.setCourseDesc(request.getCourseDesc());
        course.setCoverImage(request.getCoverImage());
        course.setCategoryId(request.getCategoryId());
        course.setTeacherId(request.getTeacherId());
        course.setDifficulty(request.getDifficulty());
        course.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        course.setViewCount(0);
        this.save(course);
    }

    @Override
    public void updateCourse(CourseRequest request) {
        EduCourse course = this.getById(request.getId());
        if (course != null) {
            course.setCourseName(request.getCourseName());
            course.setCourseDesc(request.getCourseDesc());
            course.setCoverImage(request.getCoverImage());
            course.setCategoryId(request.getCategoryId());
            course.setDifficulty(request.getDifficulty());
            course.setStatus(request.getStatus());
            this.updateById(course);
        }
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        EduCourse course = new EduCourse();
        course.setId(id);
        course.setStatus(status);
        this.updateById(course);
    }
}