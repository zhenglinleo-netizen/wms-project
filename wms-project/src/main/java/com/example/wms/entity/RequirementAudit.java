package com.example.wms.entity;

import lombok.Data;

import java.util.Date;

/**
 * 需求审核记录表
 */
@Data
public class RequirementAudit {
    private Long id;
    private Long requirementId;
    private String requirementData;
    private Double totalAmount;
    private Long submittedBy;
    private Date submittedAt;
    private Long auditedBy;
    private Date auditedAt;
    private String status;
    private String rejectionReason;
}
