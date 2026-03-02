package com.mobilelearning.controller.admin;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mobilelearning.common.Result;
import com.mobilelearning.dto.*;
import com.mobilelearning.entity.*;
import com.mobilelearning.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamPaperMapper examPaperMapper;
    private final ExamQuestionMapper examQuestionMapper;
    private final EduChapterExamMapper eduChapterExamMapper;

    @GetMapping("/paper/list")
    public Result<Page<ExamPaper>> getPaperList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String paperName,
            @RequestParam(required = false) Integer status) {
        
        Page<ExamPaper> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ExamPaper> wrapper = new LambdaQueryWrapper<>();
        if (paperName != null) {
            wrapper.like(ExamPaper::getPaperName, paperName);
        }
        if (status != null) {
            wrapper.eq(ExamPaper::getStatus, status);
        }
        wrapper.orderByDesc(ExamPaper::getCreateTime);
        return Result.success(examPaperMapper.selectPage(page, wrapper));
    }

    @GetMapping("/paper/{id}")
    public Result<ExamPaper> getPaper(@PathVariable Long id) {
        return Result.success(examPaperMapper.selectById(id));
    }

    @PostMapping("/paper")
    public Result<Long> addPaper(@RequestBody PaperRequest request) {
        ExamPaper paper = new ExamPaper();
        paper.setPaperName(request.getPaperName());
        paper.setTotalScore(request.getTotalScore() != null ? request.getTotalScore() : 100);
        paper.setPassScore(request.getPassScore() != null ? request.getPassScore() : 60);
        paper.setDuration(request.getDuration() != null ? request.getDuration() : 60);
        paper.setQuestionCount(0);
        paper.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        examPaperMapper.insert(paper);
        return Result.success(paper.getId());
    }

    @PutMapping("/paper")
    public Result<Void> updatePaper(@RequestBody PaperRequest request) {
        ExamPaper paper = examPaperMapper.selectById(request.getId());
        if (paper != null) {
            paper.setPaperName(request.getPaperName());
            paper.setTotalScore(request.getTotalScore());
            paper.setPassScore(request.getPassScore());
            paper.setDuration(request.getDuration());
            paper.setStatus(request.getStatus());
            examPaperMapper.updateById(paper);
        }
        return Result.success();
    }

    @DeleteMapping("/paper/{id}")
    public Result<Void> deletePaper(@PathVariable Long id) {
        examPaperMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/question/list")
    public Result<List<ExamQuestion>> getQuestionList(@RequestParam Long paperId) {
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getPaperId, paperId);
        wrapper.orderByAsc(ExamQuestion::getQuestionOrder);
        List<ExamQuestion> questions = examQuestionMapper.selectList(wrapper);
        for (ExamQuestion question : questions) {
            if (question.getOptions() != null) {
                question.setOptions(JSONArray.toJSONString(JSON.parse(question.getOptions())));
            }
        }
        return Result.success(questions);
    }

    @PostMapping("/question")
    public Result<Void> addQuestion(@RequestBody QuestionRequest request) {
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
        
        updatePaperQuestionCount(request.getPaperId());
        return Result.success();
    }

    @PutMapping("/question")
    public Result<Void> updateQuestion(@RequestBody QuestionRequest request) {
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
        return Result.success();
    }

    @DeleteMapping("/question/{id}")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        ExamQuestion question = examQuestionMapper.selectById(id);
        if (question != null) {
            Long paperId = question.getPaperId();
            examQuestionMapper.deleteById(id);
            updatePaperQuestionCount(paperId);
        }
        return Result.success();
    }

    @GetMapping("/chapter/{chapterId}/exams")
    public Result<List<EduChapterExam>> getChapterExams(@PathVariable Long chapterId) {
        LambdaQueryWrapper<EduChapterExam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduChapterExam::getChapterId, chapterId);
        wrapper.orderByAsc(EduChapterExam::getExamOrder);
        return Result.success(eduChapterExamMapper.selectList(wrapper));
    }

    @PostMapping("/chapter/exam")
    public Result<Void> bindChapterExam(@RequestBody ChapterExamRequest request) {
        EduChapterExam chapterExam = new EduChapterExam();
        chapterExam.setChapterId(request.getChapterId());
        chapterExam.setExamId(request.getExamId());
        chapterExam.setExamOrder(request.getExamOrder() != null ? request.getExamOrder() : 1);
        chapterExam.setIsRequired(request.getIsRequired() != null ? request.getIsRequired() : 1);
        chapterExam.setPassScore(request.getPassScore() != null ? request.getPassScore() : 60);
        eduChapterExamMapper.insert(chapterExam);
        return Result.success();
    }

    @DeleteMapping("/chapter/exam/{id}")
    public Result<Void> unbindChapterExam(@PathVariable Long id) {
        eduChapterExamMapper.deleteById(id);
        return Result.success();
    }

    private void updatePaperQuestionCount(Long paperId) {
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getPaperId, paperId);
        Long count = examQuestionMapper.selectCount(wrapper);
        ExamPaper paper = examPaperMapper.selectById(paperId);
        if (paper != null) {
            paper.setQuestionCount(count.intValue());
            examPaperMapper.updateById(paper);
        }
    }
}
