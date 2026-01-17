package com.example.wms.service.impl;

import com.example.wms.entity.Inventory;
import com.example.wms.entity.Product;
import com.example.wms.mapper.InventoryMapper;
import com.example.wms.mapper.ProductMapper;
import com.example.wms.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryMapper inventoryMapper;
    
    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Inventory> getAllInventories() {
        return inventoryMapper.selectAll();
    }

    @Override
    public List<Inventory> getInventoriesByWarehouse(Long warehouseId) {
        return inventoryMapper.selectByWarehouseId(warehouseId);
    }

    @Override
    public List<Inventory> getInventoriesByProduct(Long productId) {
        return inventoryMapper.selectByProductId(productId);
    }

    @Override
    public Inventory getInventory(Long warehouseId, Long productId) {
        return inventoryMapper.selectByWarehouseAndProduct(warehouseId, productId);
    }
    
    @Override
    public void updateInventory(Inventory inventory) {
        // 根据productCode查询productId
        Product product = productMapper.selectByCode(inventory.getProductCode());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 简化库存管理：使用默认仓库ID为1（如果只有一个仓库）
        Long warehouseId = inventory.getWarehouseId() != null ? inventory.getWarehouseId() : 1L;
        
        // 检查是否已存在库存记录
        Inventory existingInventory = inventoryMapper.selectByWarehouseAndProduct(warehouseId, product.getId());
        
        if (existingInventory != null) {
            // 如果存在，直接更新库存数量
            existingInventory.setQuantity(inventory.getQuantity());
            inventoryMapper.update(existingInventory);
        } else {
            // 如果不存在，创建新的库存记录
            Inventory newInventory = new Inventory();
            newInventory.setWarehouseId(warehouseId);
            newInventory.setProductId(product.getId());
            newInventory.setQuantity(inventory.getQuantity());
            newInventory.setAvgPrice(inventory.getAvgPrice() != null ? inventory.getAvgPrice() : BigDecimal.ZERO);
            inventoryMapper.insert(newInventory);
        }
    }
}




