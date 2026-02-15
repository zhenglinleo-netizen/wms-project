package com.example.wms.service;

import org.springframework.web.multipart.MultipartFile;

public interface MinioService {

    /**
     * 确保存储桶存在
     */
    void ensureBucketExists() throws Exception;

    /**
     * 上传文件到MinIO
     */
    String uploadFile(MultipartFile file) throws Exception;

    /**
     * 计算文件哈希值
     */
    String calculateFileHash(MultipartFile file) throws Exception;

    /**
     * 删除MinIO中的文件
     */
    void deleteFile(String filename) throws Exception;

    /**
     * 从MinIO获取文件
     */
    byte[] getFile(String filename) throws Exception;
}
