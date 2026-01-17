package com.example.wms.entity;

import lombok.Data;

import java.util.Date;

/**
 * 议价审核记录
 */
@Data
public class NegotiationAudit {
    private Long id;
    
    // 关联的需求单ID
    private Long requirementId;
    
    // 议价数据，包含所有明细的议价信息
    private String negotiationData;
    
    // 议价总额
    private Double negotiatedTotal;
    
    // 提交人ID
    private Long submittedBy;
    
    // 提交时间
    private Date submittedAt;
    
    // 审核人ID
    private Long auditedBy;
    
    // 审核时间
    private Date auditedAt;
    
    // 审核状态：pending(待审核), approved(已通过), rejected(已拒绝)
    private String status;
    
    // 驳回原因
    private String rejectionReason;
}
