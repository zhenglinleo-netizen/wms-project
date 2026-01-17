package com.example.wms.service;

import com.example.wms.common.Result;
import com.example.wms.dto.RequirementCreateDTO;
import com.example.wms.entity.Requirement;
import com.example.wms.entity.RequirementItem;

import java.util.List;

public interface RequirementService {

    /**
     * 获取需求列表
     */
    Result<java.util.Map<String, Object>> getRequirementList(String keyword, String status, String priority, Long creatorId, Integer page, Integer pageSize);

    /**
     * 获取需求详情
     */
    Result<java.util.Map<String, Object>> getRequirementDetail(Long id);

    /**
     * 创建需求
     */
    Result createRequirement(RequirementCreateDTO requirementCreateDTO);

    /**
     * 从方案创建需求
     */
    Result<Requirement> createRequirementFromScheme(Integer schemeId);

    /**
     * 删除需求
     */
    Result deleteRequirement(Long id);

    /**
     * 提交需求审核
     */
    Result submitRequirementForAudit(Long id);
    
    /**
     * 审核需求
     */
    Result auditRequirement(Long auditId, String status, String rejectionReason);
    
    /**
     * 确定需求的货期和货款
     */
    Result confirmRequirement(Requirement requirement);
    
    /**
     * 更新需求信息
     */
    Result updateRequirement(Requirement requirement);
    
    /**
     * 提交议价
     */
    Result submitNegotiation(Long requirementId, List<RequirementItem> negotiatedItems, Double totalNegotiatedAmount);
    
    /**
     * 审核议价
     */
    Result auditNegotiation(Long auditId, String status, String rejectionReason);
}