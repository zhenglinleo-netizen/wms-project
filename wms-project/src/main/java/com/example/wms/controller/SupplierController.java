package com.example.wms.controller;

import com.example.wms.annotation.RequireAdmin;
import com.example.wms.common.Result;
import com.example.wms.entity.Supplier;
import com.example.wms.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    /**
     * 获取所有供应商
     */
    @GetMapping("/list")
    public Result<List<Supplier>> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    /**
     * 获取供应商详情
     */
    @GetMapping("/{id}")
    @RequireAdmin
    public Result<Supplier> getSupplierById(@PathVariable Long id) {
        return supplierService.getSupplierById(id);
    }

    /**
     * 创建供应商
     */
    @PostMapping
    @RequireAdmin
    public Result<Supplier> createSupplier(@RequestBody Supplier supplier) {
        return supplierService.createSupplier(supplier);
    }

    /**
     * 更新供应商
     */
    @PutMapping
    @RequireAdmin
    public Result<Supplier> updateSupplier(@RequestBody Supplier supplier) {
        return supplierService.updateSupplier(supplier);
    }

    /**
     * 删除供应商
     */
    @DeleteMapping("/{id}")
    @RequireAdmin
    public Result<Void> deleteSupplier(@PathVariable Long id) {
        return supplierService.deleteSupplier(id);
    }
}