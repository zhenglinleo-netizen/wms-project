package com.example.wms.service.impl;

import com.example.wms.common.Result;
import com.example.wms.entity.Supplier;
import com.example.wms.entity.SupplierProduct;
import com.example.wms.mapper.SupplierMapper;
import com.example.wms.mapper.SupplierProductMapper;
import com.example.wms.service.SupplierProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SupplierProductServiceImpl implements SupplierProductService {

    @Autowired
    private SupplierProductMapper supplierProductMapper;
    
    @Autowired
    private SupplierMapper supplierMapper;

    @Override
    public Result<List<SupplierProduct>> getAllSupplierProducts() {
        List<SupplierProduct> supplierProducts = supplierProductMapper.selectAll();
        return Result.success(supplierProducts);
    }

    @Override
    public Result<SupplierProduct> getSupplierProductById(Long id) {
        SupplierProduct supplierProduct = supplierProductMapper.selectById(id);
        if (supplierProduct == null) {
            return Result.error("供应商-辅料关联不存在");
        }
        return Result.success(supplierProduct);
    }

    @Override
    public Result<List<SupplierProduct>> getSupplierProductsBySupplierId(Long supplierId) {
        List<SupplierProduct> supplierProducts = supplierProductMapper.selectBySupplierId(supplierId);
        return Result.success(supplierProducts);
    }

    @Override
    public Result<List<SupplierProduct>> getSupplierProductsByProductId(Long productId) {
        List<SupplierProduct> supplierProducts = supplierProductMapper.selectByProductId(productId);
        return Result.success(supplierProducts);
    }

    @Override
    @Transactional
    public Result<SupplierProduct> createSupplierProduct(SupplierProduct supplierProduct) {
        // 检查是否已存在关联
        SupplierProduct existing = supplierProductMapper.selectBySupplierIdAndProductId(
                supplierProduct.getSupplierId(), supplierProduct.getProductId());
        if (existing != null) {
            return Result.error("该供应商已关联该辅料");
        }

        // 设置默认状态
        if (supplierProduct.getStatus() == null) {
            supplierProduct.setStatus(1); // 默认为启用
        }

        supplierProductMapper.insert(supplierProduct);
        return Result.success(supplierProduct);
    }

    @Override
    @Transactional
    public Result<SupplierProduct> updateSupplierProduct(SupplierProduct supplierProduct) {
        if (supplierProduct.getId() == null) {
            return Result.error("关联ID不能为空");
        }

        SupplierProduct existing = supplierProductMapper.selectById(supplierProduct.getId());
        if (existing == null) {
            return Result.error("供应商-辅料关联不存在");
        }

        supplierProductMapper.updateById(supplierProduct);
        return Result.success(supplierProduct);
    }

    @Override
    @Transactional
    public Result<Void> deleteSupplierProduct(Long id) {
        SupplierProduct existing = supplierProductMapper.selectById(id);
        if (existing == null) {
            return Result.error("供应商-辅料关联不存在");
        }

        supplierProductMapper.deleteById(id);
        return Result.success(null);
    }

    @Override
    @Transactional
    public Result<Void> batchCreateSupplierProducts(List<SupplierProduct> supplierProducts) {
        if (supplierProducts == null || supplierProducts.isEmpty()) {
            return Result.error("关联列表不能为空");
        }

        for (SupplierProduct supplierProduct : supplierProducts) {
            // 检查是否已存在关联
            SupplierProduct existing = supplierProductMapper.selectBySupplierIdAndProductId(
                    supplierProduct.getSupplierId(), supplierProduct.getProductId());
            if (existing == null) {
                // 设置默认状态
                if (supplierProduct.getStatus() == null) {
                    supplierProduct.setStatus(1); // 默认为启用
                }
                supplierProductMapper.insert(supplierProduct);
            }
        }

        return Result.success(null);
    }

    @Override
    public Result<List<Supplier>> getSuppliersByProductId(Long productId) {
        // 根据辅料ID获取所有关联的供应商-辅料关系
        List<SupplierProduct> supplierProducts = supplierProductMapper.selectByProductId(productId);
        
        // 从关系中提取供应商ID并获取供应商详情
        List<Supplier> suppliers = new ArrayList<>();
        for (SupplierProduct sp : supplierProducts) {
            Supplier supplier = supplierMapper.selectById(sp.getSupplierId());
            if (supplier != null) {
                suppliers.add(supplier);
            }
        }
        
        return Result.success(suppliers);
    }
}