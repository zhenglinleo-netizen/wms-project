package com.example.wms.service.impl;

import com.example.wms.service.MilvusService;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.grpc.SearchResults;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.QueryParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.QueryResultsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MilvusServiceImpl implements MilvusService {

    @Autowired
    private MilvusServiceClient milvusClient;

    @Override
    public SearchResults search(String collectionName, List<List<Float>> vectors, int topK) {
        return search(collectionName, vectors, topK, MetricType.IP);
    }

    @Override
    public SearchResults search(String collectionName, List<List<Float>> vectors, int topK, MetricType metricType) {
        return search(collectionName, vectors, topK, metricType, 10); // 默认nprobe=10
    }
    
    /**
     * 带自定义nprobe参数的搜索方法
     */
    public SearchResults search(String collectionName, List<List<Float>> vectors, int topK, MetricType metricType, int nprobe) {
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(collectionName)
                .withMetricType(metricType)
                .withTopK(topK)
                .withVectors(vectors)
                .withVectorFieldName("embedding")
                .withOutFields(Collections.singletonList("id"))
                .withParams(getOptimizedSearchParams(nprobe))
                .build();

        R<SearchResults> response = milvusClient.search(searchParam);
        if (response.getStatus() != R.Status.Success.getCode()) {
            log.error("Milvus search failed: {}", response.getMessage());
            throw new RuntimeException("Milvus search failed: " + response.getMessage());
        }
        return response.getData();
    }

    @Override
    public List<Float> getVectorById(String collectionName, Long id) {
        QueryParam queryParam = QueryParam.newBuilder()
                .withCollectionName(collectionName)
                .withExpr("id == " + id)
                .withOutFields(Collections.singletonList("embedding"))
                .build();

        R<io.milvus.grpc.QueryResults> response = milvusClient.query(queryParam);
        if (response.getStatus() != R.Status.Success.getCode()) {
            log.error("Milvus query failed: {}", response.getMessage());
            throw new RuntimeException("Milvus query failed: " + response.getMessage());
        }

        QueryResultsWrapper wrapper = new QueryResultsWrapper(response.getData());
        List<QueryResultsWrapper.RowRecord> records = wrapper.getRowRecords();
        if (records.isEmpty()) {
            return null;
        }
        return (List<Float>) records.get(0).get("embedding");
    }

    @Override
    public void insert(String collectionName, String partitionName, List<List<Float>> vectors, List<Long> ids) {
        // 自动创建collection（如果不存在）
        if (!collectionExists(collectionName)) {
            log.info("Collection {} does not exist, creating it...", collectionName);
            // 获取向量维度
            int dimension = vectors.isEmpty() ? 2048 : vectors.get(0).size();
            createCollection(collectionName, dimension, MetricType.IP);
        }

        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field("id", ids));
        fields.add(new InsertParam.Field("embedding", vectors));

        InsertParam.Builder builder = InsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withFields(fields);
        
        if (partitionName != null && !partitionName.isEmpty()) {
            builder.withPartitionName(partitionName);
        }

        R<io.milvus.grpc.MutationResult> response = milvusClient.insert(builder.build());
        if (response.getStatus() != R.Status.Success.getCode()) {
            log.error("Milvus insert failed: {}", response.getMessage());
            throw new RuntimeException("Milvus insert failed: " + response.getMessage());
        }
        
        log.info("Successfully inserted {} vectors into collection {}", ids.size(), collectionName);
    }

    @Override
    public void createCollection(String collectionName, int dimension, MetricType metricType) {
        try {
            log.info("开始创建Milvus集合...");
            log.info("集合名称: {}", collectionName);
            log.info("向量维度: {}", dimension);
            log.info("度量类型: {}", metricType);
            
            // 定义id字段
            log.info("定义ID字段...");
            FieldType idField = FieldType.newBuilder()
                    .withName("id")
                    .withDataType(DataType.Int64)
                    .withPrimaryKey(true)
                    .withAutoID(false)
                    .build();
            log.info("ID字段定义完成");

            // 定义向量字段
            log.info("定义向量字段...");
            FieldType vectorField = FieldType.newBuilder()
                    .withName("embedding")
                    .withDataType(DataType.FloatVector)
                    .withDimension(dimension)
                    .build();
            log.info("向量字段定义完成");

            // 创建collection参数
            log.info("构建创建集合参数...");
            CreateCollectionParam createCollectionParam = CreateCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withDescription("Material vectors collection")
                    .withShardsNum(2)
                    .addFieldType(idField)
                    .addFieldType(vectorField)
                    .withConsistencyLevel(ConsistencyLevelEnum.EVENTUALLY)
                    .build();
            log.info("创建集合参数构建完成");

            log.info("调用Milvus客户端创建集合...");
            R<io.milvus.param.RpcStatus> response = milvusClient.createCollection(createCollectionParam);
            log.info("Milvus创建集合响应状态: {}", response.getStatus());
            log.info("Milvus创建集合响应消息: {}", response.getMessage());
            
            if (response.getStatus() != R.Status.Success.getCode()) {
                log.error("创建集合失败 {}: {}", collectionName, response.getMessage());
                throw new RuntimeException("Failed to create collection: " + response.getMessage());
            }

            log.info("成功创建集合 {}，维度: {}", collectionName, dimension);
            
            // 创建索引以优化搜索性能
            log.info("开始创建索引...");
            createIndex(collectionName, metricType);
            log.info("索引创建完成");
        } catch (io.milvus.exception.MilvusException e) {
            log.error("Milvus异常创建集合 {}: {}", 
                    collectionName, e.getMessage(), e);
            throw new RuntimeException("Milvus error creating collection: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("创建集合时异常 {}: {}", collectionName, e.getMessage(), e);
            throw new RuntimeException("Error creating collection: " + e.getMessage(), e);
        }
    }
    
    /**
     * 创建索引以优化搜索性能
     */
    public void createIndex(String collectionName, MetricType metricType) {
        try {
            // 索引参数配置
            io.milvus.param.index.CreateIndexParam createIndexParam = io.milvus.param.index.CreateIndexParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withFieldName("embedding")
                    .withIndexType(io.milvus.param.IndexType.IVF_FLAT)
                    .withMetricType(metricType)
                    .withExtraParam("{\"nlist\": 128}")
                    .build();
            
            R<io.milvus.param.RpcStatus> response = milvusClient.createIndex(createIndexParam);
            if (response.getStatus() != R.Status.Success.getCode()) {
                log.error("Failed to create index for collection {}: {}", collectionName, response.getMessage());
                throw new RuntimeException("Failed to create index: " + response.getMessage());
            }
            
            log.info("Successfully created IVF_FLAT index for collection {}", collectionName);
            
            // 加载集合到内存以提高查询性能
            loadCollection(collectionName);
        } catch (Exception e) {
            log.error("Error creating index for collection {}: {}", collectionName, e.getMessage(), e);
            // 索引创建失败不影响集合使用，只记录错误
        }
    }
    
    /**
     * 加载集合到内存
     */
    public void loadCollection(String collectionName) {
        try {
            io.milvus.param.collection.LoadCollectionParam loadParam = io.milvus.param.collection.LoadCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .build();
            
            R<io.milvus.param.RpcStatus> response = milvusClient.loadCollection(loadParam);
            if (response.getStatus() != R.Status.Success.getCode()) {
                log.warn("Failed to load collection {}: {}", collectionName, response.getMessage());
                return;
            }
            
            log.info("Successfully loaded collection {} into memory", collectionName);
        } catch (Exception e) {
            log.warn("Error loading collection {}: {}", collectionName, e.getMessage());
            // 加载失败不影响集合使用，只记录警告
        }
    }
    
    /**
     * 优化搜索参数
     */
    public String getOptimizedSearchParams(int nprobe) {
        return String.format("{\"nprobe\": %d}", nprobe);
    }

    @Override
    public boolean collectionExists(String collectionName) {
        try {
            HasCollectionParam hasCollectionParam = HasCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .build();

            R<Boolean> response = milvusClient.hasCollection(hasCollectionParam);
            if (response.getStatus() != R.Status.Success.getCode()) {
                log.warn("Failed to check collection existence: {}. Collection may not exist or Milvus is not connected.", 
                        response.getMessage());
                return false;
            }

            boolean exists = response.getData();
            log.debug("Collection {} exists: {}", collectionName, exists);
            return exists;
        } catch (Exception e) {
            log.warn("Error checking collection existence: {}. Milvus may not be available.", e.getMessage());
            return false;
        }
    }

    @Override
    public Map<Long, Float> getSimilarMaterials(String collectionName, List<Float> vector, int topK) {
        return getSimilarMaterials(collectionName, vector, topK, 10); // 默认nprobe=10
    }
    
    /**
     * 带自定义nprobe参数的相似辅料搜索方法
     */
    public Map<Long, Float> getSimilarMaterials(String collectionName, List<Float> vector, int topK, int nprobe) {
        Map<Long, Float> materialScores = new HashMap<>();

        try {
            // 检查collection是否存在
            if (!collectionExists(collectionName)) {
                log.warn("Collection {} does not exist, returning empty results", collectionName);
                return materialScores;
            }

            // 构建搜索参数
            List<List<Float>> vectors = Collections.singletonList(vector);
            SearchParam searchParam = SearchParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withMetricType(MetricType.IP)
                    .withTopK(topK)
                    .withVectors(vectors)
                    .withVectorFieldName("embedding")
                    .withOutFields(Collections.singletonList("id"))
                    .withParams(getOptimizedSearchParams(nprobe))
                    .build();

            R<SearchResults> response = milvusClient.search(searchParam);
            if (response.getStatus() != R.Status.Success.getCode()) {
                log.error("Milvus search failed: {}", response.getMessage());
                return materialScores;
            }

            // 解析搜索结果 - 使用SearchResults直接获取数据
            SearchResults searchResults = response.getData();
            if (searchResults != null) {
                // 获取搜索结果
                io.milvus.grpc.SearchResultData results = searchResults.getResults();
                
                if (results != null) {
                    // 获取ID和分数
                    io.milvus.grpc.IDs ids = results.getIds();
                    if (ids != null && ids.hasIntId()) {
                        List<Long> idList = ids.getIntId().getDataList();
                        List<Float> scores = results.getScoresList();
                        
                        int resultSize = Math.min(idList.size(), scores.size());
                        for (int i = 0; i < resultSize; i++) {
                            long materialId = idList.get(i);
                            float similarity = scores.get(i);
                            materialScores.put(materialId, similarity);
                            log.debug("Found similar material: ID={}, Score={}", materialId, similarity);
                        }
                    }
                }
            }

            log.info("Found {} similar materials in collection {} with nprobe={}", materialScores.size(), collectionName, nprobe);
        } catch (Exception e) {
            log.error("Error searching similar materials: {}", e.getMessage(), e);
        }

        return materialScores;
    }
}
