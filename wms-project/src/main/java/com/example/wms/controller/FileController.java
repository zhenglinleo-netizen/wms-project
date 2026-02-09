package com.example.wms.controller;

import com.example.wms.common.Result;
import com.example.wms.service.MinioService;
import com.example.wms.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private MinioService minioService;
    
    @Autowired
    private ProductService productService;

    /**
     * 上传单个文件
     */
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            // 上传文件到MinIO
            String fileUrl = minioService.uploadFile(file);

            return Result.success("上传成功", fileUrl);
        } catch (Exception e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传多个文件
     */
    @PostMapping("/upload-multiple")
    public Result<List<String>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            if (files == null || files.length == 0) {
                return Result.error("文件不能为空");
            }

            List<String> fileUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    // 上传文件到MinIO
                    String fileUrl = minioService.uploadFile(file);
                    fileUrls.add(fileUrl);
                }
            }

            return Result.success("上传成功", fileUrls);
        } catch (Exception e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/delete")
    public Result<?> deleteFile(@RequestParam("filename") String filename) {
        try {
            if (filename == null || filename.isEmpty()) {
                return Result.error("文件路径不能为空");
            }

            // 从MinIO删除文件
            minioService.deleteFile(filename);

            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查文件是否已存在（通过哈希值）
     */
    @PostMapping("/checkExists")
    public Result<Boolean> checkFileExists(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            // 计算文件哈希值
            String fileHash = minioService.calculateFileHash(file);
            
            // 检查是否已存在相同哈希值的辅料
            boolean exists = productService.getProductByFileHash(fileHash) != null;

            return Result.success(exists ? "文件已存在" : "文件不存在", exists);
        } catch (Exception e) {
            return Result.error("检查失败: " + e.getMessage());
        }
    }

    /**
     * 获取图片文件
     */
    @GetMapping("/get-image")
    public ResponseEntity<byte[]> getImage(@RequestParam("filename") String filename) {
        try {
            // 从MinIO获取文件
            byte[] fileBytes = minioService.getFile(filename);
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(fileBytes.length);
            // 返回响应
            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
