package com.springai.sample.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * RAG 极简示例
 * 演示如何使用向量数据库进行知识检索
 */
@Component
public class RagSimpleDemo {

    private final VectorStore vectorStore;

    public RagSimpleDemo(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * 示例 1：添加文档到向量库
     */
    public void addDocuments() {
        // 创建文档
        Document doc1 = new Document("Spring AI 是一个用于构建 AI 应用的框架");
        Document doc2 = new Document("向量数据库可以存储和检索文本的语义信息");
        Document doc3 = new Document("RAG 技术结合了检索和生成，提升 AI 回答质量");

        // 添加到向量库
        vectorStore.add(List.of(doc1, doc2, doc3));
        
        System.out.println("✅ 成功添加 3 个文档到向量库");
    }

    /**
     * 示例 2：相似度搜索
     */
    public List<Document> searchSimilar(String query) {
        // 创建搜索请求
        SearchRequest request = SearchRequest.query(query)
                .withTopK(3)  // 返回最相似的 3 个结果
                .withSimilarityThreshold(0.7);  // 相似度阈值

        // 执行搜索
        List<Document> results = vectorStore.similaritySearch(request);
        
        System.out.println("查询：" + query);
        System.out.println("找到 " + results.size() + " 个相关文档：");
        results.forEach(doc -> System.out.println("- " + doc.getContent()));
        
        return results;
    }

    /**
     * 示例 3：完整的 RAG 流程
     */
    public String ragQuery(String userQuestion) {
        // 1. 检索相关文档
        List<Document> relevantDocs = searchSimilar(userQuestion);
        
        // 2. 构建上下文
        StringBuilder context = new StringBuilder();
        for (Document doc : relevantDocs) {
            context.append(doc.getContent()).append("\n");
        }
        
        // 3. 构建提示词（实际应用中会调用大模型）
        String prompt = String.format("""
                基于以下上下文回答问题：
                
                上下文：
                %s
                
                问题：%s
                """, context, userQuestion);
        
        System.out.println("生成的提示词：");
        System.out.println(prompt);
        
        return prompt;
    }

    /**
     * 运行示例
     */
    public void runDemo() {
        System.out.println("========== RAG 示例演示 ==========\n");
        
        // 步骤 1：添加文档
        System.out.println("步骤 1：添加文档到向量库");
        addDocuments();
        System.out.println();
        
        // 步骤 2：相似度搜索
        System.out.println("步骤 2：相似度搜索");
        searchSimilar("什么是 RAG？");
        System.out.println();
        
        // 步骤 3：完整 RAG 查询
        System.out.println("步骤 3：完整 RAG 查询");
        ragQuery("如何使用向量数据库？");
        
        System.out.println("\n========== 示例演示完成 ==========");
    }
}

