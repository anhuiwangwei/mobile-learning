package com.mobilelearning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mobilelearning.dto.ChapterRequest;
import com.mobilelearning.entity.EduChapter;

import java.util.List;

public interface EduChapterService extends IService<EduChapter> {
    List<EduChapter> getByCourseId(Long courseId);
    void addChapter(ChapterRequest request);
    void updateChapter(ChapterRequest request);
}