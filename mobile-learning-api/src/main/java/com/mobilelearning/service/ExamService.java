package com.mobilelearning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mobilelearning.dto.PaperRequest;
import com.mobilelearning.dto.QuestionRequest;
import com.mobilelearning.entity.ExamPaper;
import com.mobilelearning.entity.ExamQuestion;

import java.util.List;

public interface ExamService extends IService<ExamPaper> {
    Page<ExamPaper> getPaperList(Integer pageNum, Integer pageSize, String paperName);
    Long addPaper(PaperRequest request);
    void updatePaper(PaperRequest request);
    List<ExamQuestion> getQuestions(Long paperId);
    void addQuestion(QuestionRequest request);
    void updateQuestion(QuestionRequest request);
    void deleteQuestion(Long id);
}