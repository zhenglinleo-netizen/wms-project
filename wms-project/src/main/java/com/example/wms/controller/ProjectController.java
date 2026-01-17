package com.example.wms.controller;

import com.example.wms.common.Result;
import com.example.wms.entity.Project;
import com.example.wms.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 项目管理控制器
 */
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * 获取项目列表
     */
    @GetMapping("/list")
    public Result<List<Project>> getProjectList(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        // 从请求属性中获取当前登录用户ID，忽略前端传递的userId参数
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录或登录已过期");
        }
        params.put("userId", userId);
        List<Project> projects = projectService.getProjectList(params);
        return Result.success("获取项目列表成功", projects);
    }

    /**
     * 根据ID获取项目详情
     */
    @GetMapping("/{id}")
    public Result<Project> getProjectById(@PathVariable Long id, HttpServletRequest request) {
        // 从请求中获取当前登录用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录或登录已过期");
        }
        Project project = projectService.getProjectById(id, userId);
        return Result.success("获取项目详情成功", project);
    }

    /**
     * 新增项目
     */
    @PostMapping
    public Result<Integer> addProject(@RequestBody Project project, HttpServletRequest request) {
        // 从请求属性中获取当前用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录或登录已过期");
        }
        project.setOwnerId(userId);
        
        // 检查项目名称是否已存在
        if (projectService.checkProjectNameExists(project.getProjectName(), userId, null)) {
            return Result.error("项目名称已存在");
        }
        
        int result = projectService.addProject(project);
        return Result.success("新增项目成功", result);
    }

    /**
     * 更新项目
     */
    @PutMapping
    public Result<Integer> updateProject(@RequestBody Project project, HttpServletRequest request) {
        // 从请求属性中获取当前用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录或登录已过期");
        }
        
        // 检查项目名称是否已存在（排除当前项目）
        if (projectService.checkProjectNameExists(project.getProjectName(), userId, project.getId())) {
            return Result.error("项目名称已存在");
        }
        
        int result = projectService.updateProject(project);
        return Result.success("更新项目成功", result);
    }

    /**
     * 删除项目
     */
    @DeleteMapping("/{id}")
    public Result<Integer> deleteProject(@PathVariable Long id, HttpServletRequest request) {
        // 从请求属性中获取当前登录用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录或登录已过期");
        }
        int result = projectService.deleteProject(id);
        return Result.success("删除项目成功", result);
    }
}