package com.mobilelearning.controller.admin;

import com.mobilelearning.common.Result;
import com.mobilelearning.entity.EduCategory;
import com.mobilelearning.service.EduCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class CategoryController {

    private final EduCategoryService eduCategoryService;

    @GetMapping("/list")
    public Result<List<EduCategory>> getList() {
        return Result.success(eduCategoryService.getList());
    }

    @GetMapping("/children/{parentId}")
    public Result<List<EduCategory>> getByParentId(@PathVariable Long parentId) {
        return Result.success(eduCategoryService.getByParentId(parentId));
    }

    @PostMapping
    public Result<Void> add(@RequestBody EduCategory category) {
        eduCategoryService.save(category);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody EduCategory category) {
        eduCategoryService.updateById(category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        eduCategoryService.removeById(id);
        return Result.success();
    }
}