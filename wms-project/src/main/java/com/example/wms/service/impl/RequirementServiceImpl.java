package com.example.wms.service.impl;

import com.example.wms.common.Result;
import com.example.wms.dto.RequirementCreateDTO;
import com.example.wms.entity.Project;
import com.example.wms.entity.Requirement;
import com.example.wms.entity.RequirementItem;
import com.example.wms.entity.Scheme;
import com.example.wms.entity.SchemeItem;
import com.example.wms.entity.Product;

import com.example.wms.entity.RequirementAudit;
import com.example.wms.mapper.ProjectMapper;
import com.example.wms.mapper.RequirementItemMapper;
import com.example.wms.mapper.RequirementMapper;
import com.example.wms.mapper.SchemeItemMapper;
import com.example.wms.mapper.SchemeMapper;
import com.example.wms.mapper.ProductMapper;

import com.example.wms.mapper.RequirementAuditMapper;
import com.example.wms.service.RequirementService;
import com.example.wms.service.SchemeItemService;
import com.example.wms.service.SchemeService;
import com.example.wms.service.PurchaseOrderService;
import com.example.wms.service.NoticeService;
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
    private ProjectMapper projectMapper;

    @Autowired
    private SchemeMapper schemeMapper;

    @Autowired
    private SchemeItemMapper schemeItemMapper;

    @Autowired
    private ProductMapper productMapper;



    @Autowired
    private RequirementAuditMapper requirementAuditMapper;

    @Autowired
    private SchemeService schemeService;

    @Autowired
    private SchemeItemService schemeItemService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private NoticeService noticeService;

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
        
        // 查询项目名称和方案名称
        String projectName = null;
        String schemeName = null;
        if (requirement.getProjectId() != null) {
            Project project = projectMapper.getProjectById(requirement.getProjectId(), null);
            if (project != null) {
                projectName = project.getProjectName();
            }
        }
        if (requirement.getSchemeId() != null) {
            Scheme scheme = schemeMapper.getSchemeById(requirement.getSchemeId());
            if (scheme != null) {
                schemeName = scheme.getSchemeName();
            }
        }
        
        // 构造返回结果
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
        resultMap.put("projectName", projectName);
        resultMap.put("schemeName", schemeName);
        resultMap.put("items", items);
        
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
        requirement.setStatus("pending");
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
                totalAmount += item.getQuantity() * (item.getPrice() > 0 ? item.getPrice() : item.getPrice());
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
            
            // 发送通知给所有管理员
            String title = "采购需求审核通知";
            String content = "用户提交了采购需求，需求单号：" + requirement.getRequirementCode() + "，请及时审核。";
            noticeService.sendNoticeToAdmins(title, content, "requirement_audit", id);
            
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
                totalAmount += item.getQuantity() * (item.getPrice() > 0 ? item.getPrice() : item.getPrice());
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


}