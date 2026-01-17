package com.example.wms.service;

import com.example.wms.entity.SchemeItem;

import java.util.List;

/**
 * 方案明细服务接口
 */
public interface SchemeItemService {
    /**
     * 根据方案ID获取方案明细列表
     */
    List<SchemeItem> getSchemeItemListBySchemeId(Long schemeId);

    /**
     * 根据ID获取方案明细
     */
    SchemeItem getSchemeItemById(Long id);

    /**
     * 根据方案ID和辅料ID获取方案明细
     */
    SchemeItem getSchemeItemBySchemeIdAndMaterialId(Long schemeId, Long materialId);

    /**
     * 新增方案明细
     */
    int addSchemeItem(SchemeItem schemeItem);

    /**
     * 更新方案明细
     */
    int updateSchemeItem(SchemeItem schemeItem);

    /**
     * 删除方案明细
     */
    int deleteSchemeItem(Long id);

    /**
     * 根据方案ID删除所有方案明细
     */
    int deleteSchemeItemsBySchemeId(Long schemeId);
}