package com.mobilelearning.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mobilelearning.common.AuthContext;
import com.mobilelearning.common.Result;
import com.mobilelearning.entity.*;
import com.mobilelearning.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/learning")
@RequiredArgsConstructor
public class LearningApiController {

    private final EduSectionMapper eduSectionMapper;
    private final EduLearningProgressMapper eduLearningProgressMapper;

    @PostMapping("/video/progress")
    public Result<Void> updateVideoProgress(@RequestParam Long sectionId, 
                                             @RequestParam Integer progress,
                                             @RequestParam Integer watchTime) {
        Long userId = AuthContext.getUserId();
        
        LambdaQueryWrapper<EduLearningProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduLearningProgress::getUserId, userId);
        wrapper.eq(EduLearningProgress::getSectionId, sectionId);
        EduLearningProgress learningProgress = eduLearningProgressMapper.selectOne(wrapper);
        
        if (learningProgress == null) {
            learningProgress = new EduLearningProgress();
            learningProgress.setUserId(userId);
            learningProgress.setSectionId(sectionId);
            learningProgress.setProgress(0);
            learningProgress.setWatchTime(0);
            learningProgress.setIsCompleted(0);
            eduLearningProgressMapper.insert(learningProgress);
        }
        
        learningProgress.setProgress(progress);
        learningProgress.setWatchTime(watchTime);
        
        EduSection section = eduSectionMapper.selectById(sectionId);
        if (progress >= 100) {
            learningProgress.setIsCompleted(1);
            learningProgress.setCompleteTime(new Date());
        }
        
        eduLearningProgressMapper.updateById(learningProgress);
        return Result.success();
    }

    @PostMapping("/pdf/progress")
    public Result<Void> updatePdfProgress(@RequestParam Long sectionId,
                                          @RequestParam Integer currentPage,
                                          @RequestParam(required = false) Boolean isStart) {
        Long userId = AuthContext.getUserId();
        
        LambdaQueryWrapper<EduLearningProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduLearningProgress::getUserId, userId);
        wrapper.eq(EduLearningProgress::getSectionId, sectionId);
        EduLearningProgress learningProgress = eduLearningProgressMapper.selectOne(wrapper);
        
        EduSection section = eduSectionMapper.selectById(sectionId);
        
        if (learningProgress == null) {
            learningProgress = new EduLearningProgress();
            learningProgress.setUserId(userId);
            learningProgress.setSectionId(sectionId);
            learningProgress.setCurrentPage(0);
            learningProgress.setWatchTime(0);
            learningProgress.setIsCompleted(0);
            eduLearningProgressMapper.insert(learningProgress);
        }
        
        if (Boolean.TRUE.equals(isStart)) {
            learningProgress.setReadStartTime(new Date());
        }
        
        learningProgress.setCurrentPage(currentPage);
        
        if (currentPage >= section.getDuration() && learningProgress.getReadStartTime() != null) {
            long seconds = (new Date().getTime() - learningProgress.getReadStartTime().getTime()) / 1000;
            learningProgress.setWatchTime((int) seconds);
            
            if (seconds >= section.getPdfReadTime()) {
                learningProgress.setIsCompleted(1);
                learningProgress.setCompleteTime(new Date());
            }
        }
        
        eduLearningProgressMapper.updateById(learningProgress);
        return Result.success();
    }

    @GetMapping("/section/{sectionId}")
    public Result<Map<String, Object>> getSectionStatus(@PathVariable Long sectionId) {
        Long userId = AuthContext.getUserId();
        
        LambdaQueryWrapper<EduLearningProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduLearningProgress::getUserId, userId);
        wrapper.eq(EduLearningProgress::getSectionId, sectionId);
        EduLearningProgress progress = eduLearningProgressMapper.selectOne(wrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("isCompleted", progress != null && progress.getIsCompleted() == 1);
        result.put("progress", progress != null ? progress.getProgress() : 0);
        result.put("currentPage", progress != null ? progress.getCurrentPage() : 0);
        
        return Result.success(result);
    }
}
