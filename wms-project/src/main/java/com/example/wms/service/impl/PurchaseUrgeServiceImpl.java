package com.example.wms.service.impl;

import com.example.wms.common.Result;
import com.example.wms.entity.PurchaseUrge;
import com.example.wms.mapper.PurchaseOrderMapper;
import com.example.wms.mapper.PurchaseUrgeMapper;
import com.example.wms.service.PurchaseUrgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PurchaseUrgeServiceImpl implements PurchaseUrgeService {

    @Autowired
    private PurchaseUrgeMapper purchaseUrgeMapper;
    
    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;
    
    private Long getCurrentUserId() {
        try {
            org.springframework.web.context.request.ServletRequestAttributes attributes = 
                (org.springframework.web.context.request.ServletRequestAttributes) 
                org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                Long userId = (Long) attributes.getRequest().getAttribute("userId");
                return userId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1L;
    }
    
    @Override
    public Result<List<PurchaseUrge>> getUrgeListByOrderId(Long orderId) {
        List<PurchaseUrge> urges = purchaseUrgeMapper.selectByOrderId(orderId);
        return Result.success(urges);
    }
    
    @Override
    public Result<List<PurchaseUrge>> getPendingUrgesByOrderId(Long orderId) {
        List<PurchaseUrge> urges = purchaseUrgeMapper.selectPendingByOrderId(orderId);
        return Result.success(urges);
    }
    
    @Override
    public Result<PurchaseUrge> createUrge(Long orderId, String urgeContent) {
        if (purchaseOrderMapper.selectById(orderId) == null) {
            return Result.error("采购订单不存在");
        }
        
        PurchaseUrge urge = new PurchaseUrge();
        urge.setOrderId(orderId);
        urge.setUserId(getCurrentUserId());
        urge.setUrgeContent(urgeContent);
        urge.setStatus("pending");
        urge.setUrgeTime(new Date());
        
        purchaseUrgeMapper.insert(urge);
        
        return Result.success(urge);
    }
    
    @Override
    public Result<Void> respondToUrge(Long urgeId, String responseContent) {
        PurchaseUrge urge = purchaseUrgeMapper.selectById(urgeId);
        if (urge == null) {
            return Result.error("催促记录不存在");
        }
        
        purchaseUrgeMapper.updateResponse(urgeId, responseContent, "responded");
        return Result.success(null);
    }
    
    @Override
    public Result<Void> deleteUrge(Long id) {
        if (purchaseUrgeMapper.selectById(id) == null) {
            return Result.error("催促记录不存在");
        }
        
        purchaseUrgeMapper.deleteById(id);
        return Result.success(null);
    }
}
