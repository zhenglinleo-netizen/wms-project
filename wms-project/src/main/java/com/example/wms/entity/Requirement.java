package com.example.wms.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Requirement {
    private Long id;
    private String requirementCode;
    private Long projectId;
    private Long schemeId;
    private Long creatorId;
    private String creatorName;
    private Long auditorId;
    private String auditorName;
    private String purpose;
    private Date deliveryDate;
    private Date expectedDeliveryDate;
    private Integer expectedDeliveryDays;
    private Date deadline;
    private String priority;
    private String status;
    private Double totalPayment;
    private Date confirmedTime;
    private Long confirmedBy;
    private Date createTime;
    private Date auditTime;
    private String auditRemark;
    private String remark;
    private Double totalAmount;
    
    // 关联的需求明细
    private List<RequirementItem> items;
    
    // 项目名称（非数据库字段，用于前端展示）
    private String projectName;
    
    // 方案名称（非数据库字段，用于前端展示）
    private String schemeName;
    
    // 创建者角色（非数据库字段，用于前端判断是否需要议价）
    private String creatorRole;
}