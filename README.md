# Spring AI Dev Kit

<div align="center">

![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0-green)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![License](https://img.shields.io/badge/License-Apache%202.0-blue)

**基于 Spring AI 的智能研发助手，整合 RAG、MCP 和 AI Agent 能力**

[快速开始](#快速开始) • [功能特性](#功能特性) • [架构设计](#架构设计) • [部署指南](#部署指南)

</div>

---

## 📖 项目简介

Spring AI Dev Kit 是一个企业级智能研发助手平台，基于 Spring AI 框架构建，整合了 RAG（检索增强生成）、MCP（模型上下文协议）和 AI Agent 三大核心能力，旨在提升研发效率，实现智能化的研发流程。

### 核心能力

- **RAG 静态知识库**：支持 Git 代码库解析、文档解析（MD/PDF），构建企业级知识库
- **MCP 动态工具集**：内置 ELK 日志查询、Prometheus 监控分析等研发运营工具
- **AI Agent 智能编排**：自动调度 RAG 和 MCP 能力，实现智能问答和任务自动化

---

## ✨ 功能特性

### 🔍 RAG 知识库

- ✅ Git 代码库自动拉取与解析
- ✅ 文档解析（Markdown、PDF）
- ✅ PGVector 向量数据库存储
- ✅ 语义相似度检索
- ✅ 支持多种编程语言代码解析

### 🛠️ MCP 工具集

- ✅ ELK 日志查询工具
- ✅ Prometheus 监控分析工具
- ✅ SQL 报表导出工具
- ✅ 代码评审工具
- ✅ 基于 Spring AI Function Calling
- ✅ AI 自动识别并调用工具

### 🤖 AI Agent

- ✅ Ollama / OpenAI 大模型支持
- ✅ SSE 流式响应
- ✅ RAG + MCP 智能编排
- ✅ 上下文感知对话
- ✅ 多轮对话支持

---

## 🏗️ 架构设计

### 模块划分

```
spring-ai-dev-kit
├── common          # 公共模块（异常处理、工具类、统一返回）
├── rag             # RAG 知识库模块
├── mcp             # MCP 工具模块
├── agent           # AI Agent 模块
├── config          # 配置模块
└── boot            # 启动模块
```

### 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.0 | 应用框架 |
| Spring AI | 1.0.0 | AI 集成框架 |
| Ollama | Latest | 本地大模型 |
| PostgreSQL + PGVector | Latest | 向量数据库 |
| Redis | 7.x | 缓存 |
| Docker | Latest | 容器化部署 |
| Nginx | Latest | 反向代理 |

---

## 🚀 快速开始

### 前置要求

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Git

### 本地开发

1. **克隆项目**

```bash
git clone https://github.com/your-repo/spring-ai-dev-kit.git
cd spring-ai-dev-kit
```

2. **启动依赖服务**

```bash
docker-compose up -d postgres redis ollama
```

3. **下载 Ollama 模型**

```bash
docker exec -it spring-ai-ollama ollama pull qwen2.5:7b
```

4. **编译项目**

```bash
mvn clean package -DskipTests
```

5. **启动应用**

```bash
java -jar boot/target/boot-1.0.0.jar
```

6. **访问应用**

- Swagger UI: http://localhost:8080/swagger-ui
- API Docs: http://localhost:8080/v3/api-docs

---

## 🐳 Docker 部署

### 一键部署

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f spring-ai-dev-kit

# 停止服务
docker-compose down
```

### 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| Nginx | 80 | 反向代理 |
| Spring AI Dev Kit | 8080 | 应用服务 |
| PostgreSQL | 5432 | 数据库 |
| Redis | 6379 | 缓存 |
| Ollama | 11434 | 大模型服务 |

---

## 📖 完整文档

- [快速开始](QUICKSTART.md) - 5 分钟快速体验
- [配置说明](CONFIG.md) - 详细的配置文档
- [工具使用](TOOLS.md) - MCP 工具使用指南
- [系统对接](INTEGRATION.md) - 如何对接到任何系统
- [测试指南](TESTING.md) - 完整的测试流程
- [检查清单](CHECKLIST.md) - 代码完整性检查
- [开发文档](DEVELOPMENT.md) - 开发指南
- [架构设计](ARCHITECTURE.md) - 系统架构说明

## 📚 API 使用示例

### 智能对话（SSE 流式，自动调用工具）

```bash
# AI 会自动判断是否需要调用工具
curl -N "http://localhost:8080/api/agent/chat?message=查询最近1小时的错误日志"

# AI 会自动调用 ELK 工具查询日志
curl -N "http://localhost:8080/api/agent/chat?message=查看服务器192.168.1.100的CPU使用率"

# AI 会自动调用 Prometheus 工具查询监控
curl -N "http://localhost:8080/api/agent/chat?message=帮我评审这段代码：public void test() { ... }"

# AI 会自动调用代码评审工具
```

---

## 🔧 配置说明

### 快速配置（只需配置这些即可使用）

复制 `env.example` 为 `.env`，修改以下配置：

```bash
# 大模型配置（二选一）
OLLAMA_BASE_URL=http://localhost:11434  # 使用本地 Ollama
OLLAMA_MODEL=qwen2.5:7b

# 或使用 OpenAI
OPENAI_API_KEY=sk-your-api-key-here

# MCP 工具配置（按需配置）
ELK_BASE_URL=http://your-elk:9200
ELK_USERNAME=elastic
ELK_PASSWORD=your-password

PROMETHEUS_BASE_URL=http://your-prometheus:9090
```

详细配置说明请查看 [CONFIG.md](CONFIG.md)

---

## 🛣️ Roadmap

- [x] RAG 基础能力
- [x] MCP 工具集成
- [x] AI Agent 编排
- [x] Docker 部署
- [ ] 前端 UI 界面
- [ ] 更多 MCP 工具
- [ ] 多模态支持
- [ ] 分布式部署

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

---

## 📄 License

本项目采用 Apache 2.0 许可证 - 详见 [LICENSE](LICENSE) 文件

---

## 📧 联系方式

- 项目主页：https://github.com/your-repo/spring-ai-dev-kit
- Issue 反馈：https://github.com/your-repo/spring-ai-dev-kit/issues

---

<div align="center">

**如果这个项目对你有帮助，请给个 ⭐️ Star 支持一下！**

Made with ❤️ by Spring AI Dev Kit Team

</div>

