package com.example.wms.service.impl;

import com.example.wms.service.MilvusService;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.SearchResults;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.dml.QueryParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.param.MetricType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class MilvusServiceImpl implements MilvusService {

    @Autowired
    private MilvusServiceClient milvusClient;

    @Override
    public SearchResults search(String collectionName, List<List<Float>> vectors, int topK) {
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(collectionName)
                .withMetricType(MetricType.L2) // Or IP, COSINE based on your training
                .withTopK(topK)
                .withVectors(vectors)
                .withVectorFieldName("embedding") // Assuming the field name is 'embedding'
                .withParams("{\"nprobe\":10}")
                .build();

        io.milvus.param.R<SearchResults> response = milvusClient.search(searchParam);
        if (response.getStatus() != io.milvus.param.R.Status.Success.getCode()) {
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

        io.milvus.param.R<io.milvus.grpc.QueryResults> response = milvusClient.query(queryParam);
        if (response.getStatus() != io.milvus.param.R.Status.Success.getCode()) {
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
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field("id", ids));
        fields.add(new InsertParam.Field("embedding", vectors));

        InsertParam.Builder builder = InsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withFields(fields);
        
        if (partitionName != null && !partitionName.isEmpty()) {
            builder.withPartitionName(partitionName);
        }

        io.milvus.param.R<io.milvus.grpc.MutationResult> response = milvusClient.insert(builder.build());
        if (response.getStatus() != io.milvus.param.R.Status.Success.getCode()) {
            log.error("Milvus insert failed: {}", response.getMessage());
            throw new RuntimeException("Milvus insert failed: " + response.getMessage());
        }
    }
}
