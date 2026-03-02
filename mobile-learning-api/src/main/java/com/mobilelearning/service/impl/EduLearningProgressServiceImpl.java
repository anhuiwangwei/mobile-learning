package com.mobilelearning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobilelearning.entity.EduChapter;
import com.mobilelearning.entity.EduLearningProgress;
import com.mobilelearning.entity.EduSection;
import com.mobilelearning.mapper.EduChapterMapper;
import com.mobilelearning.mapper.EduLearningProgressMapper;
import com.mobilelearning.service.EduLearningProgressService;
import com.mobilelearning.service.EduSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EduLearningProgressServiceImpl extends ServiceImpl<EduLearningProgressMapper, EduLearningProgress> implements EduLearningProgressService {

    private final EduSectionService eduSectionService;
    private final EduChapterMapper eduChapterMapper;

    @Override
    public EduLearningProgress getByUserAndSection(Long userId, Long sectionId) {
        LambdaQueryWrapper<EduLearningProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduLearningProgress::getUserId, userId);
        wrapper.eq(EduLearningProgress::getSectionId, sectionId);
        return this.getOne(wrapper);
    }

    @Override
    public void updateVideoProgress(Long userId, Long sectionId, Integer progress, Integer watchTime) {
        EduLearningProgress learningProgress = getByUserAndSection(userId, sectionId);
        
        if (learningProgress == null) {
            learningProgress = new EduLearningProgress();
            learningProgress.setUserId(userId);
            learningProgress.setSectionId(sectionId);
            learningProgress.setProgress(0);
            learningProgress.setWatchTime(0);
            learningProgress.setIsCompleted(0);
            this.save(learningProgress);
        }
        
        learningProgress.setProgress(progress);
        learningProgress.setWatchTime(watchTime);
        
        if (progress >= 100) {
            learningProgress.setIsCompleted(1);
            learningProgress.setCompleteTime(new Date());
        }
        
        this.updateById(learningProgress);
    }

    @Override
    public void updatePdfProgress(Long userId, Long sectionId, Integer currentPage, Boolean isStart, Integer pdfReadTime) {
        EduLearningProgress learningProgress = getByUserAndSection(userId, sectionId);
        EduSection section = eduSectionService.getById(sectionId);
        
        if (learningProgress == null) {
            learningProgress = new EduLearningProgress();
            learningProgress.setUserId(userId);
            learningProgress.setSectionId(sectionId);
            learningProgress.setCurrentPage(0);
            learningProgress.setWatchTime(0);
            learningProgress.setIsCompleted(0);
            this.save(learningProgress);
        }
        
        if (Boolean.TRUE.equals(isStart)) {
            learningProgress.setReadStartTime(new Date());
        }
        
        learningProgress.setCurrentPage(currentPage);
        
        if (section != null && currentPage >= section.getDuration() && learningProgress.getReadStartTime() != null) {
            long seconds = (new Date().getTime() - learningProgress.getReadStartTime().getTime()) / 1000;
            learningProgress.setWatchTime((int) seconds);
            
            if (seconds >= pdfReadTime) {
                learningProgress.setIsCompleted(1);
                learningProgress.setCompleteTime(new Date());
            }
        }
        
        this.updateById(learningProgress);
    }

    @Override
    public Map<String, Object> getCourseProgress(Long userId, Long courseId) {
        Map<String, Object> progress = new HashMap<>();
        
        LambdaQueryWrapper<EduChapter> chapterWrapper = new LambdaQueryWrapper<>();
        chapterWrapper.eq(EduChapter::getCourseId, courseId);
        List<EduChapter> chapters = eduChapterMapper.selectList(chapterWrapper);
        
        int totalVideo = 0, completedVideo = 0;
        int totalPdf = 0, completedPdf = 0;
        
        for (EduChapter chapter : chapters) {
            LambdaQueryWrapper<EduSection> sectionWrapper = new LambdaQueryWrapper<>();
            sectionWrapper.eq(EduSection::getChapterId, chapter.getId());
            List<EduSection> sections = eduSectionService.list(sectionWrapper);
            
            for (EduSection section : sections) {
                if ("video".equals(section.getSectionType())) {
                    totalVideo++;
                    LambdaQueryWrapper<EduLearningProgress> progressWrapper = new LambdaQueryWrapper<>();
                    progressWrapper.eq(EduLearningProgress::getUserId, userId);
                    progressWrapper.eq(EduLearningProgress::getSectionId, section.getId());
                    progressWrapper.eq(EduLearningProgress::getIsCompleted, 1);
                    if (this.count(progressWrapper) > 0) {
                        completedVideo++;
                    }
                } else if ("pdf".equals(section.getSectionType())) {
                    totalPdf++;
                    LambdaQueryWrapper<EduLearningProgress> progressWrapper = new LambdaQueryWrapper<>();
                    progressWrapper.eq(EduLearningProgress::getUserId, userId);
                    progressWrapper.eq(EduLearningProgress::getSectionId, section.getId());
                    progressWrapper.eq(EduLearningProgress::getIsCompleted, 1);
                    if (this.count(progressWrapper) > 0) {
                        completedPdf++;
                    }
                }
            }
        }
        
        progress.put("totalVideo", totalVideo);
        progress.put("completedVideo", completedVideo);
        progress.put("totalPdf", totalPdf);
        progress.put("completedPdf", completedPdf);
        
        int total = totalVideo + totalPdf;
        int completed = completedVideo + completedPdf;
        progress.put("total", total);
        progress.put("completed", completed);
        progress.put("percentage", total > 0 ? (completed * 100 / total) : 0);
        
        return progress;
    }
}