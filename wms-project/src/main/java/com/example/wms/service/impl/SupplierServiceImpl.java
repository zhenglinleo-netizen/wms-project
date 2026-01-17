package com.example.wms.service.impl;

import com.example.wms.common.Result;
import com.example.wms.entity.Supplier;
import com.example.wms.mapper.SupplierMapper;
import com.example.wms.mapper.SupplierProductMapper;
import com.example.wms.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierMapper supplierMapper;
    
    @Autowired
    private SupplierProductMapper supplierProductMapper;

    @Override
    public Result<List<Supplier>> getAllSuppliers() {
        List<Supplier> suppliers = supplierMapper.selectAll();
        return Result.success(suppliers);
    }

    @Override
    public Result<Supplier> getSupplierById(Long id) {
        Supplier supplier = supplierMapper.selectById(id);
        if (supplier == null) {
            return Result.error("供应商不存在");
        }
        return Result.success(supplier);
    }

    @Override
    @Transactional
    public Result<Supplier> createSupplier(Supplier supplier) {
        // 设置默认值
        if (supplier.getStatus() == null) {
            supplier.setStatus(1); // 默认启用
        }
        if (supplier.getRating() == null) {
            supplier.setRating(5); // 默认评分5
        }
        if (supplier.getCreateTime() == null) {
            supplier.setCreateTime(new Date());
        }
        if (supplier.getSupplierCode() == null || supplier.getSupplierCode().isEmpty()) {
            supplier.setSupplierCode("SUPP" + System.currentTimeMillis());
        }

        supplierMapper.insert(supplier);
        return Result.success(supplier);
    }

    @Override
    @Transactional
    public Result<Supplier> updateSupplier(Supplier supplier) {
        if (supplier.getId() == null) {
            return Result.error("供应商ID不能为空");
        }

        Supplier existingSupplier = supplierMapper.selectById(supplier.getId());
        if (existingSupplier == null) {
            return Result.error("供应商不存在");
        }

        supplierMapper.updateById(supplier);
        return Result.success(supplier);
    }

    @Override
    @Transactional
    public Result<Void> deleteSupplier(Long id) {
        Supplier existingSupplier = supplierMapper.selectById(id);
        if (existingSupplier == null) {
            return Result.error("供应商不存在");
        }

        // 先删除与该供应商相关的所有辅料关联关系
        supplierProductMapper.deleteBySupplierId(id);
        
        // 再删除供应商
        supplierMapper.deleteById(id);
        return Result.success(null);
    }
}