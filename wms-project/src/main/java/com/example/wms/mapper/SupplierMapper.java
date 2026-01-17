package com.example.wms.mapper;

import com.example.wms.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface SupplierMapper {
    
    Supplier selectById(Long id);
    
    Supplier selectByCode(String supplierCode);
    
    List<Supplier> selectAll();
    
    int insert(Supplier supplier);
    
    int updateById(Supplier supplier);
    
    int deleteById(Long id);
}
