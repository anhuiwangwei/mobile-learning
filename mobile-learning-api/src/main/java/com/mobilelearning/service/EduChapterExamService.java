package com.mobilelearning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mobilelearning.dto.ChapterExamRequest;
import com.mobilelearning.entity.EduChapterExam;

import java.util.List;

public interface EduChapterExamService extends IService<EduChapterExam> {
    List<EduChapterExam> getByChapterId(Long chapterId);
    void bindExam(ChapterExamRequest request);
    void unbindExam(Long id);
}