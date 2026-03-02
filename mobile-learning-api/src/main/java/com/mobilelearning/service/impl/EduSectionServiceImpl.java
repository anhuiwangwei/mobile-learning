package com.mobilelearning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobilelearning.dto.SectionRequest;
import com.mobilelearning.entity.EduSection;
import com.mobilelearning.mapper.EduSectionMapper;
import com.mobilelearning.service.EduSectionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EduSectionServiceImpl extends ServiceImpl<EduSectionMapper, EduSection> implements EduSectionService {

    @Override
    public List<EduSection> getByChapterId(Long chapterId) {
        LambdaQueryWrapper<EduSection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduSection::getChapterId, chapterId);
        wrapper.orderByAsc(EduSection::getSort);
        return this.list(wrapper);
    }

    @Override
    public void addSection(SectionRequest request) {
        EduSection section = new EduSection();
        section.setChapterId(request.getChapterId());
        section.setSectionName(request.getSectionName());
        section.setSectionType(request.getSectionType());
        section.setContentUrl(request.getContentUrl());
        section.setDuration(request.getDuration());
        section.setPdfReadTime(request.getPdfReadTime() != null ? request.getPdfReadTime() : 300);
        section.setIsAllowSeek(request.getIsAllowSeek() != null ? request.getIsAllowSeek() : 1);
        section.setIsStepLearning(request.getIsStepLearning() != null ? request.getIsStepLearning() : 0);
        section.setIsFree(request.getIsFree() != null ? request.getIsFree() : 0);
        section.setSort(request.getSort());
        section.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        this.save(section);
    }

    @Override
    public void updateSection(SectionRequest request) {
        EduSection section = this.getById(request.getId());
        if (section != null) {
            section.setSectionName(request.getSectionName());
            section.setSectionType(request.getSectionType());
            section.setContentUrl(request.getContentUrl());
            section.setDuration(request.getDuration());
            section.setPdfReadTime(request.getPdfReadTime());
            section.setIsAllowSeek(request.getIsAllowSeek());
            section.setIsStepLearning(request.getIsStepLearning());
            section.setIsFree(request.getIsFree());
            section.setSort(request.getSort());
            section.setStatus(request.getStatus());
            this.updateById(section);
        }
    }
}