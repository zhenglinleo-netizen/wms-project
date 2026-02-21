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
 * 基于千问多模态融合向量API
 */
@Slf4j
@Service
public class MultimodalEmbeddingServiceImpl implements MultimodalEmbeddingService {

    @Value("${dashscope.api-key}")
    private String apiKey;

    @Value("${dashscope.api-url}")
    private String apiUrl;

    @Value("${dashscope.model}")
    private String model;

    @Value("${dashscope.timeout}")
    private int timeout;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Float> embedText(String text) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        Map<String, Object> input = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();

        Map<String, Object> textContent = new HashMap<>();
        textContent.put("text", text);
        contents.add(textContent);

        input.put("contents", contents);
        requestBody.put("input", input);

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

        // 添加文本内容
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("text", text);
        contents.add(textContent);

        // 添加图片内容
        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("image", "data:image/" + imageType + ";base64," + base64);
        contents.add(imageContent);

        input.put("contents", contents);
        requestBody.put("input", input);

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
        textBuilder.append("辅料名称：").append(materialInfo.getOrDefault("auxiliaryName", materialInfo.getOrDefault("name", ""))).append("\n");
        textBuilder.append("辅料类别：").append(materialInfo.getOrDefault("category", "")).append("\n");
        textBuilder.append("具体类型：").append(materialInfo.getOrDefault("type", "")).append("\n");
        textBuilder.append("材质：").append(materialInfo.getOrDefault("material", "")).append("\n");
        textBuilder.append("颜色：").append(materialInfo.getOrDefault("color", "")).append("\n");
        textBuilder.append("风格：").append(materialInfo.getOrDefault("style", "")).append("\n");
        textBuilder.append("辅料类别：").append(materialInfo.getOrDefault("auxiliaryCategory", "")).append("\n");
        textBuilder.append("工艺大类：").append(materialInfo.getOrDefault("processCategory", "")).append("\n");
        textBuilder.append("材料层：").append(materialInfo.getOrDefault("materialLayer", "")).append("\n");
        textBuilder.append("效果层：").append(materialInfo.getOrDefault("effectLayer", "")).append("\n");
        textBuilder.append("适用阶段：").append(materialInfo.getOrDefault("applicationStage", "")).append("\n");
        textBuilder.append("详细描述：").append(materialInfo.getOrDefault("description", "")).append("\n");

        String text = textBuilder.toString();
        log.info("构建的辅料文本描述：{}", text);

        if (imageFile != null && !imageFile.isEmpty()) {
            // 多模态融合向量化
            return embedMultimodal(text, imageFile);
        } else {
            // 仅文本向量化
            return embedText(text);
        }
    }

    /**
     * 发送HTTP请求到千问API
     */
    private String sendHttpRequest(Map<String, Object> requestBody) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL requestUrl = new URL(apiUrl);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setDoOutput(true);

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                // 读取错误信息
                StringBuilder errorBuilder = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        errorBuilder.append(responseLine.trim());
                    }
                }
                String errorMessage = errorBuilder.toString();
                log.error("千问API请求失败，状态码: {}, 错误信息: {}", responseCode, errorMessage);
                throw new RuntimeException("千问API请求失败，状态码: " + responseCode + ", 错误信息: " + errorMessage);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            return response.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 解析千问API返回的向量数据
     */
    private List<Float> parseVectorResponse(String response) throws Exception {
        log.info("千问API返回原始响应：{}", response);
        JsonNode rootNode = objectMapper.readTree(response);
        
        // 检查是否是错误响应
        if (rootNode.has("error")) {
            JsonNode errorNode = rootNode.path("error");
            String errorMessage = errorNode.path("message").asText("未知错误");
            String errorCode = errorNode.path("code").asText("未知错误码");
            log.error("千问API返回错误，错误码: {}, 错误信息: {}", errorCode, errorMessage);
            throw new RuntimeException("千问API返回错误：" + errorMessage + " (错误码: " + errorCode + ")");
        }
        
        // 尝试不同的响应格式解析
        JsonNode embeddingNode = null;
        
        // 格式1: {"data": [{"embedding": [...]}]} (标准格式)
        JsonNode dataNode = rootNode.path("data");
        if (dataNode.isArray() && dataNode.size() > 0) {
            JsonNode firstItemNode = dataNode.get(0);
            embeddingNode = firstItemNode.path("embedding");
            log.info("尝试格式1解析，找到embedding字段: {}", embeddingNode.isArray());
        }
        
        // 格式2: {"embedding": [...]} (直接返回向量)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            embeddingNode = rootNode.path("embedding");
            log.info("尝试格式2解析，找到embedding字段: {}", embeddingNode.isArray());
        }
        
        // 格式3: {"result": {"embedding": [...]}} (结果嵌套)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            JsonNode resultNode = rootNode.path("result");
            if (resultNode.isObject()) {
                embeddingNode = resultNode.path("embedding");
                log.info("尝试格式3解析，找到embedding字段: {}", embeddingNode.isArray());
            }
        }
        
        // 格式4: {"output": {"embeddings": [...]}} (输出嵌套)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            JsonNode outputNode = rootNode.path("output");
            if (outputNode.isObject()) {
                embeddingNode = outputNode.path("embeddings");
                log.info("尝试格式4解析，找到embeddings字段: {}", embeddingNode.isArray());
                if (!embeddingNode.isArray()) {
                    embeddingNode = outputNode.path("embedding");
                    log.info("尝试格式4解析，找到embedding字段: {}", embeddingNode.isArray());
                }
            }
        }
        
        // 格式5: {"data": {"embedding": [...]}} (data对象格式)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            if (dataNode.isObject()) {
                embeddingNode = dataNode.path("embedding");
                log.info("尝试格式5解析，找到embedding字段: {}", embeddingNode.isArray());
            }
        }
        
        // 格式6: {"outputs": [{"embedding": [...]}]} (outputs数组格式)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            JsonNode outputsNode = rootNode.path("outputs");
            if (outputsNode.isArray() && outputsNode.size() > 0) {
                JsonNode firstOutputNode = outputsNode.get(0);
                embeddingNode = firstOutputNode.path("embedding");
                log.info("尝试格式6解析，找到embedding字段: {}", embeddingNode.isArray());
            }
        }
        
        // 格式7: {"response": {"embedding": [...]}} (response嵌套格式)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            JsonNode responseNode = rootNode.path("response");
            if (responseNode.isObject()) {
                embeddingNode = responseNode.path("embedding");
                log.info("尝试格式7解析，找到embedding字段: {}", embeddingNode.isArray());
            }
        }
        
        // 格式8: {"result": [{"embedding": [...]}]} (result数组格式)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            JsonNode resultArrayNode = rootNode.path("result");
            if (resultArrayNode.isArray() && resultArrayNode.size() > 0) {
                JsonNode firstResultNode = resultArrayNode.get(0);
                embeddingNode = firstResultNode.path("embedding");
                log.info("尝试格式8解析，找到embedding字段: {}", embeddingNode.isArray());
            }
        }
        
        // 格式9: {"vectors": [...]} (vectors直接数组格式)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            embeddingNode = rootNode.path("vectors");
            log.info("尝试格式9解析，找到vectors字段: {}", embeddingNode.isArray());
        }
        
        // 格式10: {"vector": [...]} (vector直接数组格式)
        if (embeddingNode == null || !embeddingNode.isArray()) {
            embeddingNode = rootNode.path("vector");
            log.info("尝试格式10解析，找到vector字段: {}", embeddingNode.isArray());
        }

        if (!embeddingNode.isArray()) {
            // 打印所有可能的字段，以便调试
            log.error("千问API返回格式详细信息:");
            log.error("所有字段: {}", rootNode.fieldNames());
            if (rootNode.has("data")) {
                log.error("data字段类型: {}", rootNode.path("data").getNodeType());
                log.error("data字段内容: {}", rootNode.path("data"));
            }
            if (rootNode.has("result")) {
                log.error("result字段类型: {}", rootNode.path("result").getNodeType());
                log.error("result字段内容: {}", rootNode.path("result"));
            }
            if (rootNode.has("output")) {
                log.error("output字段类型: {}", rootNode.path("output").getNodeType());
                log.error("output字段内容: {}", rootNode.path("output"));
            }
            if (rootNode.has("outputs")) {
                log.error("outputs字段类型: {}", rootNode.path("outputs").getNodeType());
                log.error("outputs字段内容: {}", rootNode.path("outputs"));
            }
            if (rootNode.has("response")) {
                log.error("response字段类型: {}", rootNode.path("response").getNodeType());
                log.error("response字段内容: {}", rootNode.path("response"));
            }
            if (rootNode.has("vectors")) {
                log.error("vectors字段类型: {}", rootNode.path("vectors").getNodeType());
                log.error("vectors字段内容: {}", rootNode.path("vectors"));
            }
            if (rootNode.has("vector")) {
                log.error("vector字段类型: {}", rootNode.path("vector").getNodeType());
                log.error("vector字段内容: {}", rootNode.path("vector"));
            }
            throw new RuntimeException("千问API返回格式异常，无法找到有效的embedding字段");
        }

        List<Float> vector = new ArrayList<>();
        for (JsonNode element : embeddingNode) {
            if (element.isDouble()) {
                vector.add((float) element.asDouble());
            } else if (element.isInt()) {
                vector.add((float) element.asInt());
            } else if (element.isFloat()) {
                vector.add(element.floatValue());
            } else if (element.isNumber()) {
                // 处理其他数字类型
                vector.add((float) element.asDouble());
            } else {
                log.warn("跳过非数字元素: {}", element.asText());
            }
        }

        if (vector.isEmpty()) {
            log.error("千问API返回空向量，embedding字段长度: {}", embeddingNode.size());
            throw new RuntimeException("千问API返回空向量");
        }

        log.info("成功获取多模态向量，维度: {}", vector.size());
        return vector;
    }
}
