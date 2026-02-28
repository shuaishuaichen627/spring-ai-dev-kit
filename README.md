# Spring AI Dev Kit

<div align="center">

![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0-green)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![License](https://img.shields.io/badge/License-Apache%202.0-blue)

**基于 Spring AI 的智能研发助手，整合 RAG、MCP 和 AI Agent 能力**

[快速开始](#-快速启动) • [功能特性](#-功能特性) • [系统对接](#-系统对接) • [文档](#-完整文档)

</div>

---

## 📖 项目简介

Spring AI Dev Kit 是一个企业级智能研发助手平台，基于 Spring AI 框架构建，整合了 RAG（检索增强生成）、MCP（模型上下文协议）和 AI Agent 三大核心能力。

### 🎯 核心特性

- **真实系统对接**：可直接对接任何企业的 ELK 和 Prometheus 系统
- **AI 自动识别**：根据用户问题自动调用相应的工具
- **灵活配置**：只需修改配置文件即可对接不同系统
- **完善文档**：从快速开始到系统对接，应有尽有

### ✨ 功能特性

#### 🔍 RAG 知识库
- ✅ Git 代码库自动拉取与解析
- ✅ 文档解析（Markdown、PDF）
- ✅ PGVector 向量数据库存储
- ✅ 语义相似度检索

#### 🛠️ MCP 工具集
- ✅ **ELK 日志查询工具**（真实对接 Elasticsearch）
- ✅ **Prometheus 监控工具**（真实对接 Prometheus）
- ✅ SQL 报表导出工具
- ✅ 代码评审工具
- ✅ 基于 Spring AI @Tool 注解
- ✅ AI 自动识别并调用工具

#### 🤖 AI Agent
- ✅ Ollama / OpenAI 大模型支持
- ✅ SSE 流式响应
- ✅ RAG + MCP 智能编排
- ✅ 上下文感知对话

---

## 🚀 快速启动

### 前置要求
- Java 17+
- Maven 3.8+
- Docker & Docker Compose

### 方式一：Docker 一键部署（推荐）

```bash
# 1. 克隆项目
git clone https://github.com/your-username/spring-ai-dev-kit.git
cd spring-ai-dev-kit

# 2. 配置环境变量
cp env.example .env
# 编辑 .env 文件，填入你的配置

# 3. 一键部署
chmod +x deploy.sh
./deploy.sh

# 4. 访问应用
# http://localhost/swagger-ui
```

### 方式二：本地开发

```bash
# 1. 启动依赖服务
docker-compose up -d postgres redis ollama

# 2. 下载模型
docker exec -it spring-ai-ollama ollama pull qwen2.5:7b

# 3. 编译运行
mvn clean install
java -jar boot/target/boot-1.0.0.jar
```

---

## 🔌 系统对接

### ELK 日志系统对接

只需配置以下环境变量：

```bash
ELK_BASE_URL=http://your-elasticsearch:9200
ELK_USERNAME=elastic
ELK_PASSWORD=your-password
ELK_INDEX_PATTERN=logs-*
```

**使用示例：**
```bash
curl -N "http://localhost:8080/api/agent/chat?message=查询最近1小时的ERROR日志"
```

### Prometheus 监控系统对接

只需配置以下环境变量：

```bash
PROMETHEUS_BASE_URL=http://your-prometheus:9090
```

**使用示例：**
```bash
curl -N "http://localhost:8080/api/agent/chat?message=查询node_cpu_seconds_total指标"
```

详细对接说明请查看 [INTEGRATION.md](INTEGRATION.md)

---

## 📚 API 使用示例

### 智能对话（SSE 流式，自动调用工具）

```bash
# AI 会自动判断是否需要调用工具
curl -N "http://localhost:8080/api/agent/chat?message=查询最近1小时的错误日志"

# AI 会自动调用 ELK 工具查询日志
curl -N "http://localhost:8080/api/agent/chat?message=查看服务器192.168.1.100的CPU使用率"

# AI 会自动调用 Prometheus 工具查询监控
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

## 🏗️ 架构设计

### 模块划分

```
spring-ai-dev-kit
├── common          # 公共模块（异常处理、工具类、统一返回）
├── rag             # RAG 知识库模块
├── mcp             # MCP 工具模块（真实对接 ELK 和 Prometheus）
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
| Elasticsearch | 8.x | 日志系统 |
| Prometheus | 2.x | 监控系统 |
| Redis | 7.x | 缓存 |
| Docker | Latest | 容器化部署 |

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
- [项目总结](SUMMARY.md) - 项目交付总结

---

## 🛣️ Roadmap

- [x] RAG 基础能力
- [x] MCP 工具集成（真实对接）
- [x] AI Agent 编排
- [x] Docker 部署
- [x] ELK 日志查询（真实对接）
- [x] Prometheus 监控查询（真实对接）
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

详见 [CONTRIBUTING.md](CONTRIBUTING.md)

---

## 📄 License

本项目采用 Apache 2.0 许可证 - 详见 [LICENSE](LICENSE) 文件

---

## 📧 联系方式

- 项目主页：https://github.com/your-username/spring-ai-dev-kit
- Issue 反馈：https://github.com/your-username/spring-ai-dev-kit/issues

---

## 🌟 Star History

如果这个项目对你有帮助，请给个 ⭐️ Star 支持一下！

---

<div align="center">

**Made with ❤️ by Spring AI Dev Kit Team**

</div>
