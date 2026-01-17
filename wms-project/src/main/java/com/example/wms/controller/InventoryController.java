package com.example.wms.controller;

import com.example.wms.common.Result;
import com.example.wms.entity.Inventory;
import com.example.wms.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/list")
    public Result<List<Inventory>> getAllInventories() {
        List<Inventory> inventories = inventoryService.getAllInventories();
        return Result.success(inventories);
    }

    @GetMapping("/warehouse/{warehouseId}")
    public Result<List<Inventory>> getInventoriesByWarehouse(@PathVariable Long warehouseId) {
        List<Inventory> inventories = inventoryService.getInventoriesByWarehouse(warehouseId);
        return Result.success(inventories);
    }

    @GetMapping("/product/{productId}")
    public Result<List<Inventory>> getInventoriesByProduct(@PathVariable Long productId) {
        List<Inventory> inventories = inventoryService.getInventoriesByProduct(productId);
        return Result.success(inventories);
    }

    @GetMapping
    public Result<Inventory> getInventory(@RequestParam Long warehouseId, @RequestParam Long productId) {
        Inventory inventory = inventoryService.getInventory(warehouseId, productId);
        return Result.success(inventory);
    }
    
    @PostMapping("/update")
    public Result<String> updateInventory(@RequestBody Inventory inventory) {
        inventoryService.updateInventory(inventory);
        return Result.success("库存更新成功", null);
    }
}




