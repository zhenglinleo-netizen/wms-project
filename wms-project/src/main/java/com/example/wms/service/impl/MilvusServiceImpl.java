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
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(collectionName)
                .withMetricType(metricType)
                .withTopK(topK)
                .withVectors(vectors)
                .withVectorFieldName("embedding")
                .withOutFields(Collections.singletonList("id"))
                .withParams("{\"nprobe\":10}")
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
            // 定义id字段
            FieldType idField = FieldType.newBuilder()
                    .withName("id")
                    .withDataType(DataType.Int64)
                    .withPrimaryKey(true)
                    .withAutoID(false)
                    .build();

            // 定义向量字段
            FieldType vectorField = FieldType.newBuilder()
                    .withName("embedding")
                    .withDataType(DataType.FloatVector)
                    .withDimension(dimension)
                    .build();

            // 创建collection参数
            CreateCollectionParam createCollectionParam = CreateCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withDescription("Material vectors collection")
                    .withShardsNum(2)
                    .addFieldType(idField)
                    .addFieldType(vectorField)
                    .withConsistencyLevel(ConsistencyLevelEnum.EVENTUALLY)
                    .build();

            R<io.milvus.param.RpcStatus> response = milvusClient.createCollection(createCollectionParam);
            if (response.getStatus() != R.Status.Success.getCode()) {
                log.error("Failed to create collection {}: {}", collectionName, response.getMessage());
                throw new RuntimeException("Failed to create collection: " + response.getMessage());
            }

            log.info("Successfully created collection {} with dimension {}", collectionName, dimension);
        } catch (io.milvus.exception.MilvusException e) {
            log.error("Milvus error creating collection {}: {}", 
                    collectionName, e.getMessage(), e);
            throw new RuntimeException("Milvus error creating collection: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error creating collection {}: {}", collectionName, e.getMessage(), e);
            throw new RuntimeException("Error creating collection: " + e.getMessage(), e);
        }
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
                    .withParams("{\"nprobe\":10}")
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

            log.info("Found {} similar materials in collection {}", materialScores.size(), collectionName);
        } catch (Exception e) {
            log.error("Error searching similar materials: {}", e.getMessage(), e);
        }

        return materialScores;
    }
}
