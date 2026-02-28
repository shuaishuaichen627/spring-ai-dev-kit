# Spring AI Dev Kit - 集成测试指南

## 测试前准备

### 1. 启动依赖服务

```bash
# 启动 PostgreSQL + PGVector
docker run -d --name pgvector \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=spring_ai_dev_kit \
  -p 5432:5432 \
  ankane/pgvector:latest

# 启动 Redis
docker run -d --name redis \
  -p 6379:6379 \
  redis:7-alpine

# 启动 Ollama
docker run -d --name ollama \
  -p 11434:11434 \
  -v ollama:/root/.ollama \
  ollama/ollama:latest

# 下载模型
docker exec -it ollama ollama pull qwen2.5:7b

# 启动 Elasticsearch（可选，用于测试 ELK 工具）
docker run -d --name elasticsearch \
  -e "discovery.type=single-node" \
  -e "xpack.security.enabled=false" \
  -p 9200:9200 \
  elasticsearch:8.11.1

# 启动 Prometheus（可选，用于测试 Prometheus 工具）
docker run -d --name prometheus \
  -p 9090:9090 \
  prom/prometheus:latest
```

### 2. 配置环境变量

创建 `.env` 文件：

```bash
# 大模型配置
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=qwen2.5:7b

# 数据库配置
DB_URL=jdbc:postgresql://localhost:5432/spring_ai_dev_kit
DB_USERNAME=postgres
DB_PASSWORD=postgres

# Redis 配置
REDIS_HOST=localhost
REDIS_PORT=6379

# ELK 配置（如果启动了 Elasticsearch）
MCP_ELK_ENABLED=true
ELK_BASE_URL=http://localhost:9200
ELK_USERNAME=
ELK_PASSWORD=
ELK_INDEX_PATTERN=logs-*

# Prometheus 配置（如果启动了 Prometheus）
MCP_PROMETHEUS_ENABLED=true
PROMETHEUS_BASE_URL=http://localhost:9090
```

## 测试步骤

### 1. 编译项目

```bash
mvn clean install -DskipTests
```

### 2. 启动应用

```bash
cd boot
mvn spring-boot:run
```

或者直接运行：

```bash
java -jar boot/target/boot-1.0.0.jar
```

### 3. 检查启动日志

确认以下组件初始化成功：

```
✅ Elasticsearch 客户端初始化成功：http://localhost:9200
✅ Prometheus WebClient 初始化成功：http://localhost:9090
✅ Spring AI ChatClient 初始化成功
✅ VectorStore 初始化成功
```

### 4. 测试 API

#### 测试基础对话

```bash
curl -N "http://localhost:8080/api/agent/chat?message=你好，介绍一下自己"
```

#### 测试 ELK 工具调用

```bash
# 先插入一些测试日志到 Elasticsearch
curl -X POST "http://localhost:9200/logs-test/_doc" \
  -H 'Content-Type: application/json' \
  -d '{
    "@timestamp": "2024-01-01T10:00:00Z",
    "level": "ERROR",
    "message": "NullPointerException at UserService.java:123",
    "service": "user-service"
  }'

# 测试查询
curl -N "http://localhost:8080/api/agent/chat?message=查询最近的ERROR日志"
```

#### 测试 Prometheus 工具调用

```bash
curl -N "http://localhost:8080/api/agent/chat?message=查询up指标的当前状态"
```

#### 测试 RAG 功能

```bash
# 添加文档
curl -X POST "http://localhost:8080/api/rag/document/pdf?filePath=/path/to/doc.pdf"

# 搜索
curl "http://localhost:8080/api/rag/search?query=如何优化性能&topK=3"
```

## 常见问题排查

### 1. 连接 Ollama 失败

**错误信息：**
```
Connection refused: localhost/127.0.0.1:11434
```

**解决方案：**
```bash
# 检查 Ollama 是否运行
docker ps | grep ollama

# 检查端口是否开放
curl http://localhost:11434/api/tags

# 重启 Ollama
docker restart ollama
```

### 2. 向量数据库初始化失败

**错误信息：**
```
relation "vector_store" does not exist
```

**解决方案：**
```bash
# 手动执行初始化脚本
docker exec -i pgvector psql -U postgres -d spring_ai_dev_kit < init-db.sql
```

### 3. Elasticsearch 连接失败

**错误信息：**
```
Connection refused: localhost:9200
```

**解决方案：**
```bash
# 检查 Elasticsearch 状态
curl http://localhost:9200

# 如果不需要 ELK 功能，可以禁用
export MCP_ELK_ENABLED=false
```

### 4. 工具未被调用

**可能原因：**
1. 工具描述不够清晰
2. 用户问题不够明确
3. 大模型未识别出需要调用工具

**解决方案：**
- 使用更明确的问题，如"查询 ELK 日志"而不是"看看日志"
- 检查日志中是否有工具调用记录
- 调整工具的 description 描述

## 性能测试

### 1. 并发测试

```bash
# 使用 ab 工具测试
ab -n 100 -c 10 "http://localhost:8080/api/agent/chat?message=hello"
```

### 2. 响应时间测试

```bash
# 测试流式响应
time curl -N "http://localhost:8080/api/agent/chat?message=查询日志"
```

## 集成测试清单

- [ ] 应用成功启动
- [ ] Swagger UI 可访问（http://localhost:8080/swagger-ui）
- [ ] 基础对话功能正常
- [ ] ELK 工具可以正常调用
- [ ] Prometheus 工具可以正常调用
- [ ] RAG 检索功能正常
- [ ] SSE 流式响应正常
- [ ] 错误处理正常
- [ ] 日志输出正常

## 生产环境检查

### 1. 配置检查

```bash
# 检查所有环境变量是否配置
env | grep -E "OLLAMA|OPENAI|DB_|REDIS|ELK|PROMETHEUS"
```

### 2. 连接测试

```bash
# 测试数据库连接
psql -h localhost -U postgres -d spring_ai_dev_kit -c "SELECT 1"

# 测试 Redis 连接
redis-cli ping

# 测试 Elasticsearch 连接
curl http://your-elk:9200/_cluster/health

# 测试 Prometheus 连接
curl http://your-prometheus:9090/api/v1/status/config
```

### 3. 性能优化

- 调整数据库连接池大小
- 配置 Redis 缓存
- 优化向量检索参数
- 调整大模型参数（temperature、max-tokens）

## 监控指标

关注以下指标：

1. **响应时间**：平均响应时间应 < 3s
2. **工具调用成功率**：应 > 95%
3. **大模型调用成功率**：应 > 99%
4. **内存使用**：应 < 2GB
5. **CPU 使用**：应 < 80%

## 日志分析

查看关键日志：

```bash
# 查看应用日志
tail -f logs/spring-ai-dev-kit.log

# 查看工具调用日志
grep "执行.*工具" logs/spring-ai-dev-kit.log

# 查看错误日志
grep "ERROR" logs/spring-ai-dev-kit.log
```

