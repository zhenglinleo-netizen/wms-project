package com.example.wms.service;

import com.example.wms.common.Result;
import com.example.wms.entity.PurchaseUrge;
import java.util.List;

public interface PurchaseUrgeService {
    
    Result<List<PurchaseUrge>> getUrgeListByOrderId(Long orderId);
    
    Result<List<PurchaseUrge>> getPendingUrgesByOrderId(Long orderId);
    
    Result<PurchaseUrge> createUrge(Long orderId, String urgeContent);
    
    Result<Void> respondToUrge(Long urgeId, String responseContent);
    
    Result<Void> deleteUrge(Long id);
}
