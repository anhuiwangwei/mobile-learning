package com.mobilelearning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mobilelearning.entity.EduLearningProgress;

import java.util.Map;

public interface EduLearningProgressService extends IService<EduLearningProgress> {
    EduLearningProgress getByUserAndSection(Long userId, Long sectionId);
    void updateVideoProgress(Long userId, Long sectionId, Integer progress, Integer watchTime);
    void updatePdfProgress(Long userId, Long sectionId, Integer currentPage, Boolean isStart, Integer pdfReadTime);
    Map<String, Object> getCourseProgress(Long userId, Long courseId);
}