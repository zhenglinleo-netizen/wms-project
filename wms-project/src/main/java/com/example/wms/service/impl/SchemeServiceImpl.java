package com.example.wms.service.impl;

import com.example.wms.entity.Scheme;
import com.example.wms.mapper.SchemeMapper;
import com.example.wms.service.SchemeService;
import com.example.wms.service.SchemeItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 方案服务实现类
 */
@Service
public class SchemeServiceImpl implements SchemeService {

    @Autowired
    private SchemeMapper schemeMapper;
    
    @Autowired
    private SchemeItemService schemeItemService;

    @Override
    public List<Scheme> getSchemeListByProjectId(Long projectId) {
        return schemeMapper.getSchemeListByProjectId(projectId);
    }

    @Override
    public List<Scheme> getSchemeList(Map<String, Object> params) {
        return schemeMapper.getSchemeList(params);
    }

    @Override
    public Scheme getSchemeById(Long id) {
        return schemeMapper.getSchemeById(id);
    }

    @Override
    public int addScheme(Scheme scheme) {
        // 生成唯一的方案编码
        if (scheme.getSchemeCode() == null || scheme.getSchemeCode().isEmpty()) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            scheme.setSchemeCode("SCHEME_" + timestamp);
        }
        // 设置默认版本号为1
        if (scheme.getVersion() == null) {
            scheme.setVersion(1);
        }
        // 设置默认状态为待确定
        if (scheme.getStatus() == null || scheme.getStatus().isEmpty()) {
            scheme.setStatus("待确定");
        }
        // 设置默认创建者ID为1（管理员）
        if (scheme.getCreatorId() == null) {
            scheme.setCreatorId(1L);
        }
        return schemeMapper.addScheme(scheme);
    }

    @Override
    public int updateScheme(Scheme scheme) {
        // 只需要确保ID不为null
        if (scheme.getId() == null) {
            throw new IllegalArgumentException("方案ID不能为空");
        }
        return schemeMapper.updateScheme(scheme);
    }

    @Override
    public int deleteScheme(Long id) {
        // 先删除方案下的所有物料项
        schemeItemService.deleteSchemeItemsBySchemeId(id);
        // 再删除方案
        return schemeMapper.deleteScheme(id);
    }

    @Override
    public boolean checkSchemeNameExists(String schemeName, Long projectId, Long excludeId) {
        return schemeMapper.checkSchemeNameExists(schemeName, projectId, excludeId) > 0;
    }
    
    @Override
    public void deleteSchemesByProjectId(Long projectId) {
        // 先获取项目下的所有方案
        List<Scheme> schemes = getSchemeListByProjectId(projectId);
        // 逐个删除方案下的物料项
        for (Scheme scheme : schemes) {
            schemeItemService.deleteSchemeItemsBySchemeId(scheme.getId());
        }
        // 最后删除所有方案
        schemeMapper.deleteSchemesByProjectId(projectId);
    }
}