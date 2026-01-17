package com.example.wms.controller;

import com.example.wms.common.Result;
import com.example.wms.entity.PurchaseOrder;
import com.example.wms.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/purchase-order")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;
    
    @GetMapping("/list")
    public Result<Map<String, Object>> getPurchaseOrderList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return purchaseOrderService.getPurchaseOrderList(keyword, status, creatorId, page, pageSize);
    }
    
    @GetMapping("/detail/{id}")
    public Result<PurchaseOrder> getPurchaseOrderDetail(@PathVariable Long id) {
        return purchaseOrderService.getPurchaseOrderDetail(id);
    }
    
    @PostMapping("/create-from-requirement/{requirementId}")
    public Result<Void> createPurchaseOrderFromRequirement(@PathVariable Long requirementId) {
        return purchaseOrderService.createPurchaseOrderFromRequirement(requirementId);
    }
    
    @PutMapping("/status/{id}")
    public Result<Void> updatePurchaseOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        return purchaseOrderService.updatePurchaseOrderStatus(id, status);
    }
    
    @PutMapping("/item/status/{itemId}")
    public Result<Void> updateOrderItemStatus(@PathVariable Long itemId, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        return purchaseOrderService.updateOrderItemStatus(itemId, status);
    }
    
    @PutMapping("/item/supplier/{itemId}")
    public Result<Void> updateOrderItemSupplier(@PathVariable Long itemId, @RequestBody Map<String, Object> request) {
        Long supplierId = Long.valueOf(request.get("supplierId").toString());
        String supplierName = request.get("supplierName").toString();
        return purchaseOrderService.updateOrderItemSupplier(itemId, supplierId, supplierName);
    }
    
    @DeleteMapping("/delete/{id}")
    public Result<Void> deletePurchaseOrder(@PathVariable Long id) {
        return purchaseOrderService.deletePurchaseOrder(id);
    }
}
