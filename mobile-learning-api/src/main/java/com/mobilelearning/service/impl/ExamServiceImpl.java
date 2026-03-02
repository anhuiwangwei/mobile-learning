package com.mobilelearning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobilelearning.dto.PaperRequest;
import com.mobilelearning.dto.QuestionRequest;
import com.mobilelearning.entity.ExamPaper;
import com.mobilelearning.entity.ExamQuestion;
import com.mobilelearning.mapper.ExamPaperMapper;
import com.mobilelearning.mapper.ExamQuestionMapper;
import com.mobilelearning.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl extends ServiceImpl<ExamPaperMapper, ExamPaper> implements ExamService {

    private final ExamQuestionMapper examQuestionMapper;

    @Override
    public Page<ExamPaper> getPaperList(Integer pageNum, Integer pageSize, String paperName) {
        Page<ExamPaper> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ExamPaper> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(paperName)) {
            wrapper.like(ExamPaper::getPaperName, paperName);
        }
        wrapper.orderByDesc(ExamPaper::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public Long addPaper(PaperRequest request) {
        ExamPaper paper = new ExamPaper();
        paper.setPaperName(request.getPaperName());
        paper.setTotalScore(request.getTotalScore() != null ? request.getTotalScore() : 100);
        paper.setPassScore(request.getPassScore() != null ? request.getPassScore() : 60);
        paper.setDuration(request.getDuration() != null ? request.getDuration() : 60);
        paper.setQuestionCount(0);
        paper.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        this.save(paper);
        return paper.getId();
    }

    @Override
    public void updatePaper(PaperRequest request) {
        ExamPaper paper = this.getById(request.getId());
        if (paper != null) {
            paper.setPaperName(request.getPaperName());
            paper.setTotalScore(request.getTotalScore());
            paper.setPassScore(request.getPassScore());
            paper.setDuration(request.getDuration());
            paper.setStatus(request.getStatus());
            this.updateById(paper);
        }
    }

    @Override
    public List<ExamQuestion> getQuestions(Long paperId) {
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getPaperId, paperId);
        wrapper.orderByAsc(ExamQuestion::getQuestionOrder);
        return examQuestionMapper.selectList(wrapper);
    }

    @Override
    public void addQuestion(QuestionRequest request) {
        ExamQuestion question = new ExamQuestion();
        question.setPaperId(request.getPaperId());
        question.setQuestionType(request.getQuestionType());
        question.setQuestionContent(request.getQuestionContent());
        question.setOptions(request.getOptions());
        question.setAnswer(request.getAnswer());
        question.setAnalysis(request.getAnalysis());
        question.setDifficulty(request.getDifficulty() != null ? request.getDifficulty() : 1);
        question.setScore(request.getScore() != null ? request.getScore() : 5);
        question.setQuestionOrder(request.getQuestionOrder());
        examQuestionMapper.insert(question);
        updateQuestionCount(request.getPaperId());
    }

    @Override
    public void updateQuestion(QuestionRequest request) {
        ExamQuestion question = examQuestionMapper.selectById(request.getId());
        if (question != null) {
            question.setQuestionType(request.getQuestionType());
            question.setQuestionContent(request.getQuestionContent());
            question.setOptions(request.getOptions());
            question.setAnswer(request.getAnswer());
            question.setAnalysis(request.getAnalysis());
            question.setDifficulty(request.getDifficulty());
            question.setScore(request.getScore());
            question.setQuestionOrder(request.getQuestionOrder());
            examQuestionMapper.updateById(question);
        }
    }

    @Override
    public void deleteQuestion(Long id) {
        ExamQuestion question = examQuestionMapper.selectById(id);
        if (question != null) {
            Long paperId = question.getPaperId();
            examQuestionMapper.deleteById(id);
            updateQuestionCount(paperId);
        }
    }

    private void updateQuestionCount(Long paperId) {
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getPaperId, paperId);
        Long count = examQuestionMapper.selectCount(wrapper);
        ExamPaper paper = this.getById(paperId);
        if (paper != null) {
            paper.setQuestionCount(count.intValue());
            this.updateById(paper);
        }
    }
}