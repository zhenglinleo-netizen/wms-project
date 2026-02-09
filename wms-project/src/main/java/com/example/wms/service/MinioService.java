package com.example.wms.service;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.UUID;

@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * 确保存储桶存在
     */
    public void ensureBucketExists() throws Exception {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (MinioException e) {
            throw new Exception("创建存储桶失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件到MinIO
     */
    public String uploadFile(MultipartFile file) throws Exception {
        ensureBucketExists();

        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 生成简化路径：UUID+扩展名
            String filename = UUID.randomUUID() + extension;

            // 获取文件输入流
            InputStream inputStream = file.getInputStream();

            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 关闭输入流
            inputStream.close();

            // 返回文件访问URL
            return endpoint + "/" + bucketName + "/" + filename;
        } catch (MinioException e) {
            throw new Exception("上传文件失败: " + e.getMessage());
        } catch (IOException e) {
            throw new Exception("读取文件失败: " + e.getMessage());
        }
    }

    /**
     * 计算文件哈希值
     */
    public String calculateFileHash(MultipartFile file) throws Exception {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            InputStream inputStream = file.getInputStream();
            byte[] buffer = new byte[8192];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
            inputStream.close();
            
            byte[] hash = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new Exception("计算文件哈希值失败: " + e.getMessage());
        }
    }

    /**
     * 删除MinIO中的文件
     */
    public void deleteFile(String filename) throws Exception {
        try {
            // 从URL中提取文件路径
            if (filename.startsWith(endpoint)) {
                filename = filename.replace(endpoint + "/" + bucketName + "/", "");
            }

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build()
            );
        } catch (MinioException e) {
            throw new Exception("删除文件失败: " + e.getMessage());
        }
    }

    /**
     * 从MinIO获取文件
     */
    public byte[] getFile(String filename) throws Exception {
        try {
            System.out.println("原始文件名: " + filename);
            
            // 直接使用文件名，不需要从URL中提取
            System.out.println("处理后的文件名: " + filename);

            // 获取对象
            GetObjectArgs args = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .build();

            // 读取文件内容到字节数组
            try (InputStream inputStream = minioClient.getObject(args);
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                System.out.println("成功获取MinIO对象输入流");
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("成功读取文件内容，总字节数: " + outputStream.size());
                return outputStream.toByteArray();
            }
        } catch (MinioException e) {
            System.out.println("MinIO异常: " + e.getMessage());
            throw new Exception("获取文件失败: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO异常: " + e.getMessage());
            throw new Exception("写入文件失败: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("其他异常: " + e.getMessage());
            throw e;
        }
    }
}
