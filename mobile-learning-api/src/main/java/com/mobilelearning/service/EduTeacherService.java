package com.mobilelearning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mobilelearning.dto.TeacherRequest;
import com.mobilelearning.entity.EduTeacher;

public interface EduTeacherService extends IService<EduTeacher> {
    Page<EduTeacher> getTeacherList(Integer pageNum, Integer pageSize, String teacherNo);
    void addTeacher(TeacherRequest request);
    void updateTeacher(TeacherRequest request);
    void deleteTeacher(Long id);
    EduTeacher getByUserId(Long userId);
}