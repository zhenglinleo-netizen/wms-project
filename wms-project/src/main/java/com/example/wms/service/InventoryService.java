package com.example.wms.service;

import com.example.wms.entity.Inventory;
import java.util.List;

public interface InventoryService {
    List<Inventory> getAllInventories();
    List<Inventory> getInventoriesByWarehouse(Long warehouseId);
    List<Inventory> getInventoriesByProduct(Long productId);
    Inventory getInventory(Long warehouseId, Long productId);
    void updateInventory(Inventory inventory);
}




