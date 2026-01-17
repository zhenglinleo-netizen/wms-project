package com.example.wms.mapper;

import com.example.wms.entity.Project;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 项目Mapper接口
 */
@Mapper
public interface ProjectMapper {
    /**
     * 获取项目列表
     */
    List<Project> getProjectList(@Param("params") Map<String, Object> params);

    /**
     * 根据ID获取项目详情
     */
    Project getProjectById(@Param("id") Long id, @Param("userId") Long userId);

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
    int deleteProject(@Param("id") Long id);

    /**
     * 检查项目名称是否已存在
     */
    int checkProjectNameExists(@Param("projectName") String projectName, @Param("ownerId") Long ownerId, @Param("excludeId") Long excludeId);
}