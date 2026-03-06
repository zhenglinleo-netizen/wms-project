package com.example.wms.service.impl;

import com.example.wms.service.MultimodalEmbeddingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多模态向量化服务实现
 * 基于阿里云百炼平台 qwen3-vl-embedding 模型
 */
@Slf4j
@Service
public class MultimodalEmbeddingServiceImpl implements MultimodalEmbeddingService {

    @Value("${bailian.api-key}")
    private String apiKey;

    @Value("${bailian.api-url}")
    private String apiUrl;

    @Value("${bailian.model}")
    private String model;

    @Value("${bailian.timeout}")
    private int timeout;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Float> embedText(String text) throws Exception {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("文本内容不能为空");
        }
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        Map<String, Object> input = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();

        Map<String, Object> textContent = new HashMap<>();
        textContent.put("text", text);
        contents.add(textContent);

        input.put("contents", contents);
        requestBody.put("input", input);

        // 添加可选参数，指定向量维度（与Milvus集合维度保持一致）
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("dimension", 2048);
        parameters.put("output_type", "dense");
        requestBody.put("parameters", parameters);

        log.info("发送文本向量化请求，文本长度: {}", text.length());
        String response = sendHttpRequest(requestBody);
        return parseVectorResponse(response);
    }

    @Override
    public List<Float> embedImage(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("图片文件不能为空");
        }

        byte[] fileBytes = file.getBytes();
        String base64 = Base64.getEncoder().encodeToString(fileBytes);
        String contentType = file.getContentType();
        String imageType = contentType != null ? contentType.split("/")[1] : "jpg";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        Map<String, Object> input = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();

        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("image", "data:image/" + imageType + ";base64," + base64);
        contents.add(imageContent);

        input.put("contents", contents);
        requestBody.put("input", input);

        // 添加可选参数，指定向量维度（与Milvus集合维度保持一致）
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("dimension", 2048);
        parameters.put("output_type", "dense");
        requestBody.put("parameters", parameters);

        String response = sendHttpRequest(requestBody);
        return parseVectorResponse(response);
    }

    @Override
    public List<Float> embedMultimodal(String text, MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("图片文件不能为空");
        }

        byte[] fileBytes = file.getBytes();
        String base64 = Base64.getEncoder().encodeToString(fileBytes);
        String contentType = file.getContentType();
        String imageType = contentType != null ? contentType.split("/")[1] : "jpg";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        Map<String, Object> input = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();

        // DashScope API要求：将文本和图片放在同一个对象里以生成融合向量
        Map<String, Object> multimodalContent = new HashMap<>();
        if (text != null && !text.trim().isEmpty()) {
            multimodalContent.put("text", text);
        }
        multimodalContent.put("image", "data:image/" + imageType + ";base64," + base64);
        contents.add(multimodalContent);

        input.put("contents", contents);
        requestBody.put("input", input);
        
        // 添加可选参数，指定向量维度（与Milvus集合维度保持一致）
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("dimension", 2048);
        parameters.put("output_type", "dense");
        requestBody.put("parameters", parameters);

        log.info("发送多模态融合向量化请求（文本+图片融合为一个向量）");
        String response = sendHttpRequest(requestBody);
        return parseVectorResponse(response);
    }

    @Override
    public List<Float> embedMaterial(Map<String, Object> materialInfo, MultipartFile imageFile) throws Exception {
        if (materialInfo == null) {
            throw new IllegalArgumentException("辅料信息不能为空");
        }

        // 构建辅料文本描述
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("辅料名称：").append(getSafeValue(materialInfo, "auxiliaryName", materialInfo.getOrDefault("name", ""))).append("\n");
        textBuilder.append("辅料类别：").append(getSafeValue(materialInfo, "category", "")).append("\n");
        textBuilder.append("具体类型：").append(getSafeValue(materialInfo, "type", "")).append("\n");
        textBuilder.append("材质：").append(getSafeValue(materialInfo, "material", "")).append("\n");
        textBuilder.append("颜色：").append(getSafeValue(materialInfo, "color", "")).append("\n");
        textBuilder.append("风格：").append(getSafeValue(materialInfo, "style", "")).append("\n");
        textBuilder.append("规格：").append(getSafeValue(materialInfo, "specification", "")).append("\n");
        textBuilder.append("单位：").append(getSafeValue(materialInfo, "unit", "")).append("\n");
        textBuilder.append("价格：").append(getSafeValue(materialInfo, "price", "")).append("\n");
        textBuilder.append("预计交付天数：").append(getSafeValue(materialInfo, "expectedDeliveryDays", "")).append("\n");
        textBuilder.append("产品编码：").append(getSafeValue(materialInfo, "productCode", "")).append("\n");
        textBuilder.append("辅料类别：").append(getSafeValue(materialInfo, "auxiliaryCategory", "")).append("\n");
        textBuilder.append("工艺大类：").append(getSafeValue(materialInfo, "processCategory", "")).append("\n");
        textBuilder.append("材料层：").append(getSafeValue(materialInfo, "materialLayer", "")).append("\n");
        textBuilder.append("效果层：").append(getSafeValue(materialInfo, "effectLayer", "")).append("\n");
        textBuilder.append("适用阶段：").append(getSafeValue(materialInfo, "applicationStage", "")).append("\n");
        textBuilder.append("详细描述：").append(getSafeValue(materialInfo, "description", "")).append("\n");

        String text = textBuilder.toString();
        log.info("构建的辅料文本描述：{}", text);
        log.info("辅料信息Map大小：{}", materialInfo.size());
        log.info("辅料信息Map键集合：{}", materialInfo.keySet());

        if (imageFile != null && !imageFile.isEmpty()) {
            // 多模态融合向量化
            log.info("使用多模态融合向量化，图片大小：{} 字节", imageFile.getSize());
            return embedMultimodal(text, imageFile);
        } else {
            // 仅文本向量化
            log.info("使用仅文本向量化");
            return embedText(text);
        }
    }

    /**
     * 安全获取Map中的值，处理null情况
     */
    private Object getSafeValue(Map<String, Object> map, String key, Object defaultValue) {
        Object value = map.get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 发送HTTP请求到阿里云百炼API
     */
    private String sendHttpRequest(Map<String, Object> requestBody) throws Exception {
        HttpURLConnection connection = null;
        int maxRetries = 3;
        int retryDelay = 1000; // 1秒
        
        log.info("准备发送HTTP请求到阿里云百炼API");
        log.info("API URL: {}", apiUrl);
        log.info("API Key: {}...{}", apiKey.substring(0, Math.min(5, apiKey.length())), apiKey.substring(Math.max(0, apiKey.length() - 5)));
        log.info("请求方法: POST");
        log.info("超时设置: {}ms", timeout);
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                URL requestUrl = new URL(apiUrl);
                connection = (HttpURLConnection) requestUrl.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + apiKey);
                connection.setRequestProperty("Accept", "application/json");
                connection.setConnectTimeout(timeout);
                connection.setReadTimeout(timeout);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);

                String jsonBody = objectMapper.writeValueAsString(requestBody);
                log.debug("阿里云百炼API请求体: {}", jsonBody);
                log.info("请求体大小: {} 字节", jsonBody.length());

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                    os.flush();
                }

                int responseCode = connection.getResponseCode();
                log.info("阿里云百炼API响应状态码: {}", responseCode);
                
                // 打印所有响应头部
                java.util.Map<String, java.util.List<String>> headers = connection.getHeaderFields();
                log.info("响应头部信息:");
                for (java.util.Map.Entry<String, java.util.List<String>> entry : headers.entrySet()) {
                    String key = entry.getKey();
                    if (key != null) {
                        log.info("{}: {}", key, entry.getValue());
                    }
                }
                
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    // 读取错误信息
                    StringBuilder errorBuilder = new StringBuilder();
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            errorBuilder.append(responseLine.trim());
                        }
                    } catch (Exception e) {
                        log.warn("读取错误流失败: {}", e.getMessage());
                    }
                    String errorMessage = errorBuilder.toString();
                    log.error("阿里云百炼API请求失败，状态码: {}, 错误信息: {}", responseCode, errorMessage);
                    
                    // 特别处理认证错误
                    if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED || errorMessage.contains("100016")) {
                        log.error("API令牌认证失败！错误码: 100016");
                        log.error("可能的原因:");
                        log.error("1. API密钥不正确或已过期");
                        log.error("2. API密钥格式错误");
                        log.error("3. 认证头部设置不正确");
                        log.error("4. API服务端配置问题");
                        log.error("当前使用的API密钥: {}...{}", apiKey.substring(0, Math.min(5, apiKey.length())), apiKey.substring(Math.max(0, apiKey.length() - 5)));
                        log.error("当前使用的API URL: {}", apiUrl);
                        throw new RuntimeException("API令牌认证失败 (错误码: 100016)。请检查API密钥是否正确，是否已过期，或联系阿里云技术支持。");
                    }
                    
                    // 对于特定错误码，考虑重试
                    if (responseCode >= 500 && attempt < maxRetries) {
                        log.warn("阿里云百炼API返回服务器错误，尝试重试 ({}/{})...", attempt, maxRetries);
                        Thread.sleep(retryDelay);
                        retryDelay *= 2; // 指数退避
                        continue;
                    }
                    
                    throw new RuntimeException("阿里云百炼API请求失败，状态码: " + responseCode + ", 错误信息: " + errorMessage);
                }

                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }

                log.debug("阿里云百炼API响应: {}", response.toString());
                log.info("API请求成功，响应大小: {} 字节", response.length());
                return response.toString();
            } catch (Exception e) {
                log.error("阿里云百炼API请求异常 (尝试 {}): {}", attempt, e.getMessage());
                log.error("异常详情:", e);
                
                // 对于网络异常，考虑重试
                if ((e instanceof java.net.SocketTimeoutException || e instanceof java.net.ConnectException) && attempt < maxRetries) {
                    log.warn("网络异常，尝试重试 ({}/{})...", attempt, maxRetries);
                    Thread.sleep(retryDelay);
                    retryDelay *= 2; // 指数退避
                    continue;
                }
                
                throw e;
            } finally {
                if (connection != null) {
                    try {
                        connection.disconnect();
                    } catch (Exception e) {
                        log.warn("关闭连接失败: {}", e.getMessage());
                    }
                }
            }
        }
        
        // 如果所有重试都失败，抛出异常
        throw new RuntimeException("阿里云百炼API请求失败，已尝试最大重试次数");
    }

    /**
     * 深度搜索查找embedding字段
     * 递归遍历JSON树，查找所有可能的embedding相关字段
     */
    private JsonNode findEmbeddingField(JsonNode node) {
        if (node == null) {
            return null;
        }
        
        // 检查当前节点是否是embedding相关字段
        if (node.isArray()) {
            // 检查是否是数字数组（向量）
            boolean isNumericArray = true;
            for (JsonNode element : node) {
                if (!element.isNumber()) {
                    isNumericArray = false;
                    break;
                }
            }
            if (isNumericArray && node.size() > 0) {
                return node;
            }
        }
        
        // 检查当前节点的字段
        if (node.isObject()) {
            // 检查常见的embedding字段名
            String[] embeddingFieldNames = {
                "embedding", "embeddings", "vector", "vectors", "result", "data", "output", "response"
            };
            
            for (String fieldName : embeddingFieldNames) {
                if (node.has(fieldName)) {
                    JsonNode childNode = node.path(fieldName);
                    JsonNode result = findEmbeddingField(childNode);
                    if (result != null && result.isArray()) {
                        return result;
                    }
                }
            }
            
            // 遍历所有字段
            for (JsonNode childNode : node) {
                JsonNode result = findEmbeddingField(childNode);
                if (result != null && result.isArray()) {
                    return result;
                }
            }
        }
        
        return null;
    }

    /**
     * 解析阿里云百炼API返回的向量数据
     */
    private List<Float> parseVectorResponse(String response) throws Exception {
        if (response == null || response.trim().isEmpty()) {
            log.error("阿里云百炼API返回空响应");
            throw new RuntimeException("阿里云百炼API返回空响应");
        }
        
        log.info("阿里云百炼API返回原始响应：{}", response);
        JsonNode rootNode = objectMapper.readTree(response);
        
        // 检查是否是错误响应
        if (rootNode.has("error")) {
            JsonNode errorNode = rootNode.path("error");
            String errorMessage = errorNode.path("message").asText("未知错误");
            String errorCode = errorNode.path("code").asText("未知错误码");
            log.error("阿里云百炼API返回错误，错误码: {}, 错误信息: {}", errorCode, errorMessage);
            throw new RuntimeException("阿里云百炼API返回错误：" + errorMessage + " (错误码: " + errorCode + ")");
        }
        
        // 检查是否是阿里云百炼API错误响应格式
        if (rootNode.has("Code") && rootNode.has("Message") && rootNode.has("Success")) {
            String errorCode = rootNode.path("Code").asText("未知错误码");
            String errorMessage = rootNode.path("Message").asText("未知错误");
            boolean success = rootNode.path("Success").asBoolean(false);
            if (!success) {
                log.error("阿里云百炼API返回错误，错误码: {}, 错误信息: {}", errorCode, errorMessage);
                throw new RuntimeException("阿里云百炼API返回错误：" + errorMessage + " (错误码: " + errorCode + ")");
            }
        }
        
        // 检查是否是DashScope API错误响应格式: {"code": "...", "message": "..."}
        if (rootNode.has("code") && rootNode.has("message") && !rootNode.has("output")) {
            String errorCode = rootNode.path("code").asText("未知错误码");
            String errorMessage = rootNode.path("message").asText("未知错误");
            log.error("DashScope API返回错误，错误码: {}, 错误信息: {}", errorCode, errorMessage);
            throw new RuntimeException("DashScope API返回错误：" + errorMessage + " (错误码: " + errorCode + ")");
        }
        
        // 尝试不同的响应格式解析
        JsonNode embeddingNode = null;
        
        // 格式1: {"data": [{"embedding": [...]}]} (标准格式)
        JsonNode dataNode = rootNode.path("data");
        if (dataNode.isArray() && dataNode.size() > 0) {
            JsonNode firstItemNode = dataNode.get(0);
            embeddingNode = firstItemNode.path("embedding");
            log.info("尝试格式1解析，找到embedding字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
        }
        
        // 格式2: {"embedding": [...]} (直接返回向量)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            embeddingNode = rootNode.path("embedding");
            log.info("尝试格式2解析，找到embedding字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
        }
        
        // 格式3: {"result": {"embedding": [...]}} (结果嵌套)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            JsonNode resultNode = rootNode.path("result");
            if (resultNode.isObject()) {
                embeddingNode = resultNode.path("embedding");
                log.info("尝试格式3解析，找到embedding字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
            }
        }
        
        // 格式4: DashScope API格式 {"output": {"embeddings": [{"embedding": [...]}]}}
        if (embeddingNode == null || !embeddingNode.isArray()) {
            JsonNode outputNode = rootNode.path("output");
            if (outputNode.isObject()) {
                JsonNode embeddingsArray = outputNode.path("embeddings");
                if (embeddingsArray.isArray() && embeddingsArray.size() > 0) {
                    JsonNode firstEmbedding = embeddingsArray.get(0);
                    embeddingNode = firstEmbedding.path("embedding");
                    log.info("尝试格式4解析(DashScope)，找到embedding字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
                }
                if (!embeddingNode.isArray()) {
                    embeddingNode = outputNode.path("embedding");
                    log.info("尝试格式4解析，找到直接embedding字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
                }
            }
        }
        
        // 格式5: {"data": {"embedding": [...]}} (data对象格式)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            if (dataNode.isObject()) {
                embeddingNode = dataNode.path("embedding");
                log.info("尝试格式5解析，找到embedding字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
            }
        }
        
        // 格式6: {"outputs": [{"embedding": [...]}]} (outputs数组格式)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            JsonNode outputsNode = rootNode.path("outputs");
            if (outputsNode.isArray() && outputsNode.size() > 0) {
                JsonNode firstOutputNode = outputsNode.get(0);
                embeddingNode = firstOutputNode.path("embedding");
                log.info("尝试格式6解析，找到embedding字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
            }
        }
        
        // 格式7: {"response": {"embedding": [...]}} (response嵌套格式)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            JsonNode responseNode = rootNode.path("response");
            if (responseNode.isObject()) {
                embeddingNode = responseNode.path("embedding");
                log.info("尝试格式7解析，找到embedding字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
            }
        }
        
        // 格式8: {"result": [{"embedding": [...]}]} (result数组格式)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            JsonNode resultArrayNode = rootNode.path("result");
            if (resultArrayNode.isArray() && resultArrayNode.size() > 0) {
                JsonNode firstResultNode = resultArrayNode.get(0);
                embeddingNode = firstResultNode.path("embedding");
                log.info("尝试格式8解析，找到embedding字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
            }
        }
        
        // 格式9: {"vectors": [...]} (vectors直接数组格式)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            embeddingNode = rootNode.path("vectors");
            log.info("尝试格式9解析，找到vectors字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
        }
        
        // 格式11: {"data": {"embeddings": [...]}} (data对象包含embeddings数组)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            if (dataNode.isObject()) {
                embeddingNode = dataNode.path("embeddings");
                log.info("尝试格式11解析，找到embeddings字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
            }
        }
        
        // 格式12: {"result": {"data": {"embedding": [...]}}} (result.data格式)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            JsonNode resultNode = rootNode.path("result");
            if (resultNode.isObject()) {
                JsonNode resultDataNode = resultNode.path("data");
                if (resultDataNode.isObject()) {
                    embeddingNode = resultDataNode.path("embedding");
                    log.info("尝试格式12解析，找到embedding字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
                    if (!embeddingNode.isArray()) {
                        embeddingNode = resultDataNode.path("embeddings");
                        log.info("尝试格式12解析，找到embeddings字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
                    }
                }
            }
        }
        
        // 格式13: {"output": {"data": {"embedding": [...]}}} (output.data格式)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            JsonNode outputNode = rootNode.path("output");
            if (outputNode.isObject()) {
                JsonNode outputDataNode = outputNode.path("data");
                if (outputDataNode.isObject()) {
                    embeddingNode = outputDataNode.path("embedding");
                    log.info("尝试格式13解析，找到embedding字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
                    if (!embeddingNode.isArray()) {
                        embeddingNode = outputDataNode.path("embeddings");
                        log.info("尝试格式13解析，找到embeddings字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
                    }
                }
            }
        }
        
        // 格式14: {"response": {"data": {"embedding": [...]}}} (response.data格式)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            JsonNode responseNode = rootNode.path("response");
            if (responseNode.isObject()) {
                JsonNode responseDataNode = responseNode.path("data");
                if (responseDataNode.isObject()) {
                    embeddingNode = responseDataNode.path("embedding");
                    log.info("尝试格式14解析，找到embedding字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
                    if (!embeddingNode.isArray()) {
                        embeddingNode = responseDataNode.path("embeddings");
                        log.info("尝试格式14解析，找到embeddings字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
                    }
                }
            }
        }
        
        // 格式15: {"result": {"output": {"embedding": [...]}}} (result.output格式)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            JsonNode resultNode = rootNode.path("result");
            if (resultNode.isObject()) {
                JsonNode resultOutputNode = resultNode.path("output");
                if (resultOutputNode.isObject()) {
                    embeddingNode = resultOutputNode.path("embedding");
                    log.info("尝试格式15解析，找到embedding字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
                    if (!embeddingNode.isArray()) {
                        embeddingNode = resultOutputNode.path("embeddings");
                        log.info("尝试格式15解析，找到embeddings字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.isArray() ? embeddingNode.size() : 0);
                    }
                }
            }
        }
        
        // 格式16: 通用深度搜索 - 递归查找所有可能的embedding字段
        if (embeddingNode == null || !embeddingNode.isArray()) {
            embeddingNode = findEmbeddingField(rootNode);
            if (embeddingNode != null && embeddingNode.isArray()) {
                log.info("尝试格式16解析，通过深度搜索找到embedding字段: {}, 长度: {}", embeddingNode.isArray(), embeddingNode.size());
            }
        }

        if (embeddingNode == null || !embeddingNode.isArray()) {
            // 打印所有可能的字段，以便调试
            log.error("阿里云百炼API返回格式详细信息:");
            log.error("所有字段: {}", rootNode.fieldNames());
            if (rootNode.has("data")) {
                log.error("data字段类型: {}", rootNode.path("data").getNodeType());
                log.error("data字段内容: {}", rootNode.path("data"));
                if (rootNode.path("data").isObject()) {
                    log.error("data字段的所有子字段: {}", rootNode.path("data").fieldNames());
                }
            }
            if (rootNode.has("result")) {
                log.error("result字段类型: {}", rootNode.path("result").getNodeType());
                log.error("result字段内容: {}", rootNode.path("result"));
                if (rootNode.path("result").isObject()) {
                    log.error("result字段的所有子字段: {}", rootNode.path("result").fieldNames());
                }
            }
            if (rootNode.has("output")) {
                log.error("output字段类型: {}", rootNode.path("output").getNodeType());
                log.error("output字段内容: {}", rootNode.path("output"));
                if (rootNode.path("output").isObject()) {
                    log.error("output字段的所有子字段: {}", rootNode.path("output").fieldNames());
                }
            }
            if (rootNode.has("outputs")) {
                log.error("outputs字段类型: {}", rootNode.path("outputs").getNodeType());
                log.error("outputs字段内容: {}", rootNode.path("outputs"));
            }
            if (rootNode.has("response")) {
                log.error("response字段类型: {}", rootNode.path("response").getNodeType());
                log.error("response字段内容: {}", rootNode.path("response"));
                if (rootNode.path("response").isObject()) {
                    log.error("response字段的所有子字段: {}", rootNode.path("response").fieldNames());
                }
            }
            if (rootNode.has("vectors")) {
                log.error("vectors字段类型: {}", rootNode.path("vectors").getNodeType());
                log.error("vectors字段内容: {}", rootNode.path("vectors"));
            }
            if (rootNode.has("vector")) {
                log.error("vector字段类型: {}", rootNode.path("vector").getNodeType());
                log.error("vector字段内容: {}", rootNode.path("vector"));
            }
            
            // 打印完整的响应内容，便于调试
            log.error("完整的API响应: {}", response);
            
            throw new RuntimeException("阿里云百炼API返回格式异常，无法找到有效的embedding字段。请检查API响应格式是否正确，或联系技术支持。");
        }

        List<Float> vector = new ArrayList<>();
        int validElements = 0;
        int invalidElements = 0;
        
        for (JsonNode element : embeddingNode) {
            if (element.isDouble()) {
                vector.add((float) element.asDouble());
                validElements++;
            } else if (element.isInt()) {
                vector.add((float) element.asInt());
                validElements++;
            } else if (element.isFloat()) {
                vector.add(element.floatValue());
                validElements++;
            } else if (element.isNumber()) {
                // 处理其他数字类型
                vector.add((float) element.asDouble());
                validElements++;
            } else {
                log.warn("跳过非数字元素: {}", element.asText());
                invalidElements++;
            }
        }

        log.info("向量解析统计: 总元素数: {}, 有效元素数: {}, 无效元素数: {}", 
                embeddingNode.size(), validElements, invalidElements);

        if (vector.isEmpty()) {
            log.error("阿里云百炼API返回空向量，embedding字段长度: {}, 有效元素数: {}, 无效元素数: {}", 
                    embeddingNode.size(), validElements, invalidElements);
            throw new RuntimeException("阿里云百炼API返回空向量，可能原因: 1. 输入内容无效 2. API响应格式异常 3. 向量计算失败");
        }

        log.info("成功获取多模态向量，维度: {}", vector.size());
        return vector;
    }
}
