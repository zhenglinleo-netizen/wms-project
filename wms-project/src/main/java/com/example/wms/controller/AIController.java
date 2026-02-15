package com.example.wms.controller;

import com.example.wms.common.Result;
import com.example.wms.service.ZhipuAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private ZhipuAIService zhipuAIService;

    @PostMapping("/recognize")
    public Result<Map<String, Object>> recognizeMaterial(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            System.out.println("收到文件上传请求，文件名: " + file.getOriginalFilename());
            System.out.println("文件大小: " + file.getSize() + " 字节");
            System.out.println("文件类型: " + file.getContentType());

            Map<String, Object> result = zhipuAIService.recognizeMaterial(file);
            System.out.println("AI识别成功，返回结果: " + result);
            return Result.success("识别成功", result);
        } catch (Exception e) {
            System.err.println("识别失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("识别失败: " + e.getMessage());
        }
    }
}