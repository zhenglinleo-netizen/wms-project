package com.example.wms.mapper;

import com.example.wms.entity.RequirementAudit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 需求审核记录Mapper
 */
@Mapper
public interface RequirementAuditMapper {
    
    /**
     * 插入需求审核记录
     */
    int insert(RequirementAudit requirementAudit);
    
    /**
     * 根据ID查询需求审核记录
     */
    RequirementAudit selectById(@Param("id") Long id);
    
    /**
     * 更新需求审核记录
     */
    int updateById(RequirementAudit requirementAudit);
    
    /**
     * 根据需求ID查询最新的需求审核记录
     */
    RequirementAudit selectLatestByRequirementId(@Param("requirementId") Long requirementId);
}
