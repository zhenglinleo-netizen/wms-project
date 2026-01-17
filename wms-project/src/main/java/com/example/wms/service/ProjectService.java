package com.example.wms.service;

import com.example.wms.entity.Project;

import java.util.List;
import java.util.Map;

/**
 * 项目服务接口
 */
public interface ProjectService {
    /**
     * 获取项目列表
     */
    List<Project> getProjectList(Map<String, Object> params);

    /**
     * 根据ID获取项目详情
     */
    Project getProjectById(Long id, Long userId);

    /**
     * 新增项目
     */
    int addProject(Project project);

    /**
     * 更新项目
     */
    int updateProject(Project project);

    /**
     * 删除项目
     */
    int deleteProject(Long id);

    /**
     * 检查项目名称是否已存在
     */
    boolean checkProjectNameExists(String projectName, Long ownerId, Long excludeId);
}