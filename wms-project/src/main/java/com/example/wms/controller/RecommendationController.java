package com.example.wms.controller;

import com.example.wms.common.Result;
import com.example.wms.entity.Product;
import com.example.wms.service.MilvusService;
import com.example.wms.service.MultimodalEmbeddingService;
import com.example.wms.service.ProductService;
import com.example.wms.service.UserBehaviorService;
import io.milvus.param.MetricType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/product")
public class RecommendationController {

    @Autowired
    private MilvusService milvusService;

    @Autowired
    private ProductService productService;

    @Autowired
    private MultimodalEmbeddingService multimodalEmbeddingService;

    @Autowired
    private UserBehaviorService userBehaviorService;

    private static final String COLLECTION_NAME = "materials";
    private static final int VECTOR_DIMENSION = 2048; // 千问多模态向量维度

    /**
     * 基于辅料ID推荐相似辅料
     */
    @GetMapping("/recommend/{productId}")
    public Result<List<Product>> recommend(@PathVariable Long productId) {
        try {
            // 1. Get the target product to ensure it exists
            Product product = productService.getProductById(productId);
            if (product == null) {
                return Result.error("Product not found");
            }

            // 2. Retrieve the vector for this product from Milvus
            List<Float> targetVector = null;
            try {
                targetVector = milvusService.getVectorById(COLLECTION_NAME, productId);
            } catch (Exception e) {
                log.warn("Failed to get vector from Milvus: {}", e.getMessage());
            }

            List<Product> recommendedProducts = new ArrayList<>();

            if (targetVector != null && !targetVector.isEmpty()) {
                // 3. Search for similar vectors
                List<List<Float>> vectors = Collections.singletonList(targetVector);
                // Top 6 (include self to be safe, then filter)
                recommendedProducts = searchSimilarProducts(vectors, productId, 5);
            }

            // 4. Fallback if Milvus is empty or search failed (for Demo robustness)
            if (recommendedProducts.isEmpty()) {
                log.info("Milvus search returned no results, using fallback random strategy.");
                List<Product> allProducts = productService.getAllProducts();
                if (allProducts != null && !allProducts.isEmpty()) {
                    List<Product> candidates = new ArrayList<>(allProducts);
                    // Remove current product
                    candidates.removeIf(p -> p.getId().equals(productId));
                    Collections.shuffle(candidates);
                    recommendedProducts = candidates.subList(0, Math.min(candidates.size(), 5));
                }
            }

            return Result.success(recommendedProducts);

        } catch (Exception e) {
            log.error("Recommendation failed", e);
            // Return empty list instead of error to avoid breaking UI
            return Result.success(new ArrayList<>());
        }
    }

    /**
     * 基于图片推荐相似辅料
     */
    @PostMapping("/recommend-by-image")
    public Result<List<Product>> recommendByImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            // 1. 多模态向量化
            List<Float> imageVector = multimodalEmbeddingService.embedImage(file);
            log.info("Image vectorization successful, dimension: {}", imageVector.size());

            // 2. 搜索相似辅料
            List<List<Float>> vectors = Collections.singletonList(imageVector);
            List<Product> recommendedProducts = searchSimilarProducts(vectors, null, 5);

            // 3. 处理结果
            if (recommendedProducts.isEmpty()) {
                log.info("No similar products found for image");
                // 可以添加一些其他的推荐策略作为 fallback
            }

            return Result.success(recommendedProducts);

        } catch (Exception e) {
            log.error("Image-based recommendation failed: {}", e.getMessage(), e);
            return Result.error("推荐失败: " + e.getMessage());
        }
    }

    /**
     * 以图搜图 - 搜索相似辅料（支持参数配置）
     * @param file 上传的图片文件
     * @param limit 返回结果数量限制，默认 10
     * @param threshold 相似度阈值，默认 0.6（60%）
     * @return 相似辅料列表
     */
    @PostMapping("/search-by-image")
    public Result<List<Product>> searchByImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "threshold", defaultValue = "0.6") Double threshold) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            log.info("Starting image search, limit={}, threshold={}", limit, threshold);

            // 1. 多模态向量化
            List<Float> imageVector = multimodalEmbeddingService.embedImage(file);
            log.info("Image vectorization successful, dimension: {}", imageVector.size());

            // 2. 在 Milvus 中搜索相似向量
            List<List<Float>> queryVectors = Collections.singletonList(imageVector);
            List<Product> similarProducts = searchSimilarProducts(queryVectors, null, limit);
            
            // 3. 过滤掉相似度低于阈值的结果
            final float minSimilarity = threshold.floatValue();
            similarProducts.removeIf(p -> p.getSimilarity() == null || p.getSimilarity() < minSimilarity);
            
            log.info("Found {} similar products (after filtering with threshold {})", similarProducts.size(), threshold);
            return Result.success(similarProducts);

        } catch (Exception e) {
            log.error("Image search failed", e);
            return Result.error("图片搜索失败：" + e.getMessage());
        }
    }

    /**
     * 初始化Milvus集合
     */
    @PostMapping("/init-milvus")
    public Result<String> initMilvusCollection() {
        try {
            // 检查集合是否存在
            if (!milvusService.collectionExists(COLLECTION_NAME)) {
                // 创建集合
                milvusService.createCollection(COLLECTION_NAME, VECTOR_DIMENSION, MetricType.IP);
                log.info("Milvus collection {} created successfully", COLLECTION_NAME);
                return Result.success("Milvus collection created successfully");
            } else {
                log.info("Milvus collection {} already exists", COLLECTION_NAME);
                return Result.success("Milvus collection already exists");
            }
        } catch (Exception e) {
            log.error("Failed to initialize Milvus collection: {}", e.getMessage(), e);
            return Result.error("Failed to initialize Milvus collection: " + e.getMessage());
        }
    }

    /**
     * 搜索相似辅料
     */
    private List<Product> searchSimilarProducts(List<List<Float>> vectors, Long excludeProductId, int topK) {
        List<Product> recommendedProducts = new ArrayList<>();

        try {
            // 使用内积计算相似度，适合向量相似度
            Map<Long, Float> similarMaterials = milvusService.getSimilarMaterials(COLLECTION_NAME, vectors.get(0), topK + 1);

            for (Map.Entry<Long, Float> entry : similarMaterials.entrySet()) {
                Long materialId = entry.getKey();
                Float score = entry.getValue();

                // 跳过当前辅料
                if (excludeProductId != null && materialId.equals(excludeProductId)) {
                    continue;
                }

                // 获取辅料信息
                Product product = productService.getProductById(materialId);
                if (product != null) {
                    // 设置相似度分数
                    product.setSimilarity(score);
                    recommendedProducts.add(product);
                    log.debug("Added similar product: ID={}, Score={}, Name={}", 
                            materialId, score, product.getProductName());
                }

                // 达到目标数量后停止
                if (recommendedProducts.size() >= topK) {
                    break;
                }
            }

        } catch (Exception e) {
            log.error("Failed to search similar products: {}", e.getMessage(), e);
        }

        return recommendedProducts;
    }

    /**
     * 基于文本描述推荐相似辅料
     */
    @PostMapping("/recommend-by-text")
    public Result<List<Product>> recommendByText(@RequestParam("text") String text) {
        try {
            if (text == null || text.isEmpty()) {
                return Result.error("Text cannot be empty");
            }

            // 1. 文本向量化
            List<Float> textVector = multimodalEmbeddingService.embedText(text);
            log.info("Text vectorization successful, dimension: {}", textVector.size());

            // 2. 搜索相似辅料
            List<List<Float>> vectors = Collections.singletonList(textVector);
            List<Product> recommendedProducts = searchSimilarProducts(vectors, null, 5);

            // 3. 处理结果
            if (recommendedProducts.isEmpty()) {
                log.info("No similar products found for text: {}", text);
            }

            return Result.success(recommendedProducts);

        } catch (Exception e) {
            log.error("Text-based recommendation failed: {}", e.getMessage(), e);
            return Result.error("推荐失败: " + e.getMessage());
        }
    }

    /**
     * 基于多模态（文本+图片）推荐相似辅料
     */
    @PostMapping("/recommend-by-multimodal")
    public Result<List<Product>> recommendByMultimodal(
            @RequestParam("text") String text,
            @RequestParam("file") MultipartFile file) {
        try {
            if (text == null || text.isEmpty()) {
                return Result.error("Text cannot be empty");
            }
            if (file == null || file.isEmpty()) {
                return Result.error("File cannot be empty");
            }

            // 1. 多模态向量化
            List<Float> multimodalVector = multimodalEmbeddingService.embedMultimodal(text, file);
            log.info("Multimodal vectorization successful, dimension: {}", multimodalVector.size());

            // 2. 搜索相似辅料
            List<List<Float>> vectors = Collections.singletonList(multimodalVector);
            List<Product> recommendedProducts = searchSimilarProducts(vectors, null, 5);

            // 3. 处理结果
            if (recommendedProducts.isEmpty()) {
                log.info("No similar products found for multimodal input");
            }

            return Result.success(recommendedProducts);

        } catch (Exception e) {
            log.error("Multimodal recommendation failed", e);
            return Result.error("推荐失败: " + e.getMessage());
        }
    }

    /**
     * 基于协同过滤的个性化推荐
     * @param userId 用户ID
     * @return 推荐辅料列表
     */
    @GetMapping("/collaborative-recommend")
    public Result<List<Product>> collaborativeRecommend(@RequestParam Long userId) {
        try {
            log.info("Generating collaborative filtering recommendations for user: {}", userId);

            List<Product> recommendedProducts = new ArrayList<>();

            // 1. 基于协同过滤获取推荐
            List<Long> recommendedMaterialIds = userBehaviorService.getRecommendedMaterials(userId);

            if (recommendedMaterialIds != null && !recommendedMaterialIds.isEmpty()) {
                // 获取推荐辅料的详细信息
                for (Long materialId : recommendedMaterialIds) {
                    if (recommendedProducts.size() >= 10) break;
                    Product product = productService.getProductById(materialId);
                    if (product != null && product.getStatus() == 1) {
                        product.setSimilarity(1.0f);
                        recommendedProducts.add(product);
                    }
                }
            }

            // 2. 如果协同过滤结果不足，混合向量相似度推荐
            if (recommendedProducts.size() < 5) {
                List<Product> allProducts = productService.getAllProducts();
                if (allProducts != null && !allProducts.isEmpty()) {
                    Set<Long> existingIds = new HashSet<>();
                    for (Product p : recommendedProducts) {
                        existingIds.add(p.getId());
                    }

                    // 获取用户最近浏览/收藏的辅料
                    List<Long> userRecentMaterials = new ArrayList<>();
                    try {
                        List<Long> similarUsers = userBehaviorService.getSimilarUsers(userId);
                        if (!similarUsers.isEmpty()) {
                            userRecentMaterials = userBehaviorService.getRecommendedMaterials(userId);
                        }
                    } catch (Exception e) {
                        log.warn("Failed to get user behavior data: {}", e.getMessage());
                    }

                    // 随机补充推荐
                    List<Product> candidates = new ArrayList<>(allProducts);
                    candidates.removeIf(p -> existingIds.contains(p.getId()) || p.getStatus() != 1);
                    Collections.shuffle(candidates);

                    int needed = 5 - recommendedProducts.size();
                    for (Product p : candidates) {
                        if (needed <= 0) break;
                        p.setSimilarity(0.5f);
                        recommendedProducts.add(p);
                        needed--;
                    }
                }
            }

            log.info("Returning {} collaborative recommendations for user {}", recommendedProducts.size(), userId);
            return Result.success(recommendedProducts);

        } catch (Exception e) {
            log.error("Collaborative filtering recommendation failed", e);
            return Result.success(new ArrayList<>());
        }
    }

    /**
     * 记录用户行为
     * @param userId 用户ID
     * @param materialId 辅料ID
     * @param behaviorType 行为类型：browse, favorite, add_to_scheme, purchase
     * @return 操作结果
     */
    @PostMapping("/record-behavior")
    public Result<String> recordBehavior(
            @RequestParam Long userId,
            @RequestParam Long materialId,
            @RequestParam String behaviorType) {
        try {
            userBehaviorService.recordBehavior(userId, materialId, behaviorType);
            return Result.success("行为记录成功");
        } catch (Exception e) {
            log.error("Failed to record user behavior", e);
            return Result.error("记录失败: " + e.getMessage());
        }
    }
}
