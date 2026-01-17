package com.example.wms.mapper;

import com.example.wms.entity.NegotiationAudit;
import org.apache.ibatis.annotations.Mapper;

/**
 * 议价审核记录Mapper
 */
@Mapper
public interface NegotiationAuditMapper {
    /**
     * 插入议价审核记录
     */
    int insert(NegotiationAudit audit);
    
    /**
     * 更新议价审核记录
     */
    int updateById(NegotiationAudit audit);
    
    /**
     * 根据ID查询议价审核记录
     */
    NegotiationAudit selectById(Long id);
    
    /**
     * 根据需求ID查询议价审核记录
     */
    NegotiationAudit selectByRequirementId(Long requirementId);
}
