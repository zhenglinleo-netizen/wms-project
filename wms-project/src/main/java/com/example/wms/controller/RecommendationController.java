package com.example.wms.controller;

import com.example.wms.common.Result;
import com.example.wms.entity.Product;
import com.example.wms.service.MilvusService;
import com.example.wms.service.MultimodalEmbeddingService;
import com.example.wms.service.ProductService;
import io.milvus.param.MetricType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
            log.error("Multimodal recommendation failed: {}", e.getMessage(), e);
            return Result.error("推荐失败: " + e.getMessage());
        }
    }
}
