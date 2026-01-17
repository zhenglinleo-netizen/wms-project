package com.example.wms.controller;

import com.example.wms.common.Result;
import com.example.wms.dto.RequirementCreateDTO;
import com.example.wms.entity.Requirement;
import com.example.wms.entity.RequirementItem;
import com.example.wms.service.RequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requirement")
public class RequirementController {

    @Autowired
    private RequirementService requirementService;

    /**
     * 获取需求列表
     */
    @GetMapping("/list")
    public Result<java.util.Map<String, Object>> getRequirementList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return requirementService.getRequirementList(keyword, status, priority, creatorId, page, pageSize);
    }

    /**
     * 获取需求详情
     */
    @GetMapping("/{id}")
    public Result<java.util.Map<String, Object>> getRequirementDetail(@PathVariable Long id) {
        return requirementService.getRequirementDetail(id);
    }

    /**
     * 创建需求
     */
    @PostMapping
    public Result createRequirement(@RequestBody RequirementCreateDTO requirementCreateDTO) {
        return requirementService.createRequirement(requirementCreateDTO);
    }

    /**
     * 从方案创建需求
     */
    @PostMapping("/from-scheme/{schemeId}")
    public Result<Requirement> createRequirementFromScheme(@PathVariable Integer schemeId) {
        return requirementService.createRequirementFromScheme(schemeId);
    }

    /**
     * 删除需求
     */
    @DeleteMapping("/{id}")
    public Result deleteRequirement(@PathVariable Long id) {
        return requirementService.deleteRequirement(id);
    }

    /**
     * 审核需求
     */
    @PostMapping("/audit")
    public Result auditRequirement(@RequestBody java.util.Map<String, Object> auditData) {
        Long auditId = Long.valueOf(auditData.get("auditId").toString());
        String status = auditData.get("status").toString();
        String rejectionReason = auditData.get("rejectionReason") != null ? auditData.get("rejectionReason").toString() : null;
        
        return requirementService.auditRequirement(auditId, status, rejectionReason);
    }
    
    /**
     * 提交需求审核
     */
    @PostMapping("/submit/{id}")
    public Result submitRequirementForAudit(@PathVariable Long id) {
        return requirementService.submitRequirementForAudit(id);
    }
    
    /**
     * 确定需求的货期和货款
     */
    @PostMapping("/confirm")
    public Result confirmRequirement(@RequestBody Requirement requirement) {
        return requirementService.confirmRequirement(requirement);
    }
    
    /**
     * 更新需求信息
     */
    @PutMapping
    public Result updateRequirement(@RequestBody Requirement requirement) {
        return requirementService.updateRequirement(requirement);
    }
    
    /**
     * 提交议价
     */
    @PostMapping("/negotiate")
    public Result submitNegotiation(@RequestBody java.util.Map<String, Object> negotiationData) {
        Long requirementId = Long.valueOf(negotiationData.get("requirementId").toString());
        java.util.List<java.util.Map<String, Object>> negotiatedItems = (java.util.List<java.util.Map<String, Object>>) negotiationData.get("negotiatedItems");
        Double totalNegotiatedAmount = Double.valueOf(negotiationData.get("totalNegotiatedAmount").toString());
        
        // 转换为实体类
        java.util.List<RequirementItem> items = new java.util.ArrayList<>();
        for (java.util.Map<String, Object> item : negotiatedItems) {
            RequirementItem requirementItem = new RequirementItem();
            requirementItem.setMaterialId(Long.valueOf(item.get("materialId").toString()));
            requirementItem.setNegotiatedPrice(Double.valueOf(item.get("negotiatedPrice").toString()));
            items.add(requirementItem);
        }
        
        return requirementService.submitNegotiation(requirementId, items, totalNegotiatedAmount);
    }
    
    /**
     * 审核议价
     */
    @PostMapping("/negotiate/audit")
    public Result auditNegotiation(@RequestBody java.util.Map<String, Object> auditData) {
        Long auditId = Long.valueOf(auditData.get("auditId").toString());
        String status = auditData.get("status").toString();
        String rejectionReason = auditData.get("rejectionReason") != null ? auditData.get("rejectionReason").toString() : null;
        
        return requirementService.auditNegotiation(auditId, status, rejectionReason);
    }
}