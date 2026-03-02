package com.mobilelearning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mobilelearning.entity.EduCategory;

import java.util.List;

public interface EduCategoryService extends IService<EduCategory> {
    List<EduCategory> getList();
    List<EduCategory> getByParentId(Long parentId);
}