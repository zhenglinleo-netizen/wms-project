package com.example.wms.service.impl;

import com.example.wms.entity.Product;
import com.example.wms.entity.MaterialCategory;
import com.example.wms.mapper.ProductMapper;
import com.example.wms.mapper.MaterialCategoryMapper;
import com.example.wms.service.ProductService;
import com.example.wms.service.MinioService;
import com.example.wms.service.MilvusService;
import com.example.wms.utils.CacheKeyUtil;
import com.example.wms.utils.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    private CacheManager cacheManager;
    @Autowired
    private MinioService minioService;
    @Autowired
    private MilvusService milvusService;

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
        // 先获取产品信息
        Product product = productMapper.selectById(id);
        
        // 软删除：更新状态为删除状态
        Product updateProduct = new Product();
        updateProduct.setId(id);
        updateProduct.setStatus(-1); // -1表示已删除
        int result = productMapper.update(updateProduct);
        
        // 清除缓存
        if (result > 0) {
            cacheManager.deletePattern(CacheKeyUtil.getPattern("product:"));
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
        // 恢复辅料：更新状态为默认状态
        Product updateProduct = new Product();
        updateProduct.setId(id);
        updateProduct.setStatus(2); // 2表示待审核状态
        int result = productMapper.update(updateProduct);
        
        // 清除缓存
        if (result > 0) {
            cacheManager.deletePattern(CacheKeyUtil.getPattern("product:"));
            logger.info("恢复辅料成功: ID=" + id);
        }
        
        return result;
    }

    @Override
    public int permanentlyDeleteProduct(Long id) {
        // 先获取产品信息，用于后续删除图片
        Product product = productMapper.selectById(id);
        
        // 永久删除：从数据库中删除
        int result = productMapper.deleteById(id);
        
        // 清除缓存
        if (result > 0) {
            cacheManager.deletePattern(CacheKeyUtil.getPattern("product:"));
            
            // 删除相关图片
            if (product != null) {
                // 删除主图
                if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                    try {
                        // 提取文件名
                        String imageUrl = product.getImageUrl();
                        String filename = imageUrl;
                        if (imageUrl.contains("/")) {
                            filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                        }
                        minioService.deleteFile(filename);
                        logger.info("删除主图成功: " + filename);
                    } catch (Exception e) {
                        logger.warning("删除主图失败: " + e.getMessage());
                        // 图片删除失败不影响产品删除
                    }
                }
                
                // 删除多图
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    try {
                        // 假设images是JSON数组格式
                        String imagesJson = product.getImages();
                        if (imagesJson.startsWith("[")) {
                            // 简单处理，提取所有文件名
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
                                        // 图片删除失败不影响其他操作
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warning("处理多图删除失败: " + e.getMessage());
                        // 图片删除失败不影响产品删除
                    }
                }
                
                // 删除Milvus中的向量数据
                try {
                    milvusService.deleteById("materials", id);
                    logger.info("删除Milvus向量数据成功: ID=" + id);
                } catch (Exception e) {
                    logger.warning("删除Milvus向量数据失败: " + e.getMessage());
                    // Milvus删除失败不影响产品删除
                }
            }
            logger.info("永久删除辅料成功: ID=" + id);
        }
        
        return result;
    }
}




