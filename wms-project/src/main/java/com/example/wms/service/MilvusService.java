package com.example.wms.service;

import io.milvus.grpc.SearchResults;
import io.milvus.param.MetricType;

import java.util.List;

public interface MilvusService {
    
    /**
     * Search for similar vectors in Milvus
     * @param collectionName The name of the collection
     * @param vectors The target vectors to search for
     * @param topK Number of nearest neighbors to return
     * @return SearchResults
     */
    SearchResults search(String collectionName, List<List<Float>> vectors, int topK);

    List<Float> getVectorById(String collectionName, Long id);
    
    /**
     * Insert vectors into Milvus
     * @param collectionName The name of the collection
     * @param partitionName The name of the partition (optional)
     * @param vectors The vectors to insert
     * @param ids The corresponding IDs
     */
    void insert(String collectionName, String partitionName, List<List<Float>> vectors, List<Long> ids);
}
