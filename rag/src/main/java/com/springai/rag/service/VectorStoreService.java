package com.springai.rag.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 向量库服务
 */
@Slf4j
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
        try {
            if (documents == null || documents.isEmpty()) {
                log.warn("文档列表为空，跳过添加");
                return;
            }
            vectorStore.add(documents);
            log.info("成功添加 {} 个文档到向量库", documents.size());
        } catch (Exception e) {
            log.error("添加文档到向量库失败", e);
            throw new RuntimeException("添加文档到向量库失败：" + e.getMessage());
        }
    }

    /**
     * 相似度搜索
     */
    public List<Document> similaritySearch(String query, int topK) {
        try {
            if (query == null || query.trim().isEmpty()) {
                log.warn("查询字符串为空，返回空列表");
                return Collections.emptyList();
            }
            
            SearchRequest request = SearchRequest.query(query).withTopK(topK);
            List<Document> results = vectorStore.similaritySearch(request);
            log.info("相似度搜索完成，查询：{}，返回 {} 个结果", query, results.size());
            return results;
        } catch (Exception e) {
            log.error("相似度搜索失败，查询：{}", query, e);
            // 返回空列表而不是抛出异常，避免影响主流程
            return Collections.emptyList();
        }
    }
    
    /**
     * 删除所有文档
     */
    public void deleteAll() {
        try {
            vectorStore.delete(Collections.emptyList());
            log.info("成功清空向量库");
        } catch (Exception e) {
            log.error("清空向量库失败", e);
            throw new RuntimeException("清空向量库失败：" + e.getMessage());
        }
    }
}
