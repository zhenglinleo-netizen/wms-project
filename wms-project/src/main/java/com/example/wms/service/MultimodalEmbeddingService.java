package com.example.wms.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

/**
 * 多模态向量化服务接口
 * 基于千问多模态融合向量API实现
 */
public interface MultimodalEmbeddingService {

    /**
     * 文本向量化
     * @param text 文本内容
     * @return 向量表示（Float列表）
     * @throws Exception 向量化异常
     */
    List<Float> embedText(String text) throws Exception;

    /**
     * 图片向量化
     * @param file 图片文件
     * @return 向量表示（Float列表）
     * @throws Exception 向量化异常
     */
    List<Float> embedImage(MultipartFile file) throws Exception;

    /**
     * 多模态融合向量化（文本+图片）
     * @param text 文本内容
     * @param file 图片文件
     * @return 向量表示（Float列表）
     * @throws Exception 向量化异常
     */
    List<Float> embedMultimodal(String text, MultipartFile file) throws Exception;

    /**
     * 基于辅料信息进行向量化
     * @param materialInfo 辅料信息（包含名称、描述、分类等）
     * @param imageFile 辅料图片
     * @return 向量表示（Float列表）
     * @throws Exception 向量化异常
     */
    List<Float> embedMaterial(Map<String, Object> materialInfo, MultipartFile imageFile) throws Exception;
}
