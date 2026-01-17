package com.example.wms.mapper;

import com.example.wms.entity.SchemeItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 方案明细Mapper接口
 */
@Mapper
public interface SchemeItemMapper {
    /**
     * 根据方案ID获取方案明细列表
     */
    List<SchemeItem> getSchemeItemListBySchemeId(@Param("schemeId") Long schemeId);

    /**
     * 根据ID获取方案明细
     */
    SchemeItem getSchemeItemById(@Param("id") Long id);

    /**
     * 根据方案ID和辅料ID获取方案明细
     */
    SchemeItem getSchemeItemBySchemeIdAndMaterialId(@Param("schemeId") Long schemeId, @Param("materialId") Long materialId);

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
    int deleteSchemeItem(@Param("id") Long id);

    /**
     * 根据方案ID删除所有方案明细
     */
    int deleteSchemeItemsBySchemeId(@Param("schemeId") Long schemeId);
}