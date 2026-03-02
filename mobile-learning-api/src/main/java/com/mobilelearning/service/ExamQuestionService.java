package com.mobilelearning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mobilelearning.entity.ExamQuestion;

import java.util.List;

public interface ExamQuestionService extends IService<ExamQuestion> {
    List<ExamQuestion> getByPaperId(Long paperId);
}