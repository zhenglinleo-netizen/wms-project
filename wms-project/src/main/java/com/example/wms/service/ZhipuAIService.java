package com.example.wms.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

public interface ZhipuAIService {
    
    Map<String, Object> recognizeMaterial(MultipartFile file) throws Exception;
}