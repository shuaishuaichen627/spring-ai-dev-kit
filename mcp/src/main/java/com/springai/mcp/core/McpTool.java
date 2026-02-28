package com.springai.mcp.core;

import java.util.Map;

/**
 * MCP 工具接口
 */
public interface McpTool {

    /**
     * 获取工具名称
     */
    String getName();

    /**
     * 获取工具描述
     */
    String getDescription();

    /**
     * 执行工具
     */
    String execute(Map<String, Object> params);
}

