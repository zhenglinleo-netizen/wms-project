package com.example.wms.service.impl;

import com.example.wms.entity.Product;
import com.example.wms.entity.MaterialCategory;
import com.example.wms.entity.MaterialType;
import com.example.wms.mapper.ProductMapper;
import com.example.wms.mapper.MaterialCategoryMapper;
import com.example.wms.mapper.MaterialTypeMapper;
import com.example.wms.service.ProductService;
import com.example.wms.service.MinioService;
import com.example.wms.service.MilvusService;
import com.example.wms.utils.CacheKeyUtil;
import com.example.wms.utils.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = Logger.getLogger(ProductServiceImpl.class.getName());

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private MaterialCategoryMapper categoryMapper;
    @Autowired
    private MaterialTypeMapper materialTypeMapper;
    @Autowired(required = false)
    private CacheManager cacheManager;
    @Autowired(required = false)
    private MinioService minioService;
    @Autowired(required = false)
    private MilvusService milvusService;

    @Override
    public List<Product> getAllProducts() {
        if (cacheManager != null) {
            String cacheKey = CacheKeyUtil.getProductListKey();
            List<Product> products = cacheManager.get(cacheKey, List.class);
            if (products != null) {
                return products;
            }
        }
        List<Product> products = productMapper.selectAll();
        // 确保返回非 null 列表
        if (products == null) {
            products = new ArrayList<>();
        }
        if (cacheManager != null) {
            String cacheKey = CacheKeyUtil.getProductListKey();
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
        if (cacheManager != null) {
            String cacheKey = CacheKeyUtil.getProductKey(id);
            Product product = cacheManager.get(cacheKey, Product.class);
            if (product != null) {
                return product;
            }
        }
        Product product = productMapper.selectById(id);
        if (product != null && cacheManager != null) {
            String cacheKey = CacheKeyUtil.getProductKey(id);
            cacheManager.set(cacheKey, product, CacheKeyUtil.DEFAULT_EXPIRE_TIME);
        }
        return product;
    }

    @Override
    public Product getProductByFileHash(String fileHash) {
        if (cacheManager != null) {
            String cacheKey = CacheKeyUtil.getProductByFileHashKey(fileHash);
            Product product = cacheManager.get(cacheKey, Product.class);
            if (product != null) {
                return product;
            }
        }
        Product product = productMapper.selectByFileHash(fileHash);
        if (product != null && cacheManager != null) {
            String cacheKey = CacheKeyUtil.getProductByFileHashKey(fileHash);
            cacheManager.set(cacheKey, product, CacheKeyUtil.DEFAULT_EXPIRE_TIME);
        }
        return product;
    }

    @Override
    public int saveProduct(Product product) {
        if (productMapper.selectByCode(product.getProductCode()) != null) {
            throw new RuntimeException("商品编码已存在");
        }
        
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
        
        if (product.getMaterial() != null && !product.getMaterial().isEmpty()) {
            MaterialType materialType = materialTypeMapper.selectByName(product.getMaterial());
            if (materialType == null) {
                MaterialType newMaterialType = new MaterialType();
                newMaterialType.setTypeName(product.getMaterial());
                newMaterialType.setDescription(null);
                materialTypeMapper.insert(newMaterialType);
            }
        }
        
        int result = productMapper.insert(product);
        
        if (result > 0 && cacheManager != null) {
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
        
        // 更新库存信息
        if (product.getStock() != null) {
            productMapper.updateInventory(product);
        }
        
        if (result > 0 && cacheManager != null) {
            cacheManager.deletePattern(CacheKeyUtil.getPattern("product:"));
        }
        
        return result;
    }

    @Override
    public int deleteProduct(Long id) {
        Product product = productMapper.selectById(id);
        
        Product updateProduct = new Product();
        updateProduct.setId(id);
        updateProduct.setStatus(-1);
        int result = productMapper.update(updateProduct);
        
        if (result > 0) {
            if (cacheManager != null) {
                cacheManager.deletePattern(CacheKeyUtil.getPattern("product:"));
            }
            logger.info("软删除辅料成功: ID=" + id);
        }
        
        return result;
    }

    @Override
    public List<Product> getDeletedProducts() {
        return productMapper.selectDeleted();
    }

    @Override
    public int restoreProduct(Long id) {
        Product updateProduct = new Product();
        updateProduct.setId(id);
        updateProduct.setStatus(2);
        int result = productMapper.update(updateProduct);
        
        if (result > 0) {
            if (cacheManager != null) {
                cacheManager.deletePattern(CacheKeyUtil.getPattern("product:"));
            }
            logger.info("恢复辅料成功: ID=" + id);
        }
        
        return result;
    }

    @Override
    public int permanentlyDeleteProduct(Long id) {
        Product product = productMapper.selectById(id);
        
        int result = productMapper.deleteById(id);
        
        if (result > 0) {
            if (cacheManager != null) {
                cacheManager.deletePattern(CacheKeyUtil.getPattern("product:"));
            }
            
            if (product != null && minioService != null) {
                if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                    try {
                        String imageUrl = product.getImageUrl();
                        String filename = imageUrl;
                        if (imageUrl.contains("/")) {
                            filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                        }
                        minioService.deleteFile(filename);
                        logger.info("删除主图成功: " + filename);
                    } catch (Exception e) {
                        logger.warning("删除主图失败: " + e.getMessage());
                    }
                }
                
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    try {
                        String imagesJson = product.getImages();
                        if (imagesJson.startsWith("[")) {
                            String[] imageUrls = imagesJson.substring(1, imagesJson.length() - 1).split(",");
                            for (String imageUrlStr : imageUrls) {
                                String imageUrl = imageUrlStr.trim().replace("\"", "");
                                if (!imageUrl.isEmpty()) {
                                    try {
                                        String filename = imageUrl;
                                        if (imageUrl.contains("/")) {
                                            filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                                        }
                                        minioService.deleteFile(filename);
                                        logger.info("删除多图成功: " + filename);
                                    } catch (Exception e) {
                                        logger.warning("删除多图失败: " + e.getMessage());
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warning("处理多图删除失败: " + e.getMessage());
                    }
                }
                
                if (milvusService != null) {
                    try {
                        milvusService.deleteById("materials", id);
                        logger.info("删除Milvus向量数据成功: ID=" + id);
                    } catch (Exception e) {
                        logger.warning("删除Milvus向量数据失败: " + e.getMessage());
                    }
                }
            }
            logger.info("永久删除辅料成功: ID=" + id);
        }
        
        return result;
    }
}




