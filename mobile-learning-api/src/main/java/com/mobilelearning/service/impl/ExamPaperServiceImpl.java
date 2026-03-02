package com.mobilelearning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobilelearning.entity.ExamPaper;
import com.mobilelearning.mapper.ExamPaperMapper;
import com.mobilelearning.service.ExamPaperService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ExamPaperServiceImpl extends ServiceImpl<ExamPaperMapper, ExamPaper> implements ExamPaperService {

    @Override
    public Page<ExamPaper> getPaperList(Integer pageNum, Integer pageSize, String paperName, Integer status) {
        Page<ExamPaper> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ExamPaper> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(paperName)) {
            wrapper.like(ExamPaper::getPaperName, paperName);
        }
        if (status != null) {
            wrapper.eq(ExamPaper::getStatus, status);
        }
        wrapper.orderByDesc(ExamPaper::getCreateTime);
        return this.page(page, wrapper);
    }
}