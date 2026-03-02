package com.mobilelearning.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mobilelearning.common.AuthContext;
import com.mobilelearning.common.Result;
import com.mobilelearning.dto.*;
import com.mobilelearning.entity.*;
import com.mobilelearning.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseApiController {

    private final EduCourseMapper eduCourseMapper;
    private final EduChapterMapper eduChapterMapper;
    private final EduSectionMapper eduSectionMapper;
    private final EduLearningProgressMapper eduLearningProgressMapper;

    @GetMapping("/list")
    public Result<List<EduCourse>> getCourseList(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<EduCourse> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(EduCourse::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(EduCourse::getStatus, status);
        } else {
            wrapper.eq(EduCourse::getStatus, 1);
        }
        wrapper.orderByDesc(EduCourse::getCreateTime);
        return Result.success(eduCourseMapper.selectList(wrapper));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getCourseDetail(@PathVariable Long id) {
        EduCourse course = eduCourseMapper.selectById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        
        course.setViewCount(course.getViewCount() + 1);
        eduCourseMapper.updateById(course);
        
        // 查询所有章节
        LambdaQueryWrapper<EduChapter> chapterWrapper = new LambdaQueryWrapper<>();
        chapterWrapper.eq(EduChapter::getCourseId, id);
        chapterWrapper.orderByAsc(EduChapter::getChapterOrder);
        List<EduChapter> chapters = eduChapterMapper.selectList(chapterWrapper);
        
        // 为每个章节查询小节
        List<ChapterWithSectionsDTO> chaptersWithSections = new ArrayList<>();
        for (EduChapter chapter : chapters) {
            ChapterWithSectionsDTO dto = new ChapterWithSectionsDTO();
            dto.setId(chapter.getId());
            dto.setCourseId(chapter.getCourseId());
            dto.setChapterName(chapter.getChapterName());
            dto.setChapterOrder(chapter.getChapterOrder());
            dto.setCreateTime(chapter.getCreateTime());
            
            // 查询该章节的小节
            LambdaQueryWrapper<EduSection> sectionWrapper = new LambdaQueryWrapper<>();
            sectionWrapper.eq(EduSection::getChapterId, chapter.getId());
            sectionWrapper.orderByAsc(EduSection::getSort);
            List<EduSection> sections = eduSectionMapper.selectList(sectionWrapper);
            dto.setSections(sections);
            
            chaptersWithSections.add(dto);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("course", course);
        result.put("chapters", chaptersWithSections);
        return Result.success(result);
    }

    @GetMapping("/{courseId}/chapters")
    public Result<List<ChapterWithSectionsDTO>> getChapters(@PathVariable Long courseId) {
        // 1. 查询所有章节
        LambdaQueryWrapper<EduChapter> chapterWrapper = new LambdaQueryWrapper<>();
        chapterWrapper.eq(EduChapter::getCourseId, courseId);
        chapterWrapper.orderByAsc(EduChapter::getChapterOrder);
        List<EduChapter> chapters = eduChapterMapper.selectList(chapterWrapper);
        
        // 2. 为每个章节查询小节
        List<ChapterWithSectionsDTO> result = new ArrayList<>();
        for (EduChapter chapter : chapters) {
            ChapterWithSectionsDTO dto = new ChapterWithSectionsDTO();
            dto.setId(chapter.getId());
            dto.setCourseId(chapter.getCourseId());
            dto.setChapterName(chapter.getChapterName());
            dto.setChapterOrder(chapter.getChapterOrder());
            dto.setCreateTime(chapter.getCreateTime());
            
            // 查询该章节的小节
            LambdaQueryWrapper<EduSection> sectionWrapper = new LambdaQueryWrapper<>();
            sectionWrapper.eq(EduSection::getChapterId, chapter.getId());
            sectionWrapper.orderByAsc(EduSection::getSort);
            List<EduSection> sections = eduSectionMapper.selectList(sectionWrapper);
            dto.setSections(sections);
            
            result.add(dto);
        }
        
        return Result.success(result);
    }

    @GetMapping("/section/{id}")
    public Result<EduSection> getSection(@PathVariable Long id) {
        return Result.success(eduSectionMapper.selectById(id));
    }

    @GetMapping("/{courseId}/progress")
    public Result<Map<String, Object>> getCourseProgress(@PathVariable Long courseId) {
        Long userId = AuthContext.getUserId();
        
        LambdaQueryWrapper<EduChapter> chapterWrapper = new LambdaQueryWrapper<>();
        chapterWrapper.eq(EduChapter::getCourseId, courseId);
        chapterWrapper.orderByAsc(EduChapter::getChapterOrder);
        List<EduChapter> chapters = eduChapterMapper.selectList(chapterWrapper);
        
        int totalVideo = 0, completedVideo = 0;
        int totalPdf = 0, completedPdf = 0;
        int totalExam = 0, completedExam = 0;
        
        for (EduChapter chapter : chapters) {
            LambdaQueryWrapper<EduSection> sectionWrapper = new LambdaQueryWrapper<>();
            sectionWrapper.eq(EduSection::getChapterId, chapter.getId());
            List<EduSection> sections = eduSectionMapper.selectList(sectionWrapper);
            
            for (EduSection section : sections) {
                if ("video".equals(section.getSectionType())) {
                    totalVideo++;
                    LambdaQueryWrapper<EduLearningProgress> progressWrapper = new LambdaQueryWrapper<>();
                    progressWrapper.eq(EduLearningProgress::getUserId, userId);
                    progressWrapper.eq(EduLearningProgress::getSectionId, section.getId());
                    progressWrapper.eq(EduLearningProgress::getIsCompleted, 1);
                    if (eduLearningProgressMapper.selectCount(progressWrapper) > 0) {
                        completedVideo++;
                    }
                } else if ("pdf".equals(section.getSectionType())) {
                    totalPdf++;
                    LambdaQueryWrapper<EduLearningProgress> progressWrapper = new LambdaQueryWrapper<>();
                    progressWrapper.eq(EduLearningProgress::getUserId, userId);
                    progressWrapper.eq(EduLearningProgress::getSectionId, section.getId());
                    progressWrapper.eq(EduLearningProgress::getIsCompleted, 1);
                    if (eduLearningProgressMapper.selectCount(progressWrapper) > 0) {
                        completedPdf++;
                    }
                }
            }
        }
        
        Map<String, Object> progress = new HashMap<>();
        progress.put("totalVideo", totalVideo);
        progress.put("completedVideo", completedVideo);
        progress.put("totalPdf", totalPdf);
        progress.put("completedPdf", completedPdf);
        progress.put("totalExam", totalExam);
        progress.put("completedExam", completedExam);
        
        int total = totalVideo + totalPdf + totalExam;
        int completed = completedVideo + completedPdf + completedExam;
        progress.put("total", total);
        progress.put("completed", completed);
        progress.put("percentage", total > 0 ? (completed * 100 / total) : 0);
        
        return Result.success(progress);
    }
}
