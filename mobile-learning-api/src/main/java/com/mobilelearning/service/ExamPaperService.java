package com.mobilelearning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mobilelearning.entity.ExamPaper;

public interface ExamPaperService extends IService<ExamPaper> {
    Page<ExamPaper> getPaperList(Integer pageNum, Integer pageSize, String paperName, Integer status);
}