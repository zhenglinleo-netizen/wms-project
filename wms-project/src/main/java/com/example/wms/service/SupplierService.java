package com.example.wms.service;

import com.example.wms.common.Result;
import com.example.wms.entity.Supplier;

import java.util.List;

public interface SupplierService {
    /**
     * 获取供应商列表
     */
    Result<List<Supplier>> getAllSuppliers();

    /**
     * 获取供应商详情
     */
    Result<Supplier> getSupplierById(Long id);

    /**
     * 创建供应商
     */
    Result<Supplier> createSupplier(Supplier supplier);

    /**
     * 更新供应商
     */
    Result<Supplier> updateSupplier(Supplier supplier);

    /**
     * 删除供应商
     */
    Result<Void> deleteSupplier(Long id);
}