package com.example.wms.service;

import com.example.wms.entity.Scheme;

import java.util.List;
import java.util.Map;

/**
 * 方案服务接口
 */
public interface SchemeService {
    /**
     * 根据项目ID获取方案列表
     */
    List<Scheme> getSchemeListByProjectId(Long projectId);

    /**
     * 获取方案列表
     */
    List<Scheme> getSchemeList(Map<String, Object> params);

    /**
     * 根据ID获取方案详情
     */
    Scheme getSchemeById(Long id);

    /**
     * 新增方案
     */
    int addScheme(Scheme scheme);

    /**
     * 更新方案
     */
    int updateScheme(Scheme scheme);

    /**
     * 删除方案
     */
    int deleteScheme(Long id);
    
    /**
     * 检查方案名称是否已存在
     */
    boolean checkSchemeNameExists(String schemeName, Long projectId, Long excludeId);
    
    /**
     * 根据项目ID删除所有方案
     */
    void deleteSchemesByProjectId(Long projectId);
}