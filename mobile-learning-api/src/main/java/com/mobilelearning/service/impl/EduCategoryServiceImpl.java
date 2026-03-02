package com.mobilelearning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobilelearning.entity.EduCategory;
import com.mobilelearning.mapper.EduCategoryMapper;
import com.mobilelearning.service.EduCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EduCategoryServiceImpl extends ServiceImpl<EduCategoryMapper, EduCategory> implements EduCategoryService {

    @Override
    public List<EduCategory> getList() {
        LambdaQueryWrapper<EduCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(EduCategory::getSort);
        return this.list(wrapper);
    }

    @Override
    public List<EduCategory> getByParentId(Long parentId) {
        LambdaQueryWrapper<EduCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduCategory::getParentId, parentId);
        wrapper.orderByAsc(EduCategory::getSort);
        return this.list(wrapper);
    }
}