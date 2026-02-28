package com.springai.mcp.service;

import com.springai.mcp.core.McpTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * MCP 网关服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class McpGatewayService {

    private final List<McpTool> mcpTools;

    /**
     * 执行 MCP 工具
     */
    public String executeTool(String toolName, Map<String, Object> params) {
        Optional<McpTool> tool = mcpTools.stream()
                .filter(t -> t.getName().equals(toolName))
                .findFirst();

        if (tool.isEmpty()) {
            log.error("未找到 MCP 工具：{}", toolName);
            throw new RuntimeException("未找到 MCP 工具：" + toolName);
        }

        log.info("执行 MCP 工具：{}", toolName);
        return tool.get().execute(params);
    }

    /**
     * 获取所有可用工具
     */
    public List<String> listTools() {
        return mcpTools.stream()
                .map(McpTool::getName)
                .toList();
    }
}

