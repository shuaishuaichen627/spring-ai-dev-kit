package com.springai.agent.service;

import com.springai.common.constant.CommonConstants;
import com.springai.common.util.SseEmitterUtil;
import com.springai.rag.service.VectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI Agent 服务
 * 工具通过 @Tool 注解自动注册，Spring AI 会自动识别并调用
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentService {

    private final ChatClient.Builder chatClientBuilder;
    private final VectorStoreService vectorStoreService;

    /**
     * 处理用户查询（流式响应，自动调用工具）
     */
    public SseEmitter chat(String userMessage) {
        SseEmitter emitter = new SseEmitter(60000L);

        // 异步处理，避免阻塞
        new Thread(() -> {
            try {
                // 1. RAG 检索相关知识
                List<Document> relevantDocs = vectorStoreService.similaritySearch(userMessage, 3);
                String context = buildContext(relevantDocs);

                // 2. 构建提示词
                String prompt = buildPrompt(userMessage, context);

                // 3. 调用大模型（流式，Spring AI 会自动识别 @Tool 注解的方法）
                ChatClient chatClient = chatClientBuilder.build();
                
                Flux<ChatResponse> responseFlux = chatClient.prompt()
                        .user(prompt)
                        .stream()
                        .chatResponse();

                // 4. 流式推送结果
                responseFlux.subscribe(
                        response -> {
                            if (response != null && response.getResult() != null 
                                    && response.getResult().getOutput() != null) {
                                String content = response.getResult().getOutput().getContent();
                                if (content != null && !content.isEmpty()) {
                                    SseEmitterUtil.send(emitter, CommonConstants.SseEvent.MESSAGE, content);
                                }
                            }
                        },
                        error -> {
                            log.error("大模型调用失败", error);
                            SseEmitterUtil.send(emitter, CommonConstants.SseEvent.ERROR, 
                                    "处理失败：" + error.getMessage());
                            SseEmitterUtil.error(emitter, error);
                        },
                        () -> {
                            SseEmitterUtil.send(emitter, CommonConstants.SseEvent.DONE, "");
                            SseEmitterUtil.complete(emitter);
                        }
                );

            } catch (Exception e) {
                log.error("Agent 处理失败", e);
                SseEmitterUtil.send(emitter, CommonConstants.SseEvent.ERROR, 
                        "处理失败：" + e.getMessage());
                SseEmitterUtil.error(emitter, e);
            }
        }).start();

        return emitter;
    }

    /**
     * 构建上下文
     */
    private String buildContext(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return "暂无相关上下文信息";
        }
        
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            context.append(String.format("[文档 %d]\n", i + 1));
            context.append(doc.getContent());
            context.append("\n\n");
        }
        return context.toString();
    }

    /**
     * 构建提示词
     */
    private String buildPrompt(String userMessage, String context) {
        return String.format("""
                你是一个智能研发助手，可以使用以下工具来帮助用户：
                1. queryElkLogs - 查询 ELK 日志系统，用于排查错误和异常
                2. queryPrometheusMetrics - 查询 Prometheus 监控指标，用于性能分析
                3. exportSqlReport - 导出 SQL 报表
                4. reviewCode - 代码评审
                
                参考上下文信息：
                %s
                
                用户问题：
                %s
                
                请根据用户问题，决定是否需要调用工具。如果需要调用工具，请自动调用并根据结果回答。
                回答要专业、准确、有条理。
                """, context, userMessage);
    }
}
