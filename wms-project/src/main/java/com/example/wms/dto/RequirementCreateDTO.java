package com.example.wms.dto;

import lombok.Data;

@Data
public class RequirementCreateDTO {
    private Integer projectId;
    private Integer schemeId;
    private String purpose;
    private String priority;
    private String remark;
}