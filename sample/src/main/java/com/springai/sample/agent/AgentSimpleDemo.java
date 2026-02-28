package com.springai.sample.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * Agent 极简示例
 * 演示如何使用 Spring AI 进行对话
 */
@Component
public class AgentSimpleDemo {

    private final ChatClient.Builder chatClientBuilder;

    public AgentSimpleDemo(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    /**
     * 示例 1：简单对话
     */
    public String simpleChat(String message) {
        ChatClient chatClient = chatClientBuilder.build();
        
        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();
        
        System.out.println("用户：" + message);
        System.out.println("AI：" + response);
        
        return response;
    }

    /**
     * 示例 2：流式对话
     */
    public void streamChat(String message) {
        ChatClient chatClient = chatClientBuilder.build();
        
        System.out.println("用户：" + message);
        System.out.print("AI：");
        
        Flux<ChatResponse> responseFlux = chatClient.prompt()
                .user(message)
                .stream()
                .chatResponse();
        
        // 流式输出
        responseFlux.subscribe(
                response -> {
                    String content = response.getResult().getOutput().getContent();
                    System.out.print(content);
                },
                error -> System.err.println("\n错误：" + error.getMessage()),
                () -> System.out.println("\n[完成]")
        );
    }

    /**
     * 示例 3：带系统提示的对话
     */
    public String chatWithSystemPrompt(String userMessage) {
        ChatClient chatClient = chatClientBuilder.build();
        
        String response = chatClient.prompt()
                .system("你是一个专业的 Java 开发助手，请用简洁的语言回答问题")
                .user(userMessage)
                .call()
                .content();
        
        System.out.println("系统提示：你是一个专业的 Java 开发助手");
        System.out.println("用户：" + userMessage);
        System.out.println("AI：" + response);
        
        return response;
    }

    /**
     * 示例 4：多轮对话
     */
    public void multiTurnChat() {
        ChatClient chatClient = chatClientBuilder.build();
        
        System.out.println("========== 多轮对话示例 ==========");
        
        // 第一轮
        String response1 = chatClient.prompt()
                .user("我想学习 Spring AI")
                .call()
                .content();
        System.out.println("用户：我想学习 Spring AI");
        System.out.println("AI：" + response1);
        System.out.println();
        
        // 第二轮（基于上下文）
        String response2 = chatClient.prompt()
                .user("它有哪些核心功能？")
                .call()
                .content();
        System.out.println("用户：它有哪些核心功能？");
        System.out.println("AI：" + response2);
    }

    /**
     * 运行示例
     */
    public void runDemo() {
        System.out.println("========== Agent 示例演示 ==========\n");
        
        // 示例 1：简单对话
        System.out.println("示例 1：简单对话");
        simpleChat("你好，介绍一下自己");
        System.out.println();
        
        // 示例 2：流式对话
        System.out.println("示例 2：流式对话");
        streamChat("用一句话解释什么是 AI");
        System.out.println();
        
        // 示例 3：带系统提示
        System.out.println("示例 3：带系统提示的对话");
        chatWithSystemPrompt("如何优化 Java 代码性能？");
        System.out.println();
        
        System.out.println("========== 示例演示完成 ==========");
    }
}

