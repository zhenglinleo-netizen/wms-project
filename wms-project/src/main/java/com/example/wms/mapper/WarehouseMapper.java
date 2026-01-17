package com.example.wms.mapper;

import com.example.wms.entity.Warehouse;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface WarehouseMapper {
    Warehouse selectById(Long id);
    Warehouse selectByCode(String warehouseCode);
    List<Warehouse> selectAll();
    int insert(Warehouse warehouse);
    int update(Warehouse warehouse);
    int deleteById(Long id);
}




