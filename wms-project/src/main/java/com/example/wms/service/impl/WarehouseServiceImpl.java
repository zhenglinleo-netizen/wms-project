package com.example.wms.service.impl;

import com.example.wms.entity.Warehouse;
import com.example.wms.mapper.WarehouseMapper;
import com.example.wms.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Override
    public List<Warehouse> getAllWarehouses() {
        return warehouseMapper.selectAll();
    }

    @Override
    public Warehouse getWarehouseById(Long id) {
        return warehouseMapper.selectById(id);
    }

    @Override
    public int saveWarehouse(Warehouse warehouse) {
        if (warehouseMapper.selectByCode(warehouse.getWarehouseCode()) != null) {
            throw new RuntimeException("仓库编码已存在");
        }
        if (warehouse.getStatus() == null) {
            warehouse.setStatus(1);
        }
        return warehouseMapper.insert(warehouse);
    }

    @Override
    public int updateWarehouse(Warehouse warehouse) {
        return warehouseMapper.update(warehouse);
    }

    @Override
    public int deleteWarehouse(Long id) {
        return warehouseMapper.deleteById(id);
    }
}




