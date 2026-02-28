package com.springai.rag.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 向量存储服务
 * 封装向量数据库操作
 */
@Service
public class VectorStoreService {

    private final VectorStore vectorStore;

    public VectorStoreService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * 添加文档到向量库
     */
    public void addDocuments(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            throw new IllegalArgumentException("文档列表不能为空");
        }
        vectorStore.add(documents);
    }

    /**
     * 相似度搜索
     */
    public List<Document> searchSimilar(String query, int topK) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("查询内容不能为空");
        }
        if (topK <= 0) {
            throw new IllegalArgumentException("topK 必须大于 0");
        }
        
        SearchRequest request = SearchRequest.query(query).withTopK(topK);
        return vectorStore.similaritySearch(request);
    }

    /**
     * 相似度搜索（带阈值）
     */
    public List<Document> searchSimilar(String query, int topK, double threshold) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("查询内容不能为空");
        }
        if (topK <= 0) {
            throw new IllegalArgumentException("topK 必须大于 0");
        }
        
        SearchRequest request = SearchRequest.query(query)
                .withTopK(topK)
                .withSimilarityThreshold(threshold);
        return vectorStore.similaritySearch(request);
    }
    
    public List<Document> searchSimilar(String query, int topK) {
        return similaritySearch(query, topK);
    }

    /**
     * 相似度搜索（别名方法，兼容测试）
     */
    public List<Document> searchSimilar(String query, int topK, double threshold) {
        return similaritySearch(query, topK, threshold);
    }


    /**
     * 删除文档
     */
    public void deleteDocuments(List<String> documentIds) {
        if (documentIds == null || documentIds.isEmpty()) {
            throw new IllegalArgumentException("文档 ID 列表不能为空");
        }
        vectorStore.delete(documentIds);
    }
}
