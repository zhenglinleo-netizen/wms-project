package com.example.wms.service;

import com.example.wms.entity.Warehouse;
import java.util.List;

public interface WarehouseService {
    List<Warehouse> getAllWarehouses();
    Warehouse getWarehouseById(Long id);
    int saveWarehouse(Warehouse warehouse);
    int updateWarehouse(Warehouse warehouse);
    int deleteWarehouse(Long id);
}




