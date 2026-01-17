package com.example.wms.controller;

import com.example.wms.common.Result;
import com.example.wms.entity.SchemeItem;
import com.example.wms.service.SchemeItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 方案明细管理控制器
 */
@RestController
@RequestMapping("/scheme-item")
public class SchemeItemController {

    @Autowired
    private SchemeItemService schemeItemService;

    /**
     * 根据方案ID获取方案明细列表
     */
    @GetMapping("/list/{schemeId}")
    public Result<List<SchemeItem>> getSchemeItemListBySchemeId(@PathVariable Long schemeId) {
        List<SchemeItem> schemeItems = schemeItemService.getSchemeItemListBySchemeId(schemeId);
        return Result.success("获取方案明细列表成功", schemeItems);
    }

    /**
     * 根据ID获取方案明细
     */
    @GetMapping("/{id}")
    public Result<SchemeItem> getSchemeItemById(@PathVariable Long id) {
        SchemeItem schemeItem = schemeItemService.getSchemeItemById(id);
        return Result.success("获取方案明细成功", schemeItem);
    }

    /**
     * 新增方案明细
     */
    @PostMapping
    public Result<Integer> addSchemeItem(@RequestBody SchemeItem schemeItem) {
        int result = schemeItemService.addSchemeItem(schemeItem);
        return Result.success("新增方案明细成功", result);
    }

    /**
     * 更新方案明细
     */
    @PutMapping
    public Result<Integer> updateSchemeItem(@RequestBody SchemeItem schemeItem) {
        int result = schemeItemService.updateSchemeItem(schemeItem);
        return Result.success("更新方案明细成功", result);
    }

    /**
     * 删除方案明细
     */
    @DeleteMapping("/{id}")
    public Result<Integer> deleteSchemeItem(@PathVariable Long id) {
        int result = schemeItemService.deleteSchemeItem(id);
        return Result.success("删除方案明细成功", result);
    }

    /**
     * 根据方案ID删除所有方案明细
     */
    @DeleteMapping("/delete-by-scheme/{schemeId}")
    public Result<Integer> deleteSchemeItemsBySchemeId(@PathVariable Long schemeId) {
        int result = schemeItemService.deleteSchemeItemsBySchemeId(schemeId);
        return Result.success("删除方案所有明细成功", result);
    }
}