package com.example.wms.service;

import com.example.wms.common.Result;
import com.example.wms.entity.PurchaseOrder;
import java.util.Map;

public interface PurchaseOrderService {
    
    Result<Map<String, Object>> getPurchaseOrderList(String keyword, String status, Long creatorId, Integer page, Integer pageSize);
    
    Result<PurchaseOrder> getPurchaseOrderDetail(Long id);
    
    Result<Void> createPurchaseOrderFromRequirement(Long requirementId);
    
    Result<Void> updatePurchaseOrderStatus(Long id, String status);
    
    Result<Void> updateOrderItemStatus(Long itemId, String status);
    
    Result<Void> updateOrderItemSupplier(Long itemId, Long supplierId, String supplierName);
    
    Result<Void> deletePurchaseOrder(Long id);
}
