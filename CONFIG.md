# application.yml 配置说明

本文档详细说明了 Spring AI Dev Kit 的配置项。

## 快速开始

### 最小配置（使用 Ollama 本地模型）

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: qwen2.5:7b
```

### 使用 OpenAI

```yaml
spring:
  ai:
    openai:
      api-key: sk-your-api-key-here
      chat:
        enabled: true
```

## 配置项详解

### 1. 大模型配置

#### Ollama（推荐用于本地部署）

```yaml
spring:
  ai:
    ollama:
      base-url: ${OLLAMA_BASE_URL:http://localhost:11434}
      chat:
        enabled: true
        options:
          model: ${OLLAMA_MODEL:qwen2.5:7b}
          temperature: 0.7
          max-tokens: 2000
```

**配置说明：**
- `base-url`: Ollama 服务地址
- `model`: 模型名称，需要先通过 `ollama pull` 下载
- `temperature`: 0-1，控制输出随机性，越高越随机
- `max-tokens`: 最大生成 token 数

**支持的模型：**
- `qwen2.5:7b` - 通义千问（推荐）
- `llama3:8b` - Meta Llama 3
- `mistral:7b` - Mistral AI
- `codellama:7b` - 代码专用模型

#### OpenAI（云端服务）

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY:}
      base-url: ${OPENAI_BASE_URL:https://api.openai.com}
      chat:
        enabled: false
        options:
          model: ${OPENAI_MODEL:gpt-4}
          temperature: 0.7
```

**配置说明：**
- `api-key`: OpenAI API 密钥
- `base-url`: 支持自定义代理地址
- `model`: gpt-4, gpt-3.5-turbo 等

### 2. 向量数据库配置

```yaml
spring:
  ai:
    vectorstore:
      pgvector:
        index-type: HNSW
        distance-type: COSINE_DISTANCE
        dimensions: 1536
        initialize-schema: true
```

**配置说明：**
- `index-type`: 索引类型，HNSW 性能最好
- `distance-type`: 距离计算方式（COSINE_DISTANCE、EUCLIDEAN_DISTANCE）
- `dimensions`: 向量维度，取决于 embedding 模型
- `initialize-schema`: 是否自动初始化数据库表

### 3. MCP 工具配置

#### ELK 日志查询工具

```yaml
mcp:
  tools:
    elk:
      enabled: true
      base-url: http://localhost:9200
      username: elastic
      password: changeme
      index-pattern: logs-*
```

**环境变量方式：**
```bash
export ELK_BASE_URL=http://your-elk-server:9200
export ELK_USERNAME=your-username
export ELK_PASSWORD=your-password
```

#### Prometheus 监控工具

```yaml
mcp:
  tools:
    prometheus:
      enabled: true
      base-url: http://localhost:9090
      username: 
      password: 
```

**环境变量方式：**
```bash
export PROMETHEUS_BASE_URL=http://your-prometheus:9090
```

#### SQL 报表工具

```yaml
mcp:
  tools:
    sql-report:
      enabled: true
      datasource-url: jdbc:postgresql://localhost:5432/report_db
      datasource-username: postgres
      datasource-password: postgres
      export-dir: ./reports
```

### 4. 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/spring_ai_dev_kit
    username: postgres
    password: postgres
```

**环境变量方式：**
```bash
export DB_URL=jdbc:postgresql://your-db:5432/dbname
export DB_USERNAME=your-username
export DB_PASSWORD=your-password
```

### 5. Redis 配置

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: 
      database: 0
```

**环境变量方式：**
```bash
export REDIS_HOST=your-redis-host
export REDIS_PORT=6379
export REDIS_PASSWORD=your-password
```

## 环境变量配置（推荐）

创建 `.env` 文件：

```bash
# 大模型配置
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=qwen2.5:7b

# OpenAI 配置（可选）
OPENAI_API_KEY=sk-your-api-key
OPENAI_BASE_URL=https://api.openai.com

# 数据库配置
DB_URL=jdbc:postgresql://localhost:5432/spring_ai_dev_kit
DB_USERNAME=postgres
DB_PASSWORD=postgres

# Redis 配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# MCP 工具配置
ELK_BASE_URL=http://localhost:9200
ELK_USERNAME=elastic
ELK_PASSWORD=changeme

PROMETHEUS_BASE_URL=http://localhost:9090

REPORT_DB_URL=jdbc:postgresql://localhost:5432/report_db
REPORT_DB_USERNAME=postgres
REPORT_DB_PASSWORD=postgres
```

## Docker 部署配置

在 `docker-compose.yml` 中配置环境变量：

```yaml
services:
  spring-ai-dev-kit:
    environment:
      OLLAMA_BASE_URL: http://ollama:11434
      DB_URL: jdbc:postgresql://postgres:5432/spring_ai_dev_kit
      REDIS_HOST: redis
      ELK_BASE_URL: http://elasticsearch:9200
      PROMETHEUS_BASE_URL: http://prometheus:9090
```

## 配置优先级

1. 命令行参数
2. 环境变量
3. application.yml
4. 默认值

## 常见问题

### Q: 如何切换大模型？

A: 修改 `spring.ai.ollama.chat.options.model` 或设置环境变量 `OLLAMA_MODEL`

### Q: 如何禁用某个 MCP 工具？

A: 设置 `mcp.tools.xxx.enabled=false`

### Q: 如何使用自定义 OpenAI 代理？

A: 修改 `spring.ai.openai.base-url` 为代理地址

### Q: 向量维度如何确定？

A: 取决于 embedding 模型：
- OpenAI text-embedding-ada-002: 1536
- Ollama nomic-embed-text: 768

