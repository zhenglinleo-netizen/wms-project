package com.example.wms.entity;

import lombok.Data;

@Data
public class RequirementItem {
    private Long id;
    private Long requirementId;
    private Long materialId;
    private String materialName;
    private String materialCode;
    private String specification;
    private Double price;
    private Double negotiatedPrice;
    private Double quantity;
    private String unit;
    private String purpose;
    private String remark;
    private Integer expectedDeliveryDays;
    private String imageUrl;
}