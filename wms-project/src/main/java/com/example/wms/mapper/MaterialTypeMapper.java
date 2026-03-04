package com.example.wms.mapper;

import com.example.wms.entity.MaterialType;

/**
 * 材质类型Mapper
 */
public interface MaterialTypeMapper {
    /**
     * 根据材质名称查询材质类型
     */
    MaterialType selectByName(String typeName);
    
    /**
     * 插入新的材质类型
     */
    int insert(MaterialType materialType);
}