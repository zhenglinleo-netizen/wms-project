package com.example.wms.mapper;

import com.example.wms.entity.MaterialCategory;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MaterialCategoryMapper {
    MaterialCategory selectByName(String categoryName);
    MaterialCategory selectById(Long id);
    List<MaterialCategory> selectAll();
    int insert(MaterialCategory category);
    int update(MaterialCategory category);
    int deleteById(Long id);
}
