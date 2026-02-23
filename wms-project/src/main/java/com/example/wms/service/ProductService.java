package com.example.wms.service;

import com.example.wms.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    List<Product> getProductsByCondition(Product product);
    List<Product> getDeletedProducts();
    Product getProductById(Long id);
    Product getProductByFileHash(String fileHash);
    int saveProduct(Product product);
    int updateProduct(Product product);
    int deleteProduct(Long id);
    int restoreProduct(Long id);
    int permanentlyDeleteProduct(Long id);
}




