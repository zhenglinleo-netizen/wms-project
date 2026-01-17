package com.example.wms.entity;

import java.util.Date;

public class PurchaseUrge {
    private Long id;
    private Long orderId;
    private Long userId;
    private String urgeContent;
    private String responseContent;
    private String status;
    private Date urgeTime;
    private Date responseTime;
    
    private String userName;
    private String orderCode;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUrgeContent() {
        return urgeContent;
    }
    
    public void setUrgeContent(String urgeContent) {
        this.urgeContent = urgeContent;
    }
    
    public String getResponseContent() {
        return responseContent;
    }
    
    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getUrgeTime() {
        return urgeTime;
    }
    
    public void setUrgeTime(Date urgeTime) {
        this.urgeTime = urgeTime;
    }
    
    public Date getResponseTime() {
        return responseTime;
    }
    
    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getOrderCode() {
        return orderCode;
    }
    
    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
}
