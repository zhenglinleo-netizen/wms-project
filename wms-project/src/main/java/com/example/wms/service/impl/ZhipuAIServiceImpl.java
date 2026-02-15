package com.example.wms.service.impl;

import com.example.wms.service.ZhipuAIService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ZhipuAIServiceImpl implements ZhipuAIService {

    @Value("${zhipu.api-key}")
    private String apiKey;

    @Value("${zhipu.api-url}")
    private String apiUrl;

    @Value("${zhipu.model}")
    private String model;

    @Value("${zhipu.timeout:30000}")
    private int timeout;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> recognizeMaterial(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        byte[] fileBytes = file.getBytes();
        String base64 = Base64.getEncoder().encodeToString(fileBytes);
        String contentType = file.getContentType();
        String imageType = contentType != null ? contentType.split("/")[1] : "jpg";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        List<Map<String, Object>> messages = new ArrayList<>();

        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一个专业的辅料识别专家，请根据图片识别辅料并提取信息。识别结果需要包含：类别（面料/辅料/扣件）、具体类型（如拉链、纽扣、线、衬里等）、材质、颜色、风格、辅料类别、辅料名称（格式为：具体类型--材质--颜色）、工艺大类、材料层、效果层、适用阶段、描述（详细描述辅料的特点、用途、适用场景等）。");
        messages.add(systemMessage);

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");

        List<Map<String, Object>> content = new ArrayList<>();

        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", "请识别图片中的辅料并提取以下信息：类别、具体类型、材质、颜色、风格、辅料类别、辅料名称（格式为：具体类型--材质--颜色）、工艺大类、材料层、效果层、适用阶段、详细描述。");
        content.add(textContent);

        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("type", "image_url");
        Map<String, String> imageUrl = new HashMap<>();
        imageUrl.put("url", "data:image/" + imageType + ";base64," + base64);
        imageContent.put("image_url", imageUrl);
        content.add(imageContent);

        userMessage.put("content", content);
        messages.add(userMessage);

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2048);

        String response = sendHttpRequest(apiUrl, apiKey, requestBody);

        return parseResponse(response);
    }

    private String sendHttpRequest(String url, String apiKey, Map<String, Object> requestBody) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL requestUrl = new URL(url);
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
                throw new RuntimeException("API请求失败，状态码: " + responseCode);
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

    private Map<String, Object> parseResponse(String response) throws Exception {
        JsonNode rootNode = objectMapper.readTree(response);
        JsonNode choicesNode = rootNode.path("choices");

        if (choicesNode.isArray() && choicesNode.size() > 0) {
            JsonNode messageNode = choicesNode.get(0).path("message");
            String content = messageNode.path("content").asText();

            return parseRecognitionResult(content);
        } else {
            throw new RuntimeException("API返回格式异常，无法解析识别结果");
        }
    }

    private Map<String, Object> parseRecognitionResult(String content) {
        Map<String, Object> result = new HashMap<>();

        String category = "未识别";
        String type = "未识别";
        String material = "未识别";
        String color = "未识别";
        String style = "未识别";
        String auxiliaryCategory = "未识别";
        String auxiliaryName = "未识别--未识别--未识别";
        String processCategory = "未识别";
        String materialLayer = "未识别";
        String effectLayer = "未识别";
        String applicationStage = "未识别";
        String description = "未识别";

        List<Map.Entry<String, String>> patterns = new ArrayList<>();
        patterns.add(new AbstractMap.SimpleEntry<>("###\\s*辅料类别\\s*\\n([\\s\\S]*?)(?=\\n###|$)", "auxiliaryCategory"));
        patterns.add(new AbstractMap.SimpleEntry<>("###\\s*辅料名称\\s*\\n([\\s\\S]*?)(?=\\n###|$)", "auxiliaryName"));
        patterns.add(new AbstractMap.SimpleEntry<>("###\\s*工艺大类\\s*\\n([\\s\\S]*?)(?=\\n###|$)", "processCategory"));
        patterns.add(new AbstractMap.SimpleEntry<>("###\\s*材料层\\s*\\n([\\s\\S]*?)(?=\\n###|$)", "materialLayer"));
        patterns.add(new AbstractMap.SimpleEntry<>("###\\s*效果层\\s*\\n([\\s\\S]*?)(?=\\n###|$)", "effectLayer"));
        patterns.add(new AbstractMap.SimpleEntry<>("###\\s*适用阶段\\s*\\n([\\s\\S]*?)(?=\\n###|$)", "applicationStage"));
        patterns.add(new AbstractMap.SimpleEntry<>("###\\s*详细描述\\s*\\n([\\s\\S]*?)(?=\\n###|$)", "description"));
        patterns.add(new AbstractMap.SimpleEntry<>("###\\s*风格\\s*\\n([\\s\\S]*?)(?=\\n###|$)", "style"));
        patterns.add(new AbstractMap.SimpleEntry<>("类别[：:]\\s*([^\\n]+)", "category"));
        patterns.add(new AbstractMap.SimpleEntry<>("具体类型[：:]\\s*([^\\n]+)", "type"));
        patterns.add(new AbstractMap.SimpleEntry<>("材质[：:]\\s*([^\\n]+)", "material"));
        patterns.add(new AbstractMap.SimpleEntry<>("颜色[：:]\\s*([^\\n]+)", "color"));
        patterns.add(new AbstractMap.SimpleEntry<>("风格[：:]\\s*([^\\n]+)", "style"));
        patterns.add(new AbstractMap.SimpleEntry<>("辅料类别[：:]\\s*([^\\n]+)", "auxiliaryCategory"));
        patterns.add(new AbstractMap.SimpleEntry<>("辅料名称[：:]\\s*([^\\n]+)", "auxiliaryName"));
        patterns.add(new AbstractMap.SimpleEntry<>("工艺大类[：:]\\s*([^\\n]+)", "processCategory"));
        patterns.add(new AbstractMap.SimpleEntry<>("材料层[：:]\\s*([^\\n]+)", "materialLayer"));
        patterns.add(new AbstractMap.SimpleEntry<>("效果层[：:]\\s*([^\\n]+)", "effectLayer"));
        patterns.add(new AbstractMap.SimpleEntry<>("适用阶段[：:]\\s*([^\\n]+)", "applicationStage"));
        patterns.add(new AbstractMap.SimpleEntry<>("描述[：:]\\s*([\\s\\S]+)", "description"));

        for (Map.Entry<String, String> entry : patterns) {
            Pattern pattern = Pattern.compile(entry.getKey());
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                String value = matcher.group(1).trim().replaceAll("\\s+", " ");
                String field = entry.getValue();

                switch (field) {
                    case "category":
                        category = value;
                        break;
                    case "type":
                        type = value;
                        break;
                    case "material":
                        material = value;
                        break;
                    case "color":
                        color = value;
                        break;
                    case "style":
                        style = value;
                        break;
                    case "auxiliaryCategory":
                        auxiliaryCategory = value;
                        break;
                    case "auxiliaryName":
                        auxiliaryName = value;
                        break;
                    case "processCategory":
                        processCategory = value;
                        break;
                    case "materialLayer":
                        materialLayer = value;
                        break;
                    case "effectLayer":
                        effectLayer = value;
                        break;
                    case "applicationStage":
                        applicationStage = value;
                        break;
                    case "description":
                        description = value;
                        break;
                }
            }
        }

        if (!"未识别--未识别--未识别".equals(auxiliaryName)) {
            String[] parts = auxiliaryName.split("--");
            if (parts.length >= 3) {
                type = parts[0].trim();
                material = parts[1].trim();
                color = parts[2].trim();
            }
        }

        if ("未识别".equals(category)) {
            category = "辅料";
        }

        if ("未识别".equals(type) && !"未识别--未识别--未识别".equals(auxiliaryName)) {
            type = auxiliaryName.split("--")[0];
        }

        if ("未识别".equals(material) && !"未识别--未识别--未识别".equals(auxiliaryName)) {
            material = auxiliaryName.split("--")[1];
        }

        if ("未识别".equals(color) && !"未识别--未识别--未识别".equals(auxiliaryName)) {
            color = auxiliaryName.split("--")[2];
        }

        if ("未识别--未识别--未识别".equals(auxiliaryName)) {
            auxiliaryName = type + "--" + material.split("（")[0] + "--" + color;
        }

        if ("未识别".equals(description)) {
            description = "这是一款" + color + "的" + type + "，采用" + material + "制成，风格" + style + "。";
        }

        result.put("category", category);
        result.put("type", type);
        result.put("material", material);
        result.put("color", color);
        result.put("style", style);
        result.put("auxiliaryCategory", auxiliaryCategory);
        result.put("auxiliaryName", auxiliaryName);
        result.put("processCategory", processCategory);
        result.put("materialLayer", materialLayer);
        result.put("effectLayer", effectLayer);
        result.put("applicationStage", applicationStage);
        result.put("description", description);
        result.put("confidence", 0.95);

        List<Map<String, Object>> similar = new ArrayList<>();
        Map<String, Object> similar1 = new HashMap<>();
        similar1.put("id", 101);
        similar1.put("name", "相似面料 A");
        similar1.put("price", 20);
        similar1.put("similarity", 0.9);
        similar.add(similar1);

        Map<String, Object> similar2 = new HashMap<>();
        similar2.put("id", 102);
        similar2.put("name", "相似面料 B");
        similar2.put("price", 22);
        similar2.put("similarity", 0.85);
        similar.add(similar2);

        result.put("similar", similar);

        return result;
    }
}