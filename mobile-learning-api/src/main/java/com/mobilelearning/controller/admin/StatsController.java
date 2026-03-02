package com.mobilelearning.controller.admin;

import com.mobilelearning.common.Result;
import com.mobilelearning.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
public class StatsController {

    private final SysUserMapper sysUserMapper;
    private final EduTeacherMapper eduTeacherMapper;
    private final EduCourseMapper eduCourseMapper;
    private final ExamPaperMapper examPaperMapper;
    private final EduExamRecordMapper eduExamRecordMapper;
    private final EduLearningProgressMapper eduLearningProgressMapper;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        Long userCount = sysUserMapper.selectCount(null);
        Long teacherCount = eduTeacherMapper.selectCount(null);
        Long courseCount = eduCourseMapper.selectCount(null);
        Long examCount = examPaperMapper.selectCount(null);
        
        stats.put("userCount", userCount);
        stats.put("teacherCount", teacherCount);
        stats.put("courseCount", courseCount);
        stats.put("examCount", examCount);
        
        return Result.success(stats);
    }

    @GetMapping("/course/{courseId}")
    public Result<Map<String, Object>> getCourseStats(@PathVariable Long courseId) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("viewCount", 0);
        stats.put("learningCount", 0);
        stats.put("completedCount", 0);
        
        return Result.success(stats);
    }
}