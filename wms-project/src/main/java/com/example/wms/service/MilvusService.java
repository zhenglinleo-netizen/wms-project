package com.example.wms.service;

import io.milvus.grpc.SearchResults;
import io.milvus.param.MetricType;

import java.util.List;
import java.util.Map;

public interface MilvusService {
    
    /**
     * Search for similar vectors in Milvus
     * @param collectionName The name of the collection
     * @param vectors The target vectors to search for
     * @param topK Number of nearest neighbors to return
     * @return SearchResults
     */
    SearchResults search(String collectionName, List<List<Float>> vectors, int topK);

    /**
     * Search for similar vectors with specified metric type
     * @param collectionName The name of the collection
     * @param vectors The target vectors to search for
     * @param topK Number of nearest neighbors to return
     * @param metricType Metric type for similarity calculation
     * @return SearchResults
     */
    SearchResults search(String collectionName, List<List<Float>> vectors, int topK, MetricType metricType);

    List<Float> getVectorById(String collectionName, Long id);
    
    /**
     * Insert vectors into Milvus
     * @param collectionName The name of the collection
     * @param partitionName The name of the partition (optional)
     * @param vectors The vectors to insert
     * @param ids The corresponding IDs
     */
    void insert(String collectionName, String partitionName, List<List<Float>> vectors, List<Long> ids);

    /**
     * Create Milvus collection for materials
     * @param collectionName The name of the collection
     * @param dimension Vector dimension
     * @param metricType Metric type for similarity calculation
     */
    void createCollection(String collectionName, int dimension, MetricType metricType);

    /**
     * Check if collection exists
     * @param collectionName The name of the collection
     * @return True if collection exists, false otherwise
     */
    boolean collectionExists(String collectionName);

    /**
     * Get similar materials with their scores
     * @param collectionName The name of the collection
     * @param vector The target vector
     * @param topK Number of nearest neighbors to return
     * @return Map of material IDs to similarity scores
     */
    Map<Long, Float> getSimilarMaterials(String collectionName, List<Float> vector, int topK);
}
