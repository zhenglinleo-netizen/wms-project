package com.example.wms.service.impl;

import com.example.wms.entity.Product;
import com.example.wms.entity.MaterialCategory;
import com.example.wms.mapper.ProductMapper;
import com.example.wms.mapper.MaterialCategoryMapper;
import com.example.wms.service.ProductService;
import com.example.wms.utils.CacheKeyUtil;
import com.example.wms.utils.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private MaterialCategoryMapper categoryMapper;
    @Autowired
    private CacheManager cacheManager;

    @Override
    public List<Product> getAllProducts() {
        String cacheKey = CacheKeyUtil.getProductListKey();
        List<Product> products = cacheManager.get(cacheKey, List.class);
        if (products == null) {
            products = productMapper.selectAll();
            cacheManager.set(cacheKey, products, CacheKeyUtil.DEFAULT_EXPIRE_TIME);
        }
        return products;
    }

    @Override
    public List<Product> getProductsByCondition(Product product) {
        return productMapper.selectByCondition(product);
    }

    @Override
    public Product getProductById(Long id) {
        String cacheKey = CacheKeyUtil.getProductKey(id);
        Product product = cacheManager.get(cacheKey, Product.class);
        if (product == null) {
            product = productMapper.selectById(id);
            if (product != null) {
                cacheManager.set(cacheKey, product, CacheKeyUtil.DEFAULT_EXPIRE_TIME);
            }
        }
        return product;
    }

    @Override
    public Product getProductByFileHash(String fileHash) {
        String cacheKey = CacheKeyUtil.getProductByFileHashKey(fileHash);
        Product product = cacheManager.get(cacheKey, Product.class);
        if (product == null) {
            product = productMapper.selectByFileHash(fileHash);
            if (product != null) {
                cacheManager.set(cacheKey, product, CacheKeyUtil.DEFAULT_EXPIRE_TIME);
            }
        }
        return product;
    }

    @Override
    public int saveProduct(Product product) {
        if (productMapper.selectByCode(product.getProductCode()) != null) {
            throw new RuntimeException("商品编码已存在");
        }
        
        // 检查文件哈希值是否已存在
        if (product.getFileHash() != null && !product.getFileHash().isEmpty()) {
            Product existingProduct = productMapper.selectByFileHash(product.getFileHash());
            if (existingProduct != null) {
                throw new RuntimeException("该辅料图片已存在，无法重复添加");
            }
        }
        
        if (product.getStatus() == null) {
            product.setStatus(2);
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
        
        int result = productMapper.insert(product);
        
        // 清除缓存
        if (result > 0) {
            cacheManager.deletePattern(CacheKeyUtil.getPattern("product:"));
        }
        
        return result;
    }

    @Override
    public int updateProduct(Product product) {
        if (product.getStatus() != null && product.getStatus() == 2 && product.getPrice() != null && product.getPrice().compareTo(java.math.BigDecimal.ZERO) > 0) {
            product.setStatus(1);
        }
        
        int result = productMapper.update(product);
        
        // 清除缓存
        if (result > 0) {
            cacheManager.deletePattern(CacheKeyUtil.getPattern("product:"));
        }
        
        return result;
    }

    @Override
    public int deleteProduct(Long id) {
        int result = productMapper.deleteById(id);
        
        // 清除缓存
        if (result > 0) {
            cacheManager.deletePattern(CacheKeyUtil.getPattern("product:"));
        }
        
        return result;
    }
}




