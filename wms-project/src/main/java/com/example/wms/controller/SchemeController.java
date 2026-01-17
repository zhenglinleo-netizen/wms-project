package com.example.wms.controller;

import com.example.wms.common.Result;
import com.example.wms.entity.Scheme;
import com.example.wms.service.SchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 方案管理控制器
 */
@RestController
@RequestMapping("/scheme")
public class SchemeController {

    @Autowired
    private SchemeService schemeService;

    /**
     * 根据项目ID获取方案列表
     */
    @GetMapping("/list/{projectId}")
    public Result<List<Scheme>> getSchemeListByProjectId(@PathVariable Long projectId, HttpServletRequest request) {
        // 从请求属性中获取当前登录用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录或登录已过期");
        }
        List<Scheme> schemes = schemeService.getSchemeListByProjectId(projectId);
        return Result.success("获取方案列表成功", schemes);
    }

    /**
     * 获取方案列表
     */
    @GetMapping("/list")
    public Result<List<Scheme>> getSchemeList(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        // 从请求属性中获取当前登录用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录或登录已过期");
        }
        List<Scheme> schemes = schemeService.getSchemeList(params);
        return Result.success("获取方案列表成功", schemes);
    }

    /**
     * 根据ID获取方案详情
     */
    @GetMapping("/{id}")
    public Result<Scheme> getSchemeById(@PathVariable Long id, HttpServletRequest request) {
        // 从请求属性中获取当前登录用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录或登录已过期");
        }
        Scheme scheme = schemeService.getSchemeById(id);
        return Result.success("获取方案详情成功", scheme);
    }

    /**
     * 新增方案
     */
    @PostMapping
    public Result<Integer> addScheme(@RequestBody Scheme scheme, HttpServletRequest request) {
        // 从请求属性中获取当前用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录或登录已过期");
        }
        scheme.setCreatorId(userId);
        
        // 检查方案名称是否已存在
        if (schemeService.checkSchemeNameExists(scheme.getSchemeName(), scheme.getProjectId(), null)) {
            return Result.error("方案名称已存在");
        }
        
        int result = schemeService.addScheme(scheme);
        return Result.success("新增方案成功", result);
    }

    /**
     * 更新方案
     */
    @PutMapping
    public Result<Integer> updateScheme(@RequestBody Scheme scheme, HttpServletRequest request) {
        // 从请求属性中获取当前登录用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录或登录已过期");
        }
        
        // 检查方案名称是否已存在（排除当前方案）
        if (schemeService.checkSchemeNameExists(scheme.getSchemeName(), scheme.getProjectId(), scheme.getId())) {
            return Result.error("方案名称已存在");
        }
        
        int result = schemeService.updateScheme(scheme);
        return Result.success("更新方案成功", result);
    }

    /**
     * 删除方案
     */
    @DeleteMapping("/{id}")
    public Result<Integer> deleteScheme(@PathVariable Long id, HttpServletRequest request) {
        // 从请求属性中获取当前登录用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录或登录已过期");
        }
        int result = schemeService.deleteScheme(id);
        return Result.success("删除方案成功", result);
    }
}