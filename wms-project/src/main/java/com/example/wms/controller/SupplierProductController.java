package com.example.wms.controller;

import com.example.wms.annotation.RequireAdmin;
import com.example.wms.common.Result;
import com.example.wms.entity.SupplierProduct;
import com.example.wms.service.SupplierProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supplier-product")
public class SupplierProductController {

    @Autowired
    private SupplierProductService supplierProductService;

    /**
     * 获取所有供应商-辅料关联
     */
    @GetMapping("/list")
    public Result<List<SupplierProduct>> getAllSupplierProducts() {
        return supplierProductService.getAllSupplierProducts();
    }

    /**
     * 根据ID获取供应商-辅料关联
     */
    @GetMapping("/{id}")
    public Result<SupplierProduct> getSupplierProductById(@PathVariable Long id) {
        return supplierProductService.getSupplierProductById(id);
    }

    /**
     * 根据供应商ID获取关联的辅料
     */
    @GetMapping("/by-supplier/{supplierId}")
    public Result<List<SupplierProduct>> getSupplierProductsBySupplierId(@PathVariable Long supplierId) {
        return supplierProductService.getSupplierProductsBySupplierId(supplierId);
    }

    /**
     * 根据辅料ID获取关联的供应商
     */
    @GetMapping("/by-product/{productId}")
    public Result<List<SupplierProduct>> getSupplierProductsByProductId(@PathVariable Long productId) {
        return supplierProductService.getSupplierProductsByProductId(productId);
    }

    /**
     * 创建供应商-辅料关联
     */
    @PostMapping
    public Result<SupplierProduct> createSupplierProduct(@RequestBody SupplierProduct supplierProduct) {
        return supplierProductService.createSupplierProduct(supplierProduct);
    }

    /**
     * 更新供应商-辅料关联
     */
    @PutMapping
    public Result<SupplierProduct> updateSupplierProduct(@RequestBody SupplierProduct supplierProduct) {
        return supplierProductService.updateSupplierProduct(supplierProduct);
    }

    /**
     * 删除供应商-辅料关联
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteSupplierProduct(@PathVariable Long id) {
        return supplierProductService.deleteSupplierProduct(id);
    }

    /**
     * 批量创建供应商-辅料关联
     */
    @PostMapping("/batch")
    public Result<Void> batchCreateSupplierProducts(@RequestBody List<SupplierProduct> supplierProducts) {
        return supplierProductService.batchCreateSupplierProducts(supplierProducts);
    }

    /**
     * 根据辅料ID获取可以提供该辅料的供应商列表
     */
    @GetMapping("/suppliers/{productId}")
    public Result<List<com.example.wms.entity.Supplier>> getSuppliersByProductId(@PathVariable Long productId) {
        return supplierProductService.getSuppliersByProductId(productId);
    }
}