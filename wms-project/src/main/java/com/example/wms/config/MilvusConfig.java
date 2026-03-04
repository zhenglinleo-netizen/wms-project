package com.example.wms.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class MilvusConfig {

    private static final Logger logger = LoggerFactory.getLogger(MilvusConfig.class);

    @Value("${milvus.host}")
    private String host;

    @Value("${milvus.port}")
    private Integer port;

    @Bean
    public MilvusServiceClient milvusServiceClient() {
        try {
            ConnectParam connectParam = ConnectParam.newBuilder()
                    .withHost(host)
                    .withPort(port)
                    .build();
            
            MilvusServiceClient client = new MilvusServiceClient(connectParam);
            logger.info("Milvus client initialized successfully");
            return client;
        } catch (Exception e) {
            logger.warn("Milvus service not available, application will continue without Milvus: {}", e.getMessage());
            // 返回null，让Spring处理
            return null;
        }
    }
}
