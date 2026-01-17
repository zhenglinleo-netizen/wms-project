package com.example.wms.service.impl;

import com.example.wms.common.Result;
import com.example.wms.entity.*;
import com.example.wms.mapper.*;
import com.example.wms.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;
    
    @Autowired
    private PurchaseOrderItemMapper purchaseOrderItemMapper;
    
    @Autowired
    private RequirementMapper requirementMapper;
    
    @Autowired
    private RequirementItemMapper requirementItemMapper;
    
    @Autowired
    private SupplierMapper supplierMapper;
    
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
    public Result<Map<String, Object>> getPurchaseOrderList(String keyword, String status, Long creatorId, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        
        List<PurchaseOrder> orders = purchaseOrderMapper.selectList(keyword, status, creatorId, offset, pageSize);
        int total = purchaseOrderMapper.selectCount(keyword, status, creatorId);
        
        Map<String, Object> pageData = new HashMap<>();
        pageData.put("records", orders);
        pageData.put("total", total);
        
        return Result.success(pageData);
    }
    
    @Override
    public Result<PurchaseOrder> getPurchaseOrderDetail(Long id) {
        PurchaseOrder order = purchaseOrderMapper.selectById(id);
        if (order == null) {
            return Result.error("采购订单不存在");
        }
        
        List<PurchaseOrderItem> items = purchaseOrderItemMapper.selectByOrderId(id);
        order.setItems(items);
        
        return Result.success(order);
    }
    
    @Override
    @Transactional
    public Result<Void> createPurchaseOrderFromRequirement(Long requirementId) {
        try {
            System.out.println("=== 开始创建采购订单，需求单ID: " + requirementId);
            
            Requirement requirement = requirementMapper.selectById(requirementId);
            if (requirement == null) {
                System.out.println("=== 需求单不存在，需求单ID: " + requirementId);
                return Result.error("需求单不存在");
            }
            
            System.out.println("=== 需求单信息: ID=" + requirement.getId() + ", 需求单号=" + requirement.getRequirementCode() + ", 创建者ID=" + requirement.getCreatorId());
            
            PurchaseOrder existingOrder = purchaseOrderMapper.selectByRequirementId(requirementId);
            if (existingOrder != null) {
                System.out.println("=== 该需求单已创建采购订单，采购订单ID: " + existingOrder.getId());
                return Result.error("该需求单已创建采购订单");
            }
            
            List<RequirementItem> requirementItems = requirementItemMapper.selectByRequirementId(requirementId);
            if (requirementItems.isEmpty()) {
                System.out.println("=== 需求单无明细，无法创建采购订单，需求单ID: " + requirementId);
                return Result.error("需求单无明细，无法创建采购订单");
            }
            
            System.out.println("=== 需求单明细数量: " + requirementItems.size());
            
            PurchaseOrder order = new PurchaseOrder();
            order.setRequirementId(requirementId);
            order.setOrderCode("PO" + System.currentTimeMillis());
            order.setStatus("pending");
            order.setCreatorId(requirement.getCreatorId()); // 使用需求单的创建者ID
            order.setCreateTime(new Date());
            order.setOrderDate(new Date());
            
            System.out.println("=== 采购订单信息: 订单号=" + order.getOrderCode() + ", 创建者ID=" + order.getCreatorId());
            
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<PurchaseOrderItem> orderItems = new ArrayList<>();
            
            for (RequirementItem item : requirementItems) {
                System.out.println("=== 处理需求明细: 辅料ID=" + item.getMaterialId() + ", 辅料名称=" + item.getMaterialName() + ", 数量=" + item.getQuantity() + ", 单价=" + item.getPrice() + ", 议价单价=" + item.getNegotiatedPrice());
                
                PurchaseOrderItem orderItem = new PurchaseOrderItem();
                orderItem.setMaterialId(item.getMaterialId());
                orderItem.setMaterialName(item.getMaterialName());
                orderItem.setMaterialCode(item.getMaterialCode());
                orderItem.setSpecification(item.getSpecification());
                orderItem.setQuantity(item.getQuantity() != null ? BigDecimal.valueOf(item.getQuantity()) : BigDecimal.ZERO);
                orderItem.setUnit(item.getUnit());
                
                BigDecimal negotiatedPrice = item.getNegotiatedPrice() != null ? BigDecimal.valueOf(item.getNegotiatedPrice()) : null;
                BigDecimal originalPrice = item.getPrice() != null ? BigDecimal.valueOf(item.getPrice()) : BigDecimal.ZERO;
                BigDecimal unitPrice = negotiatedPrice != null && negotiatedPrice.compareTo(BigDecimal.ZERO) > 0 
                    ? negotiatedPrice : originalPrice;
                orderItem.setUnitPrice(unitPrice);
                orderItem.setTotalPrice(unitPrice.multiply(orderItem.getQuantity()));
                orderItem.setStatus("pending");
                totalAmount = totalAmount.add(orderItem.getTotalPrice());
                orderItems.add(orderItem);
                
                System.out.println("=== 生成订单明细: 辅料名称=" + orderItem.getMaterialName() + ", 数量=" + orderItem.getQuantity() + ", 单价=" + orderItem.getUnitPrice() + ", 总价=" + orderItem.getTotalPrice());
            }
            
            order.setTotalAmount(totalAmount);
            System.out.println("=== 订单总金额: " + totalAmount);
            
            // 先检查是否有可用的供应商
            Supplier supplier = supplierMapper.selectById(1L);
            if (supplier != null) {
                order.setSupplierId(1L);
                System.out.println("=== 设置供应商ID: 1");
            } else {
                // 如果没有ID为1的供应商，查询第一个可用的供应商
                List<Supplier> suppliers = supplierMapper.selectAll();
                if (!suppliers.isEmpty()) {
                    order.setSupplierId(suppliers.get(0).getId());
                    System.out.println("=== 设置供应商ID: " + suppliers.get(0).getId());
                } else {
                    // 如果没有任何供应商，创建一个默认供应商
                    Supplier defaultSupplier = new Supplier();
                    defaultSupplier.setSupplierCode("SUPP" + System.currentTimeMillis());
                    defaultSupplier.setSupplierName("默认供应商");
                    defaultSupplier.setContactPerson("管理员");
                    defaultSupplier.setPhone("13800138000");
                    defaultSupplier.setEmail("admin@example.com");
                    defaultSupplier.setAddress("默认地址");
                    defaultSupplier.setRating(5);
                    defaultSupplier.setStatus(1);
                    defaultSupplier.setCreateTime(new Date());
                    supplierMapper.insert(defaultSupplier);
                    order.setSupplierId(defaultSupplier.getId());
                    System.out.println("=== 创建并设置默认供应商ID: " + defaultSupplier.getId());
                }
            }
            
            System.out.println("=== 准备插入采购订单");
            purchaseOrderMapper.insert(order);
            System.out.println("=== 采购订单插入成功，订单ID: " + order.getId());
            
            for (PurchaseOrderItem item : orderItems) {
                item.setOrderId(order.getId());
            }
            System.out.println("=== 准备插入订单明细，数量: " + orderItems.size());
            purchaseOrderItemMapper.batchInsert(orderItems);
            System.out.println("=== 订单明细插入成功");
            
            System.out.println("=== 采购订单创建成功，订单ID: " + order.getId());
            return Result.success(null);
        } catch (Exception e) {
            System.out.println("=== 创建采购订单失败，需求单ID: " + requirementId);
            e.printStackTrace();
            return Result.error("创建采购订单失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Void> updatePurchaseOrderStatus(Long id, String status) {
        PurchaseOrder order = purchaseOrderMapper.selectById(id);
        if (order == null) {
            return Result.error("采购订单不存在");
        }
        
        purchaseOrderMapper.updateStatus(id, status);
        return Result.success(null);
    }
    
    @Override
    public Result<Void> updateOrderItemStatus(Long itemId, String status) {
        List<PurchaseOrderItem> items = purchaseOrderItemMapper.selectByOrderId(itemId);
        boolean found = false;
        for (PurchaseOrderItem item : items) {
            if (item.getId().equals(itemId)) {
                found = true;
                break;
            }
        }
        if (!found) {
            return Result.error("订单明细不存在");
        }
        
        purchaseOrderItemMapper.updateStatus(itemId, status);
        return Result.success(null);
    }
    
    @Override
    public Result<Void> updateOrderItemSupplier(Long itemId, Long supplierId, String supplierName) {
        List<PurchaseOrderItem> items = purchaseOrderItemMapper.selectByOrderId(itemId);
        boolean found = false;
        for (PurchaseOrderItem item : items) {
            if (item.getId().equals(itemId)) {
                found = true;
                break;
            }
        }
        if (!found) {
            return Result.error("订单明细不存在");
        }
        
        purchaseOrderItemMapper.updateSupplier(itemId, supplierId, supplierName);
        return Result.success(null);
    }
    
    @Override
    public Result<Void> deletePurchaseOrder(Long id) {
        PurchaseOrder order = purchaseOrderMapper.selectById(id);
        if (order == null) {
            return Result.error("采购订单不存在");
        }
        
        purchaseOrderItemMapper.deleteByOrderId(id);
        purchaseOrderMapper.deleteById(id);
        
        return Result.success(null);
    }
}
