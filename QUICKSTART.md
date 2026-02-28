# Spring AI Dev Kit - 快速开始指南

## 5 分钟快速体验

### 方式一：Docker Compose（推荐）

```bash
# 1. 克隆项目
git clone https://github.com/your-repo/spring-ai-dev-kit.git
cd spring-ai-dev-kit

# 2. 配置环境变量（可选，使用默认配置可跳过）
cp env.example .env
# 编辑 .env 文件，填入你的配置

# 3. 一键启动
chmod +x deploy.sh
./deploy.sh

# 4. 访问应用
# 浏览器打开：http://localhost/swagger-ui
```

### 方式二：本地开发

```bash
# 1. 启动依赖服务
docker-compose up -d postgres redis ollama

# 2. 下载模型
docker exec -it spring-ai-ollama ollama pull qwen2.5:7b

# 3. 编译运行
mvn clean install
cd boot
mvn spring-boot:run

# 4. 访问应用
# 浏览器打开：http://localhost:8080/swagger-ui
```

## 快速测试

### 1. 测试智能对话

```bash
curl -N "http://localhost:8080/api/agent/chat?message=你好，介绍一下自己"
```

### 2. 测试 RAG 检索

```bash
# 克隆代码库
curl -X POST "http://localhost:8080/api/rag/git/clone?gitUrl=https://github.com/spring-projects/spring-boot.git&localPath=/tmp/spring-boot"

# 相似度搜索
curl "http://localhost:8080/api/rag/search?query=如何配置数据源&topK=3"
```

### 3. 测试 MCP 工具

```bash
# 获取工具列表
curl "http://localhost:8080/api/mcp/tools"

# 执行 ELK 查询
curl -X POST "http://localhost:8080/api/mcp/execute?toolName=elk_log_query" \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "ERROR",
    "startTime": "2024-01-01T00:00:00",
    "endTime": "2024-01-02T00:00:00"
  }'
```

## 常见问题

### Q: 启动失败？

检查端口占用：
```bash
# 检查 8080 端口
netstat -ano | findstr 8080

# 检查 5432 端口（PostgreSQL）
netstat -ano | findstr 5432
```

### Q: Ollama 模型下载慢？

使用国内镜像或手动下载模型文件放到 `~/.ollama/models` 目录

### Q: 向量数据库连接失败？

确保 PostgreSQL 已安装 pgvector 扩展：
```sql
CREATE EXTENSION IF NOT EXISTS vector;
```

## 下一步

- 查看 [开发文档](DEVELOPMENT.md) 了解详细功能
- 查看 [API 文档](http://localhost:8080/swagger-ui) 了解接口详情
- 查看 [贡献指南](CONTRIBUTING.md) 参与项目开发

## 获取帮助

- GitHub Issues: https://github.com/your-repo/spring-ai-dev-kit/issues
- 文档：查看项目 Wiki
- Email: support@example.com

