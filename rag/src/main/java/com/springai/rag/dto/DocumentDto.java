package com.springai.rag.dto;

import lombok.Data;

/**
 * 文档 DTO
 */
@Data
public class DocumentDto {
    
    /**
     * 文档 ID
     */
    private String id;
    
    /**
     * 文档内容
     */
    private String content;
    
    /**
     * 文档来源
     */
    private String source;
    
    /**
     * 相似度分数
     */
    private Double score;
    
    /**
     * 元数据
     */
    private String metadata;
}

