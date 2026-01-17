package com.example.wms.entity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 方案实体类
 */
public class Scheme {
    private Long id;
    private String schemeCode;
    private String schemeName;
    private Long projectId;
    private Integer version;
    private String description;
    private Long creatorId;
    private String creatorCompany;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer materialsCount;
    private List<SchemeItem> materials;

    // getter and setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        this.schemeCode = schemeCode;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorCompany() {
        return creatorCompany;
    }

    public void setCreatorCompany(String creatorCompany) {
        this.creatorCompany = creatorCompany;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getMaterialsCount() {
        return materialsCount;
    }

    public void setMaterialsCount(Integer materialsCount) {
        this.materialsCount = materialsCount;
    }

    public List<SchemeItem> getMaterials() {
        return materials;
    }

    public void setMaterials(List<SchemeItem> materials) {
        this.materials = materials;
    }
}