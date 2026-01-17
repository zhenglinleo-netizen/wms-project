package com.example.wms.mapper;

import com.example.wms.entity.SupplierProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SupplierProductMapper {
    /**
     * 插入供应商-辅料关联
     */
    int insert(SupplierProduct supplierProduct);

    /**
     * 更新供应商-辅料关联
     */
    int updateById(SupplierProduct supplierProduct);

    /**
     * 删除供应商-辅料关联
     */
    int deleteById(Long id);

    /**
     * 根据ID查询供应商-辅料关联
     */
    SupplierProduct selectById(Long id);

    /**
     * 根据供应商ID查询关联的辅料
     */
    List<SupplierProduct> selectBySupplierId(Long supplierId);

    /**
     * 根据辅料ID查询关联的供应商
     */
    List<SupplierProduct> selectByProductId(Long productId);

    /**
     * 查询所有供应商-辅料关联
     */
    List<SupplierProduct> selectAll();

    /**
     * 根据供应商ID和辅料ID查询关联
     */
    SupplierProduct selectBySupplierIdAndProductId(@Param("supplierId") Long supplierId, @Param("productId") Long productId);

    /**
     * 根据供应商ID删除所有关联
     */
    int deleteBySupplierId(Long supplierId);

    /**
     * 根据辅料ID删除所有关联
     */
    int deleteByProductId(Long productId);
}