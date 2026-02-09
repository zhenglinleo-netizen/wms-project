package com.example.wms.mapper;

import com.example.wms.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ProductMapper {
    Product selectById(Long id);
    Product selectByCode(String productCode);
    List<Product> selectAll();
    List<Product> selectByCondition(Product product);
    Product selectByFileHash(String fileHash);
    int insert(Product product);
    int update(Product product);
    int deleteById(Long id);
}




