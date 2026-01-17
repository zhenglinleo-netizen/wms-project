package com.example.wms.mapper;

import com.example.wms.entity.RequirementItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RequirementItemMapper {

    /**
     * 插入需求明细
     */
    int insert(RequirementItem requirementItem);

    /**
     * 批量插入需求明细
     */
    int batchInsert(@Param("items") List<RequirementItem> items);

    /**
     * 根据需求ID查询需求明细
     */
    List<RequirementItem> selectByRequirementId(@Param("requirementId") Long requirementId);

    /**
     * 根据需求ID删除需求明细
     */
    int deleteByRequirementId(@Param("requirementId") Long requirementId);
    
    /**
     * 根据需求ID和物料ID更新价格
     */
    int updatePriceByRequirementIdAndMaterialId(
            @Param("requirementId") Long requirementId,
            @Param("materialId") Long materialId,
            @Param("negotiatedPrice") Double negotiatedPrice);
}