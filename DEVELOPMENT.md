# Spring AI Dev Kit - 开发文档

## 目录

- [快速开始](#快速开始)
- [模块说明](#模块说明)
- [API 文档](#api-文档)
- [开发指南](#开发指南)
- [常见问题](#常见问题)

---

## 快速开始

### 环境准备

1. **安装 Java 17**
```bash
java -version
```

2. **安装 Maven**
```bash
mvn -version
```

3. **安装 Docker**
```bash
docker --version
docker-compose --version
```

### 启动开发环境

```bash
# 1. 启动依赖服务
docker-compose up -d postgres redis ollama

# 2. 下载模型
docker exec -it spring-ai-ollama ollama pull qwen2.5:7b

# 3. 编译项目
mvn clean install

# 4. 启动应用
cd boot
mvn spring-boot:run
```

---

## 模块说明

### Common 模块

公共基础模块，提供：
- 统一返回结果封装
- 全局异常处理
- 通用工具类
- 常量定义

### RAG 模块

知识库模块，提供：
- Git 代码库解析
- 文档解析（PDF、Markdown）
- 向量数据库交互
- 相似度检索

### MCP 模块

工具模块，提供：
- MCP 工具接口定义
- 内置工具实现（ELK、Prometheus）
- 工具网关调度

### Agent 模块

智能体模块，提供：
- 大模型交互
- RAG + MCP 编排
- SSE 流式响应
- 上下文管理

### Config 模块

配置模块，提供：
- Spring AI 配置
- Swagger 配置
- 数据源配置

### Boot 模块

启动模块，整合所有模块

---

## API 文档

### RAG 接口

#### 克隆 Git 仓库
```http
POST /api/rag/git/clone
?gitUrl=https://github.com/user/repo.git
&localPath=/tmp/repo
```

#### 相似度搜索
```http
GET /api/rag/search
?query=如何优化性能
&topK=5
```

### MCP 接口

#### 获取工具列表
```http
GET /api/mcp/tools
```

#### 执行工具
```http
POST /api/mcp/execute?toolName=elk_log_query
Content-Type: application/json

{
  "keyword": "ERROR",
  "startTime": "2024-01-01T00:00:00",
  "endTime": "2024-01-02T00:00:00"
}
```

### Agent 接口

#### 智能对话（SSE）
```http
GET /api/agent/chat?message=你好
```

---

## 开发指南

### 添加新的 MCP 工具

1. 实现 `McpTool` 接口：

```java
@Component
public class CustomTool implements McpTool {
    
    @Override
    public String getName() {
        return "custom_tool";
    }
    
    @Override
    public String getDescription() {
        return "自定义工具描述";
    }
    
    @Override
    public String execute(Map<String, Object> params) {
        // 实现工具逻辑
        return "执行结果";
    }
}
```

2. Spring 会自动注册该工具到 MCP 网关

### 自定义文档解析器

继承 Spring AI 的 `DocumentReader` 接口：

```java
@Service
public class CustomDocumentReader implements DocumentReader {
    
    @Override
    public List<Document> get() {
        // 实现解析逻辑
        return documents;
    }
}
```

---

## 常见问题

### Q: Ollama 模型下载慢？

A: 可以使用国内镜像或手动下载模型文件

### Q: 向量数据库连接失败？

A: 检查 PostgreSQL 是否安装了 pgvector 扩展

### Q: SSE 流式响应中断？

A: 检查 Nginx 配置，确保关闭了缓冲

---

## 贡献指南

欢迎提交 PR！请遵循以下规范：

1. 代码风格：遵循 Google Java Style Guide
2. 提交信息：使用语义化提交信息
3. 测试覆盖：新功能需要添加单元测试
4. 文档更新：更新相关文档

---

## 联系我们

- GitHub Issues: https://github.com/your-repo/spring-ai-dev-kit/issues
- Email: support@example.com

