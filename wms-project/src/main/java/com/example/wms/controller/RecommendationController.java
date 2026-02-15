package com.example.wms.controller;

import com.example.wms.common.Result;
import com.example.wms.entity.Product;
import com.example.wms.service.MilvusService;
import com.example.wms.service.ProductService;
import io.milvus.grpc.SearchResults;
import io.milvus.response.SearchResultsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/product")
public class RecommendationController {

    @Autowired
    private MilvusService milvusService;

    @Autowired
    private ProductService productService;

    private static final String COLLECTION_NAME = "product_embedding";

    @GetMapping("/recommend/{productId}")
    public Result<List<Product>> recommend(@PathVariable Long productId) {
        try {
            // 1. Get the target product to ensure it exists
            Product product = productService.getProductById(productId);
            if (product == null) {
                return Result.error("Product not found");
            }

            // 2. Retrieve the vector for this product from Milvus
            // This assumes the Python ETL script has populated Milvus with embeddings for this product ID
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
                SearchResults results = milvusService.search(COLLECTION_NAME, vectors, 6);
                
                SearchResultsWrapper wrapper = new SearchResultsWrapper(results.getResults());
                List<SearchResultsWrapper.IDScore> scores = wrapper.getIDScore(0);
                
                for (SearchResultsWrapper.IDScore score : scores) {
                    Long id = score.getLongID();
                    if (id.equals(productId)) continue; // Skip self
                    
                    Product p = productService.getProductById(id);
                    if (p != null) {
                        // Optionally set similarity score if Product entity has a transient field
                        // p.setSimilarity(score.getScore());
                        recommendedProducts.add(p);
                    }
                }
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
}
