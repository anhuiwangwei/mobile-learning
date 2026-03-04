package com.mobilelearning.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mobilelearning.common.AuthContext;
import com.mobilelearning.common.Result;
import com.mobilelearning.dto.*;
import com.mobilelearning.entity.*;
import com.mobilelearning.mapper.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/course")
@RequiredArgsConstructor
public class CourseController {

    private final EduCourseMapper eduCourseMapper;
    private final EduChapterMapper eduChapterMapper;
    private final EduSectionMapper eduSectionMapper;
    private final EduTeacherMapper eduTeacherMapper;

    @GetMapping("/list")
    public Result<Page<EduCourse>> getCourseList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Integer status) {
        
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
        return Result.success(eduCourseMapper.selectPage(page, wrapper));
    }

    @GetMapping("/{id}")
    public Result<EduCourse> getCourse(@PathVariable Long id) {
        return Result.success(eduCourseMapper.selectById(id));
    }

    @PostMapping
    public Result<Void> addCourse(@RequestBody CourseRequest request) {
        Long userId = AuthContext.getUserId();
        String userRole = AuthContext.getRole();
        
        if (request.getTeacherId() == null) {
            if ("teacher".equals(userRole)) {
                LambdaQueryWrapper<EduTeacher> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(EduTeacher::getUserId, userId);
                wrapper.eq(EduTeacher::getIsDeleted, 0);
                EduTeacher teacher = eduTeacherMapper.selectOne(wrapper);
                if (teacher != null) {
                    request.setTeacherId(teacher.getId());
                } else {
                    return Result.error("教师用户未找到关联的教师信息");
                }
            } else {
                return Result.error("必须选择授课教师");
            }
        }
        
        EduCourse course = new EduCourse();
        course.setCourseName(request.getCourseName());
        course.setCourseDesc(request.getCourseDesc());
        course.setCoverImage(request.getCoverImage());
        course.setCategoryId(request.getCategoryId());
        course.setTeacherId(request.getTeacherId());
        course.setDifficulty(request.getDifficulty());
        course.setPageTurnTime(request.getPageTurnTime() != null ? request.getPageTurnTime() : 0);
        course.setIsOrderLearning(request.getIsOrderLearning() != null ? request.getIsOrderLearning() : 0);
        course.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        eduCourseMapper.insert(course);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateCourse(@RequestBody CourseRequest request) {
        EduCourse course = eduCourseMapper.selectById(request.getId());
        if (course != null) {
            course.setCourseName(request.getCourseName());
            course.setCourseDesc(request.getCourseDesc());
            course.setCoverImage(request.getCoverImage());
            course.setCategoryId(request.getCategoryId());
            course.setDifficulty(request.getDifficulty());
            course.setPageTurnTime(request.getPageTurnTime());
            course.setIsOrderLearning(request.getIsOrderLearning());
            course.setStatus(request.getStatus());
            eduCourseMapper.updateById(course);
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        eduCourseMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}/chapters")
    public Result<List<ChapterWithSectionsDTO>> getChapters(@PathVariable("id") Long courseId) {
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

    @PostMapping("/chapter")
    public Result<Void> addChapter(@RequestBody ChapterRequest request) {
        EduChapter chapter = new EduChapter();
        chapter.setCourseId(request.getCourseId());
        chapter.setChapterName(request.getChapterName());
        chapter.setChapterOrder(request.getChapterOrder());
        eduChapterMapper.insert(chapter);
        return Result.success();
    }

    @PutMapping("/chapter")
    public Result<Void> updateChapter(@RequestBody ChapterRequest request) {
        EduChapter chapter = eduChapterMapper.selectById(request.getId());
        if (chapter != null) {
            chapter.setChapterName(request.getChapterName());
            chapter.setChapterOrder(request.getChapterOrder());
            eduChapterMapper.updateById(chapter);
        }
        return Result.success();
    }

    @DeleteMapping("/chapter/{id}")
    public Result<Void> deleteChapter(@PathVariable Long id) {
        eduChapterMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/chapter/{chapterId}/sections")
    public Result<List<EduSection>> getSections(@PathVariable("chapterId") Long chapterId) {
        LambdaQueryWrapper<EduSection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduSection::getChapterId, chapterId);
        wrapper.orderByAsc(EduSection::getSort);
        return Result.success(eduSectionMapper.selectList(wrapper));
    }

    @PostMapping("/section")
    public Result<Void> addSection(@RequestBody SectionRequest request) {
        // 校验：章节必须至少有 1 个小节
        if (request.getChapterId() != null) {
            LambdaQueryWrapper<EduSection> countWrapper = new LambdaQueryWrapper<>();
            countWrapper.eq(EduSection::getChapterId, request.getChapterId());
            Long existingCount = eduSectionMapper.selectCount(countWrapper);
            // 如果是该章节的第一个小节，无需校验
        }
        
        // 校验：如果是考试类型，必须有 examId
        if ("exam".equals(request.getSectionType())) {
            if (request.getExamId() == null) {
                return Result.error("考试类型小节必须选择试卷");
            }
        } else if ("video".equals(request.getSectionType())) {
            if (request.getContentUrl() == null || request.getContentUrl().isEmpty()) {
                return Result.error("视频类型小节必须上传视频文件");
            }
        } else if ("pdf".equals(request.getSectionType())) {
            if (request.getContentUrl() == null || request.getContentUrl().isEmpty()) {
                return Result.error("PDF 类型小节必须上传 PDF 文件");
            }
        }
        
        EduSection section = new EduSection();
        section.setChapterId(request.getChapterId());
        section.setSectionName(request.getSectionName());
        section.setSectionType(request.getSectionType());
        section.setContentUrl(request.getContentUrl());
        section.setDuration(request.getDuration());
        section.setPdfReadTime(request.getPdfReadTime() != null ? request.getPdfReadTime() : 300);
        section.setIsAllowSeek(request.getIsAllowSeek() != null ? request.getIsAllowSeek() : 1);
        section.setIsNoDrag(request.getIsNoDrag() != null ? request.getIsNoDrag() : 0);
        section.setIsStepLearning(request.getIsStepLearning() != null ? request.getIsStepLearning() : 0);
        section.setIsFree(request.getIsFree() != null ? request.getIsFree() : 0);
        section.setSort(request.getSort());
        section.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        section.setExamId(request.getExamId());
        eduSectionMapper.insert(section);
        return Result.success();
    }

    @PutMapping("/section")
    public Result<Void> updateSection(@RequestBody SectionRequest request) {
        EduSection section = eduSectionMapper.selectById(request.getId());
        if (section != null) {
            section.setSectionName(request.getSectionName());
            section.setSectionType(request.getSectionType());
            section.setContentUrl(request.getContentUrl());
            section.setDuration(request.getDuration());
            section.setPdfReadTime(request.getPdfReadTime());
            section.setIsAllowSeek(request.getIsAllowSeek());
            section.setIsNoDrag(request.getIsNoDrag());
            section.setIsStepLearning(request.getIsStepLearning());
            section.setIsFree(request.getIsFree());
            section.setSort(request.getSort());
            section.setStatus(request.getStatus());
            section.setExamId(request.getExamId());
            eduSectionMapper.updateById(section);
        }
        return Result.success();
    }

    @DeleteMapping("/section/{id}")
    public Result<Void> deleteSection(@PathVariable Long id) {
        eduSectionMapper.deleteById(id);
        return Result.success();
    }
}
