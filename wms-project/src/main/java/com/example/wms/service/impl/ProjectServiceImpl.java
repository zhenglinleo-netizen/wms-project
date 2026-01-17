package com.example.wms.service.impl;

import com.example.wms.entity.Project;
import com.example.wms.mapper.ProjectMapper;
import com.example.wms.service.ProjectService;
import com.example.wms.service.SchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 项目服务实现类
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;
    
    @Autowired
    private SchemeService schemeService;

    @Override
    public List<Project> getProjectList(Map<String, Object> params) {
        return projectMapper.getProjectList(params);
    }

    @Override
    public Project getProjectById(Long id, Long userId) {
        return projectMapper.getProjectById(id, userId);
    }

    @Override
    public int addProject(Project project) {
        // 生成唯一的项目编码
        if (project.getProjectCode() == null || project.getProjectCode().isEmpty()) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            project.setProjectCode("PROJECT_" + timestamp);
        }
        // 设置默认的所有者ID为1（管理员），如果未提供
        if (project.getOwnerId() == null) {
            project.setOwnerId(1L);
        }
        // 设置默认状态，如果未提供
        if (project.getStatus() == null || project.getStatus().isEmpty()) {
            project.setStatus("进行中");
        }
        return projectMapper.addProject(project);
    }

    @Override
    public int updateProject(Project project) {
        return projectMapper.updateProject(project);
    }

    @Override
    public int deleteProject(Long id) {
        // 先删除项目下的所有设计方案
        schemeService.deleteSchemesByProjectId(id);
        // 再删除项目
        return projectMapper.deleteProject(id);
    }

    @Override
    public boolean checkProjectNameExists(String projectName, Long ownerId, Long excludeId) {
        return projectMapper.checkProjectNameExists(projectName, ownerId, excludeId) > 0;
    }
}