package com.springai.agent.controller;

import com.springai.agent.service.AgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI Agent 控制器
 */
@Tag(name = "AI Agent", description = "AI Agent 智能对话接口，支持自动调用工具")
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @Operation(summary = "智能对话（流式，自动调用工具）", 
               description = "支持自动调用 ELK 日志查询、Prometheus 监控、SQL 报表导出等工具")
    @GetMapping("/chat")
    public SseEmitter chat(@RequestParam String message) {
        return agentService.chat(message);
    }
}

