package com.springai.mcp.controller;

import com.springai.common.result.Result;
import com.springai.mcp.service.McpGatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * MCP 工具控制器
 */
@Tag(name = "MCP 工具", description = "MCP 工具调用接口")
@RestController
@RequestMapping("/api/mcp")
@RequiredArgsConstructor
public class McpController {

    private final McpGatewayService mcpGatewayService;

    @Operation(summary = "获取所有工具列表")
    @GetMapping("/tools")
    public Result<List<String>> listTools() {
        List<String> tools = mcpGatewayService.listTools();
        return Result.success(tools);
    }

    @Operation(summary = "执行工具")
    @PostMapping("/execute")
    public Result<String> executeTool(
            @RequestParam String toolName,
            @RequestBody Map<String, Object> params) {
        String result = mcpGatewayService.executeTool(toolName, params);
        return Result.success(result);
    }
}

