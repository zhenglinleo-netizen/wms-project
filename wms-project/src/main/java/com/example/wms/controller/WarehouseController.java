package com.example.wms.controller;

import com.example.wms.common.Result;
import com.example.wms.entity.Warehouse;
import com.example.wms.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/list")
    public Result<List<Warehouse>> getAllWarehouses() {
        List<Warehouse> warehouses = warehouseService.getAllWarehouses();
        return Result.success(warehouses);
    }

    @GetMapping("/{id}")
    public Result<Warehouse> getWarehouseById(@PathVariable Long id) {
        Warehouse warehouse = warehouseService.getWarehouseById(id);
        return Result.success(warehouse);
    }

    @PostMapping
    public Result<String> saveWarehouse(@RequestBody Warehouse warehouse) {
        warehouseService.saveWarehouse(warehouse);
        return Result.success("添加成功", null);
    }

    @PutMapping
    public Result<String> updateWarehouse(@RequestBody Warehouse warehouse) {
        warehouseService.updateWarehouse(warehouse);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return Result.success("删除成功", null);
    }
}




