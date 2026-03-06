package com.example.wms.scheduler;

import com.example.wms.entity.Product;
import com.example.wms.mapper.ProductMapper;
import com.example.wms.service.MinioService;
import com.example.wms.service.MilvusService;
import com.example.wms.utils.CacheManager;
import com.example.wms.utils.CacheKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 回收站自动清理调度器
 * 定期清理回收站中超过指定天数的辅料
 */
@Component
public class RecycleCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(RecycleCleanupScheduler.class);

    @Autowired
    private ProductMapper productMapper;

    @Autowired(required = false)
    private MinioService minioService;

    @Autowired(required = false)
    private MilvusService milvusService;

    @Autowired(required = false)
    private CacheManager cacheManager;

    // 自动清理天数：30天
    private static final int AUTO_CLEANUP_DAYS = 30;

    /**
     * 每天凌晨2点执行清理任务
     * 清理回收站中超过30天的辅料
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void cleanupRecycleBin() {
        logger.info("开始执行回收站自动清理任务...");

        try {
            // 获取所有已删除的辅料
            List<Product> deletedProducts = productMapper.selectDeleted();
            logger.info("当前回收站中有 {} 个已删除辅料", deletedProducts.size());

            int cleanupCount = 0;
            int errorCount = 0;

            for (Product product : deletedProducts) {
                // 检查删除时间是否超过指定天数
                if (isExpired(product.getUpdateTime())) {
                    try {
                        // 永久删除辅料
                        cleanupProduct(product);
                        cleanupCount++;
                    } catch (Exception e) {
                        logger.error("清理辅料失败: ID={}, 名称={}", product.getId(), product.getProductName(), e);
                        errorCount++;
                    }
                }
            }

            // 清除缓存
            if (cleanupCount > 0 && cacheManager != null) {
                cacheManager.deletePattern(CacheKeyUtil.getPattern("product:"));
                logger.info("已清除产品相关缓存");
            }

            logger.info("回收站自动清理任务完成: 清理成功 {} 个，失败 {} 个", cleanupCount, errorCount);
        } catch (Exception e) {
            logger.error("回收站自动清理任务执行失败", e);
        }
    }

    /**
     * 检查辅料是否已过期
     * @param updateTime 更新时间（删除时间）
     * @return 是否过期
     */
    private boolean isExpired(LocalDateTime updateTime) {
        if (updateTime == null) {
            return true;
        }
        LocalDateTime now = LocalDateTime.now();
        long daysBetween = ChronoUnit.DAYS.between(updateTime, now);
        return daysBetween >= AUTO_CLEANUP_DAYS;
    }

    /**
     * 清理单个辅料
     * @param product 辅料信息
     */
    private void cleanupProduct(Product product) {
        if (minioService != null) {
            deleteProductImages(product);
        }

        if (milvusService != null) {
            try {
                milvusService.deleteById("materials", product.getId());
                logger.info("删除Milvus向量数据成功: ID={}", product.getId());
            } catch (Exception e) {
                logger.warn("删除Milvus向量数据失败: {}", e.getMessage());
            }
        }

        int result = productMapper.deleteById(product.getId());
        if (result > 0) {
                logger.info("成功清理辅料: ID={}, 名称={}", product.getId(), product.getProductName());
            } else {
                logger.warn("清理辅料失败: ID={}, 名称={}", product.getId(), product.getProductName());
            }
    }

    private void deleteProductImages(Product product) {
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            try {
                String imageUrl = product.getImageUrl();
                String filename = imageUrl;
                if (imageUrl.contains("/")) {
                    filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                }
                minioService.deleteFile(filename);
                logger.debug("删除主图成功: {}", filename);
            } catch (Exception e) {
                logger.warn("删除主图失败: {}", e.getMessage());
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
                                logger.debug("删除多图成功: {}", filename);
                            } catch (Exception e) {
                                logger.warn("删除多图失败: {}", e.getMessage());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("处理多图删除失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 手动触发清理任务（用于测试）
     */
    public void triggerCleanup() {
        cleanupRecycleBin();
    }

    /**
     * 获取自动清理天数配置
     * @return 自动清理天数
     */
    public int getAutoCleanupDays() {
        return AUTO_CLEANUP_DAYS;
    }
}
