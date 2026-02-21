package com.example.wms.component;

import com.example.wms.service.MilvusService;
import io.milvus.param.MetricType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Milvus初始化组件
 * 在应用启动时自动检查并创建Milvus collection
 */
@Slf4j
@Component
@Order(2) // 在MilvusClientConfig之后执行
public class MilvusInitializer implements ApplicationRunner {

    @Autowired
    private MilvusService milvusService;

    private static final String COLLECTION_NAME = "materials";
    private static final int VECTOR_DIMENSION = 2048; // 千问多模态向量维度

    @Override
    public void run(ApplicationArguments args) {
        log.info("Initializing Milvus collection...");
        
        try {
            // 检查collection是否存在
            boolean exists = milvusService.collectionExists(COLLECTION_NAME);
            if (!exists) {
                log.info("Collection {} does not exist, creating it...", COLLECTION_NAME);
                
                // 创建collection
                milvusService.createCollection(COLLECTION_NAME, VECTOR_DIMENSION, MetricType.IP);
                
                log.info("Successfully created Milvus collection: {}", COLLECTION_NAME);
            } else {
                log.info("Milvus collection {} already exists", COLLECTION_NAME);
            }
        } catch (Exception e) {
            log.error("Failed to initialize Milvus collection: {}", e.getMessage(), e);
            log.warn("Milvus initialization failed, but application will continue to start. " +
                    "Vectorization features may not work properly.");
            // 不抛出异常，允许应用继续启动
        }
    }
}
