# Spring AI Dev Kit - 示例代码

本目录包含 Spring AI Dev Kit 的极简示例代码，帮助你快速理解和使用核心功能。

## 📁 目录结构

```
sample/
├── rag/
│   └── RagSimpleDemo.java          # RAG 知识检索示例
├── agent/
│   └── AgentSimpleDemo.java        # AI Agent 对话示例
└── mcp/
    └── McpSimpleDemo.java          # MCP 工具示例
```

## 🚀 快速开始

### 1. RAG 知识检索示例

演示如何使用向量数据库进行知识检索。

```java
@Autowired
private RagSimpleDemo ragDemo;

// 运行完整示例
ragDemo.runDemo();

// 或单独使用
ragDemo.addDocuments();  // 添加文档
ragDemo.searchSimilar("什么是 RAG？");  // 搜索
```

**功能演示：**
- 添加文档到向量库
- 相似度搜索
- 完整的 RAG 查询流程

### 2. AI Agent 对话示例

演示如何使用 Spring AI 进行对话。

```java
@Autowired
private AgentSimpleDemo agentDemo;

// 运行完整示例
agentDemo.runDemo();

// 或单独使用
agentDemo.simpleChat("你好");  // 简单对话
agentDemo.streamChat("介绍一下 Spring AI");  // 流式对话
agentDemo.chatWithSystemPrompt("如何优化性能？");  // 带系统提示
```

**功能演示：**
- 简单对话
- 流式对话
- 带系统提示的对话
- 多轮对话

### 3. MCP 工具示例

演示如何创建和使用 MCP 工具。

```java
@Autowired
private McpSimpleDemo mcpDemo;

// 运行完整示例
mcpDemo.runDemo();

// 或单独使用
mcpDemo.calculator("add", 10, 5);  // 计算器
mcpDemo.getWeather("北京");  // 天气查询
mcpDemo.analyzeText("Hello World");  // 文本分析
```

**功能演示：**
- 创建简单的 MCP 工具
- 使用 @Tool 注解
- 工具参数和返回值

## 📝 使用说明

### 方式一：通过 REST API 调用

```bash
# 启动应用
java -jar boot/target/boot-1.0.0.jar

# 调用示例（AI 会自动识别并调用工具）
curl -N "http://localhost:8080/api/agent/chat?message=帮我计算10加5"
curl -N "http://localhost:8080/api/agent/chat?message=查询北京的天气"
```

### 方式二：直接在代码中调用

```java
@RestController
@RequestMapping("/demo")
public class DemoController {
    
    @Autowired
    private RagSimpleDemo ragDemo;
    
    @Autowired
    private AgentSimpleDemo agentDemo;
    
    @Autowired
    private McpSimpleDemo mcpDemo;
    
    @GetMapping("/rag")
    public String testRag() {
        ragDemo.runDemo();
        return "RAG 示例执行完成";
    }
    
    @GetMapping("/agent")
    public String testAgent() {
        agentDemo.runDemo();
        return "Agent 示例执行完成";
    }
    
    @GetMapping("/mcp")
    public String testMcp() {
        mcpDemo.runDemo();
        return "MCP 示例执行完成";
    }
}
```

## 🎯 学习路径

### 1. 初学者

建议按以下顺序学习：

1. **MCP 工具示例** - 最简单，理解工具的概念
2. **Agent 对话示例** - 理解如何与 AI 对话
3. **RAG 知识检索示例** - 理解如何增强 AI 的知识

### 2. 进阶开发者

可以直接查看：

- `mcp/src/main/java/com/springai/mcp/tools/` - 真实的工具实现
- `agent/src/main/java/com/springai/agent/` - 完整的 Agent 实现
- `rag/src/main/java/com/springai/rag/` - 完整的 RAG 实现

## 💡 扩展示例

### 创建自己的 MCP 工具

```java
@Component
public class MyCustomTool {
    
    @Tool(description = "你的工具描述")
    public String myTool(String param1, int param2) {
        // 实现你的逻辑
        return "结果";
    }
}
```

### 创建自己的 RAG 应用

```java
@Service
public class MyRagService {
    
    @Autowired
    private VectorStore vectorStore;
    
    @Autowired
    private ChatClient.Builder chatClientBuilder;
    
    public String query(String question) {
        // 1. 检索相关文档
        List<Document> docs = vectorStore.similaritySearch(
            SearchRequest.query(question).withTopK(3)
        );
        
        // 2. 构建上下文
        String context = docs.stream()
            .map(Document::getContent)
            .collect(Collectors.joining("\n"));
        
        // 3. 调用 AI
        return chatClientBuilder.build()
            .prompt()
            .user("基于以下上下文回答：\n" + context + "\n\n问题：" + question)
            .call()
            .content();
    }
}
```

## 📚 相关文档

- [README.md](../README.md) - 项目说明
- [QUICKSTART.md](../QUICKSTART.md) - 快速开始
- [TOOLS.md](../TOOLS.md) - 工具使用指南
- [DEVELOPMENT.md](../DEVELOPMENT.md) - 开发文档

## ❓ 常见问题

### Q: 示例代码可以直接运行吗？

A: 可以，但需要先启动应用并配置好数据库和大模型。

### Q: 如何修改示例代码？

A: 示例代码都是独立的类，可以直接复制修改。

### Q: 示例代码支持哪些大模型？

A: 支持 Ollama 和 OpenAI，在 `application.yml` 中配置。

---

**开始探索 Spring AI 的强大功能吧！** 🚀

