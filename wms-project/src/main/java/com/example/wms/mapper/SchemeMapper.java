package com.example.wms.mapper;

import com.example.wms.entity.Scheme;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 方案Mapper接口
 */
@Mapper
public interface SchemeMapper {
    /**
     * 根据项目ID获取方案列表
     */
    List<Scheme> getSchemeListByProjectId(@Param("projectId") Long projectId);

    /**
     * 获取方案列表
     */
    List<Scheme> getSchemeList(@Param("params") Map<String, Object> params);

    /**
     * 根据ID获取方案详情
     */
    Scheme getSchemeById(@Param("id") Long id);

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
    int deleteScheme(@Param("id") Long id);
    
    /**
     * 检查方案名称是否已存在
     */
    int checkSchemeNameExists(@Param("schemeName") String schemeName, @Param("projectId") Long projectId, @Param("excludeId") Long excludeId);
    
    /**
     * 根据项目ID删除所有方案
     */
    int deleteSchemesByProjectId(@Param("projectId") Long projectId);
}