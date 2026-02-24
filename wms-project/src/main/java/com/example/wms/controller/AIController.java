package com.example.wms.controller;

import com.example.wms.common.Result;
import com.example.wms.entity.Product;
import com.example.wms.service.MultimodalEmbeddingService;
import com.example.wms.service.MilvusService;
import com.example.wms.service.ProductService;
import com.example.wms.service.ZhipuAIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private ZhipuAIService zhipuAIService;

    @Autowired
    private ProductService productService;

    @Autowired
    private MultimodalEmbeddingService multimodalEmbeddingService;

    @Autowired
    private MilvusService milvusService;

    @PostMapping("/recognize")
    public Result<Map<String, Object>> recognizeMaterial(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            log.info("收到文件上传请求，文件名: {}", file.getOriginalFilename());
            log.info("文件大小: {} 字节", file.getSize());
            log.info("文件类型: {}", file.getContentType());

            // 1. AI识别辅料
            Map<String, Object> recognitionResult = zhipuAIService.recognizeMaterial(file);
            log.info("AI识别成功，返回结果: {}", recognitionResult);

            return Result.success("识别成功", recognitionResult);
        } catch (Exception e) {
            log.error("识别失败: {}", e.getMessage(), e);
            return Result.error("识别失败: " + e.getMessage());
        }
    }

    @PostMapping("/vectorize")
    public Result<String> vectorizeAndStore(@RequestParam("productId") Long productId, @RequestParam("file") MultipartFile file) {
        try {
            log.info("=== 向量化流程开始 ===");
            
            if (productId == null) {
                log.error("向量化请求失败: 辅料ID不能为空");
                return Result.error("辅料ID不能为空");
            }
            if (file == null || file.isEmpty()) {
                log.error("向量化请求失败: 文件不能为空");
                return Result.error("文件不能为空");
            }

            log.info("收到向量化请求");
            log.info("辅料ID: {}", productId);
            log.info("文件名: {}", file.getOriginalFilename());
            log.info("文件大小: {} 字节", file.getSize());
            log.info("文件类型: {}", file.getContentType());

            // 1. 获取辅料信息
            log.info("开始获取辅料信息...");
            Product product = productService.getProductById(productId);
            if (product == null) {
                log.error("向量化请求失败: 辅料不存在，ID: {}", productId);
                return Result.error("辅料不存在");
            }
            log.info("获取辅料信息成功");
            log.info("辅料名称: {}", product.getProductName());
            log.info("辅料分类: {}", product.getCategory());
            log.info("辅料类型: {}", product.getType());
            log.info("辅料材质: {}", product.getMaterial());
            log.info("辅料颜色: {}", product.getColor());
            log.info("辅料风格: {}", product.getStyle());
            log.info("辅料规格: {}", product.getSpecification());
            log.info("辅料单位: {}", product.getUnit());
            log.info("辅料价格: {}", product.getPrice());
            log.info("预计交付天数: {}", product.getExpectedDeliveryDays());
            log.info("产品编码: {}", product.getProductCode());
            log.info("辅料描述: {}", product.getDescription());

            // 2. 构建辅料信息Map
            log.info("开始构建辅料信息Map...");
            Map<String, Object> materialInfo = new HashMap<>();
            materialInfo.put("auxiliaryName", product.getProductName());
            materialInfo.put("category", product.getCategory());
            materialInfo.put("type", product.getType());
            materialInfo.put("material", product.getMaterial());
            materialInfo.put("color", product.getColor());
            materialInfo.put("style", product.getStyle());
            materialInfo.put("specification", product.getSpecification());
            materialInfo.put("unit", product.getUnit());
            materialInfo.put("price", product.getPrice());
            materialInfo.put("expectedDeliveryDays", product.getExpectedDeliveryDays());
            materialInfo.put("productCode", product.getProductCode());
            materialInfo.put("description", product.getDescription());
            log.info("构建辅料信息Map成功");
            log.debug("辅料信息Map: {}", materialInfo);

            // 3. 多模态向量化
            log.info("开始多模态向量化...");
            long vectorizeStartTime = System.currentTimeMillis();
            try {
                List<Float> vector = multimodalEmbeddingService.embedMaterial(materialInfo, file);
                long vectorizeEndTime = System.currentTimeMillis();
                log.info("多模态向量化完成，耗时: {} ms", vectorizeEndTime - vectorizeStartTime);
                log.info("多模态向量化成功，向量维度: {}", vector.size());
                if (vector.size() > 0) {
                    log.debug("向量前10个值: {}", vector.subList(0, Math.min(10, vector.size())));
                    log.debug("向量后10个值: {}", vector.subList(Math.max(0, vector.size() - 10), vector.size()));
                }

                // 4. 保存向量到Milvus
                log.info("开始保存向量到Milvus...");
                log.info("准备插入数据");
                log.info("Collection名称: materials");
                log.info("插入向量数量: 1");
                log.info("辅料ID: {}", productId);
                log.info("向量维度: {}", vector.size());
                log.info("向量前5个值: {}", vector.subList(0, Math.min(5, vector.size())));
                
                List<List<Float>> vectors = new ArrayList<>();
                vectors.add(vector);
                List<Long> ids = new ArrayList<>();
                ids.add(productId);
                
                long milvusStartTime = System.currentTimeMillis();
                try {
                    milvusService.insert("materials", null, vectors, ids);
                    long milvusEndTime = System.currentTimeMillis();
                    
                    log.info("向量保存到Milvus成功");
                    log.info("保存耗时: {} ms", milvusEndTime - milvusStartTime);
                    log.info("collection: materials");
                    log.info("辅料ID: {}", productId);
                    log.info("向量维度: {}", vector.size());
                } catch (Exception milvusException) {
                    log.error("保存向量到Milvus失败: {}", milvusException.getMessage(), milvusException);
                    throw new RuntimeException("保存向量到Milvus失败: " + milvusException.getMessage(), milvusException);
                }
            } catch (Exception vectorizeException) {
                log.error("多模态向量化失败: {}", vectorizeException.getMessage(), vectorizeException);
                throw new RuntimeException("多模态向量化失败: " + vectorizeException.getMessage(), vectorizeException);
            }
            
            log.info("=== 向量化流程完成 ===");

            return Result.success("向量化并存储成功");
        } catch (RuntimeException e) {
            // 处理运行时异常
            log.error("向量化运行时失败: {}", e.getMessage(), e);
            log.error("异常类型: {}", e.getClass().getName());
            String errorMessage = e.getMessage();
            if (errorMessage == null) {
                errorMessage = "未知运行时错误，请查看服务器日志获取详细信息";
                log.error("异常消息为null，使用默认错误信息");
            }
            return Result.error("向量化失败: " + errorMessage);
        } catch (Exception e) {
            // 处理其他异常
            log.error("向量化失败: {}", e.getMessage(), e);
            log.error("异常详情:", e);
            log.error("异常类型: {}", e.getClass().getName());
            String errorMessage = e.getMessage();
            if (errorMessage == null) {
                errorMessage = "未知错误，请查看服务器日志获取详细信息";
                log.error("异常消息为null，使用默认错误信息");
            }
            return Result.error("向量化失败: " + errorMessage);
        } finally {
            log.info("=== 向量化流程结束 ===");
        }
    }

    /**
     * 根据AI识别结果创建Product对象
     */
    private Product createProductFromRecognition(Map<String, Object> recognitionResult) {
        Product product = new Product();

        // 从识别结果中提取信息
        String name = (String) recognitionResult.getOrDefault("auxiliaryName", "未命名辅料");
        String category = (String) recognitionResult.getOrDefault("category", "辅料");
        String type = (String) recognitionResult.getOrDefault("type", "");
        String material = (String) recognitionResult.getOrDefault("material", "");
        String color = (String) recognitionResult.getOrDefault("color", "");
        String description = (String) recognitionResult.getOrDefault("description", "");

        // 设置产品信息
        product.setProductName(name);
        product.setCategory(category);
        product.setDescription(description);
        product.setStatus(2); // 默认为待审核状态
        product.setUnit("件");
        product.setPrice(BigDecimal.ZERO); // 默认为0

        // 生成产品编码
        String productCode = "MAT_" + System.currentTimeMillis();
        product.setProductCode(productCode);

        return product;
    }
}