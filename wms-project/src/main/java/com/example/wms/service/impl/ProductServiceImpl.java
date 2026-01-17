package com.example.wms.service.impl;

import com.example.wms.entity.Product;
import com.example.wms.entity.MaterialCategory;
import com.example.wms.mapper.ProductMapper;
import com.example.wms.mapper.MaterialCategoryMapper;
import com.example.wms.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private MaterialCategoryMapper categoryMapper;

    @Override
    public List<Product> getAllProducts() {
        return productMapper.selectAll();
    }

    @Override
    public List<Product> getProductsByCondition(Product product) {
        return productMapper.selectByCondition(product);
    }

    @Override
    public Product getProductById(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public int saveProduct(Product product) {
        if (productMapper.selectByCode(product.getProductCode()) != null) {
            throw new RuntimeException("商品编码已存在");
        }
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        if (product.getUnit() == null || product.getUnit().isEmpty()) {
            product.setUnit("件");
        }
        if (product.getCategoryId() == null) {
            if (product.getCategory() != null && !product.getCategory().isEmpty()) {
                MaterialCategory cat = categoryMapper.selectByName(product.getCategory());
                if (cat == null) {
                    MaterialCategory newCat = new MaterialCategory();
                    newCat.setCategoryName(product.getCategory());
                    newCat.setParentId(0L);
                    newCat.setSortOrder(0);
                    newCat.setDescription(null);
                    categoryMapper.insert(newCat);
                    product.setCategoryId(newCat.getId());
                } else {
                    product.setCategoryId(cat.getId());
                }
            }
        }
        return productMapper.insert(product);
    }

    @Override
    public int updateProduct(Product product) {
        return productMapper.update(product);
    }

    @Override
    public int deleteProduct(Long id) {
        return productMapper.deleteById(id);
    }
}




