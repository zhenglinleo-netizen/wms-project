package com.example.wms.service.impl;

import com.example.wms.entity.SchemeItem;
import com.example.wms.mapper.SchemeItemMapper;
import com.example.wms.service.SchemeItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 方案明细服务实现类
 */
@Service
public class SchemeItemServiceImpl implements SchemeItemService {

    @Autowired
    private SchemeItemMapper schemeItemMapper;

    @Override
    public List<SchemeItem> getSchemeItemListBySchemeId(Long schemeId) {
        return schemeItemMapper.getSchemeItemListBySchemeId(schemeId);
    }

    @Override
    public SchemeItem getSchemeItemById(Long id) {
        return schemeItemMapper.getSchemeItemById(id);
    }

    @Override
    public SchemeItem getSchemeItemBySchemeIdAndMaterialId(Long schemeId, Long materialId) {
        return schemeItemMapper.getSchemeItemBySchemeIdAndMaterialId(schemeId, materialId);
    }

    @Override
    public int addSchemeItem(SchemeItem schemeItem) {
        // 检查是否已存在相同的scheme_id和material_id的记录
        SchemeItem existingItem = schemeItemMapper.getSchemeItemBySchemeIdAndMaterialId(schemeItem.getSchemeId(), schemeItem.getMaterialId());
        if (existingItem != null) {
            // 如果已存在，增加数量
            existingItem.setQuantity(existingItem.getQuantity() + schemeItem.getQuantity());
            return schemeItemMapper.updateSchemeItem(existingItem);
        } else {
            // 如果不存在，创建新记录
            return schemeItemMapper.addSchemeItem(schemeItem);
        }
    }

    @Override
    public int updateSchemeItem(SchemeItem schemeItem) {
        return schemeItemMapper.updateSchemeItem(schemeItem);
    }

    @Override
    public int deleteSchemeItem(Long id) {
        return schemeItemMapper.deleteSchemeItem(id);
    }

    @Override
    public int deleteSchemeItemsBySchemeId(Long schemeId) {
        return schemeItemMapper.deleteSchemeItemsBySchemeId(schemeId);
    }
}