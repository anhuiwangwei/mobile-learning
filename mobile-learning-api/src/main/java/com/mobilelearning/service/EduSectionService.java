package com.mobilelearning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mobilelearning.dto.SectionRequest;
import com.mobilelearning.entity.EduSection;

import java.util.List;

public interface EduSectionService extends IService<EduSection> {
    List<EduSection> getByChapterId(Long chapterId);
    void addSection(SectionRequest request);
    void updateSection(SectionRequest request);
}