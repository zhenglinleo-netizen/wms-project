package com.example.wms.controller;

import com.example.wms.annotation.RequireAdmin;
import com.example.wms.common.Result;
import com.example.wms.entity.MaterialCategory;
import com.example.wms.mapper.MaterialCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private MaterialCategoryMapper categoryMapper;

    @GetMapping("/list")
    public Result<List<MaterialCategory>> list() {
        List<MaterialCategory> list = categoryMapper.selectAll();
        return Result.success(list);
    }

    @PostMapping
    @RequireAdmin
    public Result<String> add(@RequestBody MaterialCategory category) {
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            return Result.error("分类名称不能为空");
        }
        if (categoryMapper.selectByName(category.getCategoryName()) != null) {
            return Result.error("分类名称已存在");
        }
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        categoryMapper.insert(category);
        return Result.success("添加成功", null);
    }

    @PutMapping
    @RequireAdmin
    public Result<String> update(@RequestBody MaterialCategory category) {
        if (category.getId() == null) {
            return Result.error("ID不能为空");
        }
        MaterialCategory exist = categoryMapper.selectById(category.getId());
        if (exist == null) {
            return Result.error("分类不存在");
        }
        if (category.getCategoryName() != null && !category.getCategoryName().equals(exist.getCategoryName())) {
            if (categoryMapper.selectByName(category.getCategoryName()) != null) {
                return Result.error("分类名称已存在");
            }
        }
        categoryMapper.update(category);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    @RequireAdmin
    public Result<String> delete(@PathVariable Long id) {
        MaterialCategory exist = categoryMapper.selectById(id);
        if (exist == null) {
            return Result.error("分类不存在");
        }
        categoryMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
