package com.example.wms.service.impl;

import com.example.wms.entity.Warehouse;
import com.example.wms.mapper.WarehouseMapper;
import com.example.wms.service.WarehouseService;
import com.example.wms.utils.CacheKeyUtil;
import com.example.wms.utils.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseMapper warehouseMapper;
    @Autowired
    private CacheManager cacheManager;

    @Override
    public List<Warehouse> getAllWarehouses() {
        String cacheKey = CacheKeyUtil.getWarehouseListKey();
        List<Warehouse> warehouses = cacheManager.get(cacheKey, List.class);
        if (warehouses == null) {
            warehouses = warehouseMapper.selectAll();
            cacheManager.set(cacheKey, warehouses, CacheKeyUtil.DEFAULT_EXPIRE_TIME);
        }
        return warehouses;
    }

    @Override
    public Warehouse getWarehouseById(Long id) {
        String cacheKey = CacheKeyUtil.getWarehouseKey(id);
        Warehouse warehouse = cacheManager.get(cacheKey, Warehouse.class);
        if (warehouse == null) {
            warehouse = warehouseMapper.selectById(id);
            if (warehouse != null) {
                cacheManager.set(cacheKey, warehouse, CacheKeyUtil.DEFAULT_EXPIRE_TIME);
            }
        }
        return warehouse;
    }

    @Override
    public int saveWarehouse(Warehouse warehouse) {
        if (warehouseMapper.selectByCode(warehouse.getWarehouseCode()) != null) {
            throw new RuntimeException("仓库编码已存在");
        }
        if (warehouse.getStatus() == null) {
            warehouse.setStatus(1);
        }
        
        int result = warehouseMapper.insert(warehouse);
        
        // 清除缓存
        if (result > 0) {
            cacheManager.deletePattern(CacheKeyUtil.getPattern("warehouse:"));
        }
        
        return result;
    }

    @Override
    public int updateWarehouse(Warehouse warehouse) {
        int result = warehouseMapper.update(warehouse);
        
        // 清除缓存
        if (result > 0) {
            cacheManager.deletePattern(CacheKeyUtil.getPattern("warehouse:"));
        }
        
        return result;
    }

    @Override
    public int deleteWarehouse(Long id) {
        int result = warehouseMapper.deleteById(id);
        
        // 清除缓存
        if (result > 0) {
            cacheManager.deletePattern(CacheKeyUtil.getPattern("warehouse:"));
        }
        
        return result;
    }
}




