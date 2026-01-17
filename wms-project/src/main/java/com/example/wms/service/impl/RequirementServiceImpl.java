package com.example.wms.service.impl;

import com.example.wms.common.Result;
import com.example.wms.dto.RequirementCreateDTO;
import com.example.wms.entity.Requirement;
import com.example.wms.entity.RequirementItem;
import com.example.wms.entity.Scheme;
import com.example.wms.entity.SchemeItem;
import com.example.wms.entity.Product;
import com.example.wms.entity.NegotiationAudit;
import com.example.wms.entity.RequirementAudit;
import com.example.wms.mapper.RequirementItemMapper;
import com.example.wms.mapper.RequirementMapper;
import com.example.wms.mapper.SchemeItemMapper;
import com.example.wms.mapper.SchemeMapper;
import com.example.wms.mapper.ProductMapper;
import com.example.wms.mapper.NegotiationAuditMapper;
import com.example.wms.mapper.RequirementAuditMapper;
import com.example.wms.service.RequirementService;
import com.example.wms.service.SchemeItemService;
import com.example.wms.service.SchemeService;
import com.example.wms.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RequirementServiceImpl implements RequirementService {

    @Autowired
    private RequirementMapper requirementMapper;

    @Autowired
    private RequirementItemMapper requirementItemMapper;

    @Autowired
    private SchemeMapper schemeMapper;

    @Autowired
    private SchemeItemMapper schemeItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private NegotiationAuditMapper negotiationAuditMapper;

    @Autowired
    private RequirementAuditMapper requirementAuditMapper;

    @Autowired
    private SchemeService schemeService;

    @Autowired
    private SchemeItemService schemeItemService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Override
    public Result<java.util.Map<String, Object>> getRequirementList(String keyword, String status, String priority, Long creatorId, Integer page, Integer pageSize) {
        // 计算分页偏移量
        int offset = (page - 1) * pageSize;
        
        // 查询需求列表
        List<Requirement> requirements = requirementMapper.selectRequirementList(keyword, status, priority, creatorId, offset, pageSize);
        
        // 查询总条数
        int total = requirementMapper.selectRequirementCount(keyword, status, priority, creatorId);
        
        // 构造分页数据结构，包含records和total字段
        java.util.Map<String, Object> pageData = new java.util.HashMap<>();
        pageData.put("records", requirements);
        pageData.put("total", total);
        
        // 构造返回结果
        Result<java.util.Map<String, Object>> result = Result.success(pageData);
        
        return result;
    }

    @Override
    public Result<java.util.Map<String, Object>> getRequirementDetail(Long id) {
        // 查询需求基本信息
        Requirement requirement = requirementMapper.selectById(id);
        if (requirement == null) {
            return Result.error("需求不存在");
        }
        
        // 查询需求明细
        List<RequirementItem> items = requirementItemMapper.selectByRequirementId(id);
        requirement.setItems(items);
        
        // 构造返回结果，包含需求信息和议价审核信息
        java.util.Map<String, Object> resultMap = new java.util.HashMap<>();
        resultMap.put("id", requirement.getId());
        resultMap.put("requirementCode", requirement.getRequirementCode());
        resultMap.put("status", requirement.getStatus());
        resultMap.put("purpose", requirement.getPurpose());
        resultMap.put("creatorName", requirement.getCreatorName());
        resultMap.put("createTime", requirement.getCreateTime());
        resultMap.put("auditorName", requirement.getAuditorName());
        resultMap.put("auditTime", requirement.getAuditTime());
        resultMap.put("auditRemark", requirement.getAuditRemark());
        resultMap.put("expectedDeliveryDays", requirement.getExpectedDeliveryDays());
        resultMap.put("expectedDeliveryDate", requirement.getExpectedDeliveryDate());
        resultMap.put("deadline", requirement.getDeadline());
        resultMap.put("totalPayment", requirement.getTotalPayment());
        resultMap.put("priority", requirement.getPriority());
        resultMap.put("items", items);
        
        // 查询议价审核记录
        NegotiationAudit negotiationAudit = negotiationAuditMapper.selectByRequirementId(id);
        if (negotiationAudit != null) {
            resultMap.put("negotiationAuditId", negotiationAudit.getId());
            resultMap.put("submittedBy", negotiationAudit.getSubmittedBy());
            resultMap.put("submittedAt", negotiationAudit.getSubmittedAt());
            resultMap.put("negotiatedTotal", negotiationAudit.getNegotiatedTotal());
            
            // 解析议价数据JSON，转换为列表
            try {
                com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                java.util.List<java.util.Map<String, Object>> negotiatedItems = objectMapper.readValue(
                    negotiationAudit.getNegotiationData(), 
                    new com.fasterxml.jackson.core.type.TypeReference<java.util.List<java.util.Map<String, Object>>>() {}
                );
                
                // 补充完整的辅料信息
                if (!negotiatedItems.isEmpty() && !items.isEmpty()) {
                    // 创建需求明细的映射，方便查找
                    java.util.Map<Long, RequirementItem> itemMap = new java.util.HashMap<>();
                    for (RequirementItem item : items) {
                        itemMap.put(item.getMaterialId(), item);
                    }
                    
                    // 为每个议价明细补充完整信息
                    for (java.util.Map<String, Object> negotiatedItem : negotiatedItems) {
                        Long materialId = ((Number) negotiatedItem.get("materialId")).longValue();
                        RequirementItem originalItem = itemMap.get(materialId);
                        if (originalItem != null) {
                            // 补充辅料编码、规格、用途、预计货期、单位等信息
                            negotiatedItem.put("materialCode", originalItem.getMaterialCode());
                            negotiatedItem.put("specification", originalItem.getSpecification());
                            negotiatedItem.put("purpose", originalItem.getPurpose());
                            negotiatedItem.put("expectedDeliveryDays", originalItem.getExpectedDeliveryDays());
                            negotiatedItem.put("unit", originalItem.getUnit());
                            
                            // 从originalItem获取原单价
                            Double originalPrice = originalItem.getPrice() != null ? originalItem.getPrice() : 0.0;
                            negotiatedItem.put("originalPrice", originalPrice);
                            
                            // 获取议价单价
                            Double negotiatedPrice = (Double) negotiatedItem.get("negotiatedPrice");
                            if (negotiatedPrice == null) {
                                negotiatedPrice = 0.0;
                            }
                            
                            // 计算差价
                            double difference = negotiatedPrice - originalPrice;
                            negotiatedItem.put("difference", difference);
                        }
                    }
                }
                
                resultMap.put("negotiatedItems", negotiatedItems);
            } catch (Exception e) {
                e.printStackTrace();
                resultMap.put("negotiatedItems", new java.util.ArrayList<>());
            }
        }
        
        return Result.success(resultMap);
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        org.springframework.web.context.request.ServletRequestAttributes attributes = (org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            Long userId = (Long) attributes.getRequest().getAttribute("userId");
            return userId;
        }
        return 1L; // 默认值，防止未登录情况
    }

    @Override
    @Transactional
    public Result<Requirement> createRequirement(RequirementCreateDTO requirementCreateDTO) {
        // 构造需求对象
        Requirement requirement = new Requirement();
        Integer projectId = requirementCreateDTO.getProjectId();
        String purpose = requirementCreateDTO.getPurpose();
        
        // 检查同一个项目内是否已存在同名的采购需求
        int count = requirementMapper.checkRequirementNameExists(Long.valueOf(projectId), purpose, null);
        if (count > 0) {
            System.out.println("同一个项目内已存在同名采购需求：" + purpose);
            return Result.error("同一个项目内已存在同名的采购需求");
        }
        
        // 获取当前登录用户ID
        Long currentUserId = getCurrentUserId();
        
        requirement.setProjectId(Long.valueOf(projectId));
        requirement.setSchemeId(Long.valueOf(requirementCreateDTO.getSchemeId()));
        requirement.setPurpose(purpose);
        requirement.setPriority(requirementCreateDTO.getPriority());
        requirement.setStatus("draft");
        requirement.setCreatorId(currentUserId); // 使用当前登录用户ID
        requirement.setCreateTime(new Date());
        requirement.setRemark(requirementCreateDTO.getRemark());
        
        // 生成需求单号
        requirement.setRequirementCode("REQ" + System.currentTimeMillis());
        
        // 保存需求
        requirementMapper.insert(requirement);
        
        return Result.success(requirement);
    }

    @Override
    @Transactional
    public Result<Requirement> createRequirementFromScheme(Integer schemeId) {
        try {
            System.out.println("开始从方案创建采购需求，schemeId: " + schemeId);
            
            // 查询方案信息
            Scheme scheme = schemeMapper.getSchemeById(Long.valueOf(schemeId));
            if (scheme == null) {
                System.out.println("方案不存在，schemeId: " + schemeId);
                return Result.error("方案不存在");
            }
            System.out.println("查询到方案信息: " + scheme.getSchemeName() + "，项目ID: " + scheme.getProjectId());
            
            // 查询方案明细
            List<SchemeItem> schemeItems = schemeItemMapper.getSchemeItemListBySchemeId(Long.valueOf(schemeId));
            if (schemeItems == null || schemeItems.isEmpty()) {
                System.out.println("方案中没有辅料，schemeId: " + schemeId);
                return Result.error("方案中没有辅料");
            }
            System.out.println("查询到方案明细数量: " + schemeItems.size());
            
            // 构造需求对象
        Requirement requirement = new Requirement();
        Long projectId = scheme.getProjectId();
        requirement.setProjectId(projectId);
        requirement.setSchemeId(Long.valueOf(schemeId));
        String purpose = "采购方案：" + scheme.getSchemeName();
        requirement.setPurpose(purpose);
        requirement.setPriority("normal");
        requirement.setStatus("draft");
        requirement.setCreatorId(getCurrentUserId()); // 使用当前登录用户ID
        requirement.setCreateTime(new Date());
        requirement.setRemark("从方案自动生成");
            
            // 检查同一个项目内是否已存在同名的采购需求
            int count = requirementMapper.checkRequirementNameExists(projectId, purpose, null);
            if (count > 0) {
                System.out.println("同一个项目内已存在同名采购需求：" + purpose);
                return Result.error("同一个项目内已存在同名的采购需求");
            }
            
            // 生成需求单号
            requirement.setRequirementCode("REQ" + System.currentTimeMillis());
            
            // 保存需求
            requirementMapper.insert(requirement);
            System.out.println("保存需求成功，需求ID: " + requirement.getId());
            
            // 保存需求明细
            List<RequirementItem> requirementItems = new ArrayList<>();
            for (SchemeItem schemeItem : schemeItems) {
                // 查询辅料信息
                Product product = productMapper.selectById(schemeItem.getMaterialId());
                System.out.println("查询到辅料信息: " + (product != null ? product.getProductName() : "未知辅料"));
                
                RequirementItem requirementItem = new RequirementItem();
                requirementItem.setRequirementId(requirement.getId());
                requirementItem.setMaterialId(schemeItem.getMaterialId());
                requirementItem.setMaterialName(product != null ? product.getProductName() : "未知辅料");
                requirementItem.setMaterialCode(product != null ? product.getProductCode() : "");
                requirementItem.setSpecification(product != null ? product.getSpecification() : "");
                requirementItem.setQuantity(schemeItem.getQuantity());
                requirementItem.setUnit(schemeItem.getUnit());
                requirementItem.setPurpose(schemeItem.getPurpose());
                requirementItem.setRemark(schemeItem.getRemark());
                requirementItems.add(requirementItem);
            }
            
            // 批量插入需求明细
            if (!requirementItems.isEmpty()) {
                int insertedCount = requirementItemMapper.batchInsert(requirementItems);
                System.out.println("批量插入需求明细成功，期望数量: " + requirementItems.size() + ", 实际插入数量: " + insertedCount);
            }
            
            return Result.success(requirement);
        } catch (Exception e) {
            System.out.println("从方案创建采购需求失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("从方案创建采购需求失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Void> deleteRequirement(Long id) {
        // 删除需求明细
        requirementItemMapper.deleteByRequirementId(id);
        
        // 删除需求
        requirementMapper.deleteById(id);
        
        return Result.success(null);
    }
    
    @Override
    @Transactional
    public Result<Void> submitRequirementForAudit(Long id) {
        try {
            // 获取需求详情
            Requirement requirement = requirementMapper.selectById(id);
            if (requirement == null) {
                return Result.error("需求不存在");
            }
            
            // 获取需求明细
            List<RequirementItem> requirementItems = requirementItemMapper.selectByRequirementId(id);
            if (requirementItems.isEmpty()) {
                return Result.error("需求无明细，无法提交审核");
            }
            
            // 计算需求总额
            double totalAmount = 0;
            for (RequirementItem item : requirementItems) {
                totalAmount += item.getQuantity() * (item.getNegotiatedPrice() > 0 ? item.getNegotiatedPrice() : item.getPrice());
            }
            
            // 更新需求状态为待审核
            Requirement updateRequirement = new Requirement();
            updateRequirement.setId(id);
            updateRequirement.setStatus("pending");
            requirementMapper.updateById(updateRequirement);
            
            // 保存需求审核记录
            RequirementAudit audit = new RequirementAudit();
            audit.setRequirementId(id);
            audit.setRequirementData(objectMapper.writeValueAsString(requirementItems));
            audit.setTotalAmount(totalAmount);
            audit.setSubmittedBy(getCurrentUserId());
            audit.setSubmittedAt(new Date());
            audit.setStatus("pending");
            requirementAuditMapper.insert(audit);
            
            return Result.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("提交审核失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Void> auditRequirement(Long requirementId, String status, String rejectionReason) {
        try {
            // 获取最新的需求审核记录
            RequirementAudit audit = requirementAuditMapper.selectLatestByRequirementId(requirementId);
            if (audit == null) {
                // 如果没有审核记录，直接更新需求状态
                Requirement requirement = new Requirement();
                requirement.setId(requirementId);
                
                if ("approved".equals(status)) {
                    // 审核通过，将状态设置为"待确定"
                    requirement.setStatus("confirming");
                } else if ("rejected".equals(status)) {
                    // 审核未通过，将状态设置为"已拒绝"
                    requirement.setStatus("rejected");
                }
                requirement.setAuditorId(getCurrentUserId()); // 使用当前登录用户ID
                requirement.setAuditTime(new Date());
                requirement.setAuditRemark(rejectionReason);
                
                requirementMapper.updateById(requirement);
                
                return Result.success(null);
            } else {
                // 更新审核记录
                audit.setStatus(status);
                audit.setAuditedBy(getCurrentUserId());
                audit.setAuditedAt(new Date());
                audit.setRejectionReason(rejectionReason);
                requirementAuditMapper.updateById(audit);
                
                // 更新需求状态
                Requirement requirement = new Requirement();
                requirement.setId(audit.getRequirementId());
                
                if ("approved".equals(status)) {
                    // 审核通过，将状态设置为"待确定"
                    requirement.setStatus("confirming");
                    requirement.setTotalPayment(audit.getTotalAmount());
                } else if ("rejected".equals(status)) {
                    // 审核未通过，将状态设置为"已拒绝"
                    requirement.setStatus("rejected");
                }
                requirement.setAuditorId(getCurrentUserId()); // 使用当前登录用户ID
                requirement.setAuditTime(new Date());
                requirement.setAuditRemark(rejectionReason);
                
                requirementMapper.updateById(requirement);
                
                return Result.success(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("需求审核失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<Void> confirmRequirement(Requirement requirement) {
        try {
            // 获取需求详情
            Requirement existingRequirement = requirementMapper.selectById(requirement.getId());
            if (existingRequirement == null) {
                return Result.error("需求不存在");
            }
            
            // 获取需求明细
            List<RequirementItem> requirementItems = requirementItemMapper.selectByRequirementId(requirement.getId());
            if (requirementItems.isEmpty()) {
                return Result.error("需求无明细，无法提交审核");
            }
            
            // 计算需求总额
            double totalAmount = 0;
            for (RequirementItem item : requirementItems) {
                totalAmount += item.getQuantity() * (item.getNegotiatedPrice() > 0 ? item.getNegotiatedPrice() : item.getPrice());
            }
            
            // 更新需求，保存deadline、expectedDeliveryDate和expectedDeliveryDays
            Requirement updateRequirement = new Requirement();
            updateRequirement.setId(requirement.getId());
            updateRequirement.setStatus("pending");
            updateRequirement.setDeadline(requirement.getDeadline());
            updateRequirement.setExpectedDeliveryDate(requirement.getExpectedDeliveryDate());
            updateRequirement.setExpectedDeliveryDays(requirement.getExpectedDeliveryDays());
            updateRequirement.setTotalPayment(totalAmount);
            
            requirementMapper.updateById(updateRequirement);
            
            // 保存需求审核记录
            RequirementAudit audit = new RequirementAudit();
            audit.setRequirementId(requirement.getId());
            audit.setRequirementData(objectMapper.writeValueAsString(requirementItems));
            audit.setTotalAmount(totalAmount);
            audit.setSubmittedBy(getCurrentUserId());
            audit.setSubmittedAt(new Date());
            audit.setStatus("pending");
            requirementAuditMapper.insert(audit);
            
            return Result.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("提交审核失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result updateRequirement(Requirement requirement) {
        // 更新需求信息
        requirementMapper.updateById(requirement);
        return Result.success(null);
    }

    // 创建ObjectMapper实例
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    @Override
    @Transactional
    public Result submitNegotiation(Long requirementId, List<RequirementItem> negotiatedItems, Double totalNegotiatedAmount) {
        try {
            // 更新需求状态为"议价待审核"
            Requirement requirement = new Requirement();
            requirement.setId(requirementId);
            requirement.setStatus("negotiating_pending");
            requirementMapper.updateById(requirement);
            
            // 保存议价数据到审核表
            NegotiationAudit audit = new NegotiationAudit();
            audit.setRequirementId(requirementId);
            // 使用Jackson将list转换为JSON字符串
            audit.setNegotiationData(objectMapper.writeValueAsString(negotiatedItems));
            audit.setNegotiatedTotal(totalNegotiatedAmount);
            audit.setSubmittedBy(getCurrentUserId());
            audit.setSubmittedAt(new Date());
            audit.setStatus("pending");
            negotiationAuditMapper.insert(audit);
            
            // 暂时不更新需求明细和总价，等待审核通过后再更新
            
            return Result.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("议价提交失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result auditNegotiation(Long auditId, String status, String rejectionReason) {
        try {
            System.out.println("=== 开始议价审核，审核记录ID: " + auditId + ", 审核结果: " + status);
            
            // 获取议价审核记录
            NegotiationAudit audit = negotiationAuditMapper.selectById(auditId);
            if (audit == null) {
                System.out.println("=== 议价审核记录不存在，审核记录ID: " + auditId);
                return Result.error("议价审核记录不存在");
            }
            
            System.out.println("=== 议价审核记录信息: ID=" + audit.getId() + ", 需求单ID=" + audit.getRequirementId() + ", 议价总额=" + audit.getNegotiatedTotal());
            
            // 更新审核记录
            audit.setStatus(status);
            audit.setAuditedBy(getCurrentUserId());
            audit.setAuditedAt(new Date());
            audit.setRejectionReason(rejectionReason);
            negotiationAuditMapper.updateById(audit);
            System.out.println("=== 议价审核记录更新成功");
            
            // 更新需求状态
            Requirement requirement = new Requirement();
            requirement.setId(audit.getRequirementId());
            
            if ("approved".equals(status)) {
                // 审核通过
                requirement.setStatus("approved");
                requirement.setTotalPayment(audit.getNegotiatedTotal());
                requirementMapper.updateById(requirement);
                System.out.println("=== 需求单状态更新成功，需求单ID: " + audit.getRequirementId() + ", 新状态: approved");
                
                // 更新需求明细的议价价格
                System.out.println("=== 开始更新需求明细的议价价格");
                // 使用Jackson将JSON字符串转换为List
                List<RequirementItem> negotiatedItems = objectMapper.readValue(
                    audit.getNegotiationData(), 
                    new com.fasterxml.jackson.core.type.TypeReference<List<RequirementItem>>() {}
                );
                System.out.println("=== 议价明细数量: " + negotiatedItems.size());
                
                for (RequirementItem item : negotiatedItems) {
                    requirementItemMapper.updatePriceByRequirementIdAndMaterialId(
                        audit.getRequirementId(),
                        item.getMaterialId(),
                        item.getNegotiatedPrice()
                    );
                }
                System.out.println("=== 需求明细议价价格更新成功");
                
                // 自动创建采购订单
                System.out.println("=== 开始创建采购订单，检查purchaseOrderService是否为空: " + (purchaseOrderService != null ? "否" : "是"));
                if (purchaseOrderService != null) {
                    System.out.println("=== 调用purchaseOrderService.createPurchaseOrderFromRequirement，需求单ID: " + audit.getRequirementId());
                    Result<Void> result = purchaseOrderService.createPurchaseOrderFromRequirement(audit.getRequirementId());
                    System.out.println("=== 创建采购订单结果: " + (result.getCode() == 200 ? "成功" : "失败") + ", 消息: " + result.getMessage());
                } else {
                    System.out.println("=== purchaseOrderService为空，无法创建采购订单");
                }
            } else if ("rejected".equals(status)) {
                // 审核未通过
                requirement.setStatus("rejected");
                requirementMapper.updateById(requirement);
                System.out.println("=== 需求单状态更新成功，需求单ID: " + audit.getRequirementId() + ", 新状态: rejected");
            }
            
            System.out.println("=== 议价审核完成，审核记录ID: " + auditId);
            return Result.success(null);
        } catch (Exception e) {
            System.out.println("=== 议价审核失败，审核记录ID: " + auditId);
            e.printStackTrace();
            return Result.error("议价审核失败: " + e.getMessage());
        }
    }
}