package com.mobilelearning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobilelearning.dto.ChapterExamRequest;
import com.mobilelearning.entity.EduChapterExam;
import com.mobilelearning.mapper.EduChapterExamMapper;
import com.mobilelearning.service.EduChapterExamService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EduChapterExamServiceImpl extends ServiceImpl<EduChapterExamMapper, EduChapterExam> implements EduChapterExamService {

    @Override
    public List<EduChapterExam> getByChapterId(Long chapterId) {
        LambdaQueryWrapper<EduChapterExam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduChapterExam::getChapterId, chapterId);
        wrapper.orderByAsc(EduChapterExam::getExamOrder);
        return this.list(wrapper);
    }

    @Override
    public void bindExam(ChapterExamRequest request) {
        EduChapterExam chapterExam = new EduChapterExam();
        chapterExam.setChapterId(request.getChapterId());
        chapterExam.setExamId(request.getExamId());
        chapterExam.setExamOrder(request.getExamOrder() != null ? request.getExamOrder() : 1);
        chapterExam.setIsRequired(request.getIsRequired() != null ? request.getIsRequired() : 1);
        chapterExam.setPassScore(request.getPassScore() != null ? request.getPassScore() : 60);
        this.save(chapterExam);
    }

    @Override
    public void unbindExam(Long id) {
        this.removeById(id);
    }
}