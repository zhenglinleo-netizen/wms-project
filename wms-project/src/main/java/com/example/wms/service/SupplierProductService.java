package com.example.wms.service;

import com.example.wms.common.Result;
import com.example.wms.entity.SupplierProduct;

import java.util.List;

public interface SupplierProductService {
    /**
     * 获取所有供应商-辅料关联
     */
    Result<List<SupplierProduct>> getAllSupplierProducts();

    /**
     * 根据ID获取供应商-辅料关联
     */
    Result<SupplierProduct> getSupplierProductById(Long id);

    /**
     * 根据供应商ID获取关联的辅料
     */
    Result<List<SupplierProduct>> getSupplierProductsBySupplierId(Long supplierId);

    /**
     * 根据辅料ID获取关联的供应商
     */
    Result<List<SupplierProduct>> getSupplierProductsByProductId(Long productId);

    /**
     * 创建供应商-辅料关联
     */
    Result<SupplierProduct> createSupplierProduct(SupplierProduct supplierProduct);

    /**
     * 更新供应商-辅料关联
     */
    Result<SupplierProduct> updateSupplierProduct(SupplierProduct supplierProduct);

    /**
     * 删除供应商-辅料关联
     */
    Result<Void> deleteSupplierProduct(Long id);

    /**
     * 批量添加供应商-辅料关联
     */
    Result<Void> batchCreateSupplierProducts(List<SupplierProduct> supplierProducts);

    /**
     * 根据辅料ID获取可以提供该辅料的供应商列表
     */
    Result<List<com.example.wms.entity.Supplier>> getSuppliersByProductId(Long productId);
}