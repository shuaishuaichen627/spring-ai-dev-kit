package com.springai.agent.dto;

import lombok.Data;

/**
 * 聊天请求 DTO
 */
@Data
public class ChatRequest {
    
    /**
     * 用户消息
     */
    private String message;
    
    /**
     * 会话 ID
     */
    private String sessionId;
    
    /**
     * 是否使用 RAG
     */
    private Boolean useRag = true;
    
    /**
     * 是否使用 MCP
     */
    private Boolean useMcp = true;
    
    /**
     * 温度参数
     */
    private Double temperature = 0.7;
}

