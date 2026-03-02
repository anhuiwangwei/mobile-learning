package com.mobilelearning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobilelearning.dto.ChapterRequest;
import com.mobilelearning.entity.EduChapter;
import com.mobilelearning.mapper.EduChapterMapper;
import com.mobilelearning.service.EduChapterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EduChapterServiceImpl extends ServiceImpl<EduChapterMapper, EduChapter> implements EduChapterService {

    @Override
    public List<EduChapter> getByCourseId(Long courseId) {
        LambdaQueryWrapper<EduChapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduChapter::getCourseId, courseId);
        wrapper.orderByAsc(EduChapter::getChapterOrder);
        return this.list(wrapper);
    }

    @Override
    public void addChapter(ChapterRequest request) {
        EduChapter chapter = new EduChapter();
        chapter.setCourseId(request.getCourseId());
        chapter.setChapterName(request.getChapterName());
        chapter.setChapterOrder(request.getChapterOrder());
        this.save(chapter);
    }

    @Override
    public void updateChapter(ChapterRequest request) {
        EduChapter chapter = this.getById(request.getId());
        if (chapter != null) {
            chapter.setChapterName(request.getChapterName());
            chapter.setChapterOrder(request.getChapterOrder());
            this.updateById(chapter);
        }
    }
}