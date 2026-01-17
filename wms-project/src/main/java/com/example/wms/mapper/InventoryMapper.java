package com.example.wms.mapper;

import com.example.wms.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface InventoryMapper {
    Inventory selectById(Long id);
    Inventory selectByWarehouseAndProduct(@org.apache.ibatis.annotations.Param("warehouseId") Long warehouseId, 
                                           @org.apache.ibatis.annotations.Param("productId") Long productId);
    List<Inventory> selectAll();
    List<Inventory> selectByWarehouseId(Long warehouseId);
    List<Inventory> selectByProductId(Long productId);
    int insert(Inventory inventory);
    int update(Inventory inventory);
    int updateQuantity(@org.apache.ibatis.annotations.Param("warehouseId") Long warehouseId,
                       @org.apache.ibatis.annotations.Param("productId") Long productId,
                       @org.apache.ibatis.annotations.Param("quantity") java.math.BigDecimal quantity);
    int deleteById(Long id);
}




