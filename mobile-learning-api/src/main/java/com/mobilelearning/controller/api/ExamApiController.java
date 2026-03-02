package com.mobilelearning.controller.api;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mobilelearning.common.AuthContext;
import com.mobilelearning.common.Result;
import com.mobilelearning.dto.ExamSubmitRequest;
import com.mobilelearning.entity.*;
import com.mobilelearning.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamApiController {

    private final ExamPaperMapper examPaperMapper;
    private final ExamQuestionMapper examQuestionMapper;
    private final EduExamRecordMapper eduExamRecordMapper;
    private final EduExamAnswerMapper eduExamAnswerMapper;
    private final EduChapterExamMapper eduChapterExamMapper;

    @GetMapping("/list")
    public Result<List<ExamPaper>> getExamList() {
        LambdaQueryWrapper<ExamPaper> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamPaper::getStatus, 1);
        wrapper.orderByDesc(ExamPaper::getCreateTime);
        return Result.success(examPaperMapper.selectList(wrapper));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getExamDetail(@PathVariable Long id) {
        ExamPaper paper = examPaperMapper.selectById(id);
        if (paper == null) {
            return Result.error("试卷不存在");
        }
        
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getPaperId, id);
        wrapper.orderByAsc(ExamQuestion::getQuestionOrder);
        List<ExamQuestion> questions = examQuestionMapper.selectList(wrapper);
        
        for (ExamQuestion question : questions) {
            question.setAnswer(null);
            question.setAnalysis(null);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("paper", paper);
        result.put("questions", questions);
        return Result.success(result);
    }

    @PostMapping("/start")
    public Result<Map<String, Object>> startExam(@RequestParam Long examId,
                                                   @RequestParam(required = false) Long courseId,
                                                   @RequestParam(required = false) Long chapterId) {
        Long userId = AuthContext.getUserId();
        
        EduExamRecord record = new EduExamRecord();
        record.setUserId(userId);
        record.setExamId(examId);
        record.setCourseId(courseId);
        record.setChapterId(chapterId);
        record.setScore(0);
        record.setStatus(0);
        record.setStartTime(new Date());
        eduExamRecordMapper.insert(record);
        
        ExamPaper paper = examPaperMapper.selectById(examId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("recordId", record.getId());
        result.put("paper", paper);
        
        return Result.success(result);
    }

    @PostMapping("/submit")
    public Result<Map<String, Object>> submitExam(@RequestBody ExamSubmitRequest request) {
        Long userId = AuthContext.getUserId();
        
        EduExamRecord record = eduExamRecordMapper.selectById(request.getRecordId());
        if (record == null) {
            return Result.error("考试记录不存在");
        }
        
        ExamPaper paper = examPaperMapper.selectById(record.getExamId());
        
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getPaperId, record.getExamId());
        List<ExamQuestion> questions = examQuestionMapper.selectList(wrapper);
        Map<Long, ExamQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(ExamQuestion::getId, q -> q));
        
        int totalScore = 0;
        
        for (ExamSubmitRequest.AnswerItem answerItem : request.getAnswers()) {
            ExamQuestion question = questionMap.get(answerItem.getQuestionId());
            if (question == null) continue;
            
            EduExamAnswer answer = new EduExamAnswer();
            answer.setRecordId(request.getRecordId());
            answer.setQuestionId(answerItem.getQuestionId());
            answer.setUserAnswer(answerItem.getUserAnswer());
            
            boolean isCorrect = question.getAnswer().equalsIgnoreCase(answerItem.getUserAnswer());
            answer.setIsCorrect(isCorrect ? 1 : 0);
            answer.setScore(isCorrect ? question.getScore() : 0);
            
            totalScore += answer.getScore();
            eduExamAnswerMapper.insert(answer);
        }
        
        record.setScore(totalScore);
        record.setStatus(1);
        record.setSubmitTime(new Date());
        eduExamRecordMapper.updateById(record);
        
        Map<String, Object> result = new HashMap<>();
        result.put("score", totalScore);
        result.put("passScore", paper.getPassScore());
        result.put("isPass", totalScore >= paper.getPassScore());
        
        return Result.success(result);
    }

    @GetMapping("/record/{recordId}")
    public Result<Map<String, Object>> getExamRecord(@PathVariable Long recordId) {
        EduExamRecord record = eduExamRecordMapper.selectById(recordId);
        if (record == null) {
            return Result.error("考试记录不存在");
        }
        
        ExamPaper paper = examPaperMapper.selectById(record.getExamId());
        
        LambdaQueryWrapper<EduExamAnswer> answerWrapper = new LambdaQueryWrapper<>();
        answerWrapper.eq(EduExamAnswer::getRecordId, recordId);
        List<EduExamAnswer> answers = eduExamAnswerMapper.selectList(answerWrapper);
        
        List<Long> questionIds = answers.stream()
                .map(EduExamAnswer::getQuestionId)
                .collect(Collectors.toList());
        
        List<ExamQuestion> questions = new ArrayList<>();
        if (!questionIds.isEmpty()) {
            LambdaQueryWrapper<ExamQuestion> questionWrapper = new LambdaQueryWrapper<>();
            questionWrapper.in(ExamQuestion::getId, questionIds);
            questions = examQuestionMapper.selectList(questionWrapper);
        }
        
        Map<Long, ExamQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(ExamQuestion::getId, q -> q));
        
        List<Map<String, Object>> answerDetails = new ArrayList<>();
        for (EduExamAnswer answer : answers) {
            ExamQuestion question = questionMap.get(answer.getQuestionId());
            Map<String, Object> detail = new HashMap<>();
            detail.put("questionContent", question.getQuestionContent());
            detail.put("questionType", question.getQuestionType());
            detail.put("options", JSON.parse(question.getOptions()));
            detail.put("userAnswer", answer.getUserAnswer());
            detail.put("correctAnswer", question.getAnswer());
            detail.put("isCorrect", answer.getIsCorrect());
            detail.put("score", answer.getScore());
            detail.put("analysis", question.getAnalysis());
            answerDetails.add(detail);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("record", record);
        result.put("paper", paper);
        result.put("answers", answerDetails);
        
        return Result.success(result);
    }

    @GetMapping("/records")
    public Result<List<EduExamRecord>> getMyExamRecords() {
        Long userId = AuthContext.getUserId();
        LambdaQueryWrapper<EduExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduExamRecord::getUserId, userId);
        wrapper.orderByDesc(EduExamRecord::getCreateTime);
        return Result.success(eduExamRecordMapper.selectList(wrapper));
    }

    @GetMapping("/chapter/{chapterId}")
    public Result<List<ExamPaper>> getChapterExams(@PathVariable Long chapterId) {
        LambdaQueryWrapper<EduChapterExam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduChapterExam::getChapterId, chapterId);
        List<EduChapterExam> chapterExams = eduChapterExamMapper.selectList(wrapper);
        
        List<Long> examIds = chapterExams.stream()
                .map(EduChapterExam::getExamId)
                .collect(Collectors.toList());
        
        if (examIds.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        
        LambdaQueryWrapper<ExamPaper> paperWrapper = new LambdaQueryWrapper<>();
        paperWrapper.in(ExamPaper::getId, examIds);
        paperWrapper.eq(ExamPaper::getStatus, 1);
        return Result.success(examPaperMapper.selectList(paperWrapper));
    }
}
