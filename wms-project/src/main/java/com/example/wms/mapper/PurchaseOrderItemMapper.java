package com.example.wms.mapper;

import com.example.wms.entity.PurchaseOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PurchaseOrderItemMapper {
    
    List<PurchaseOrderItem> selectByOrderId(Long orderId);
    
    int insert(PurchaseOrderItem item);
    
    int batchInsert(@Param("items") List<PurchaseOrderItem> items);
    
    int updateById(PurchaseOrderItem item);
    
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    int updateSupplier(@Param("id") Long id, @Param("supplierId") Long supplierId, @Param("supplierName") String supplierName);
    
    int deleteByOrderId(Long orderId);
}
