package com.example.wms.service;

import com.example.wms.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    List<Product> getProductsByCondition(Product product);
    Product getProductById(Long id);
    Product getProductByFileHash(String fileHash);
    int saveProduct(Product product);
    int updateProduct(Product product);
    int deleteProduct(Long id);
}




