package com.example.wms.entity;

import java.util.Date;

public class Supplier {
    private Long id;
    private String supplierCode;
    private String supplierName;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private Date cooperationStartTime; // 合作开始时间
    private String processModuleMatch; // 工艺模块匹配
    private String coreExpertise; // 核心擅长工艺
    private String weaknesses; // 不擅长/风险
    private String supplierRole; // 供应商角色
    private Integer rating;
    private Integer status;
    private String remark;
    private Date createTime;
    private Date updateTime;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSupplierCode() {
        return supplierCode;
    }
    
    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }
    
    public String getSupplierName() {
        return supplierName;
    }
    
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Date getCooperationStartTime() {
        return cooperationStartTime;
    }
    
    public void setCooperationStartTime(Date cooperationStartTime) {
        this.cooperationStartTime = cooperationStartTime;
    }
    
    public String getProcessModuleMatch() {
        return processModuleMatch;
    }
    
    public void setProcessModuleMatch(String processModuleMatch) {
        this.processModuleMatch = processModuleMatch;
    }
    
    public String getCoreExpertise() {
        return coreExpertise;
    }
    
    public void setCoreExpertise(String coreExpertise) {
        this.coreExpertise = coreExpertise;
    }
    
    public String getWeaknesses() {
        return weaknesses;
    }
    
    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }
    
    public String getSupplierRole() {
        return supplierRole;
    }
    
    public void setSupplierRole(String supplierRole) {
        this.supplierRole = supplierRole;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
