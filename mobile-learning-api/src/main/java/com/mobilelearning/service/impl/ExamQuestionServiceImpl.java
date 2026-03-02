package com.mobilelearning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobilelearning.entity.ExamQuestion;
import com.mobilelearning.mapper.ExamQuestionMapper;
import com.mobilelearning.service.ExamQuestionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamQuestionServiceImpl extends ServiceImpl<ExamQuestionMapper, ExamQuestion> implements ExamQuestionService {

    @Override
    public List<ExamQuestion> getByPaperId(Long paperId) {
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getPaperId, paperId);
        wrapper.orderByAsc(ExamQuestion::getQuestionOrder);
        return this.list(wrapper);
    }
}