#!/bin/bash

echo "=========================================="
echo "  Spring AI Dev Kit 部署脚本"
echo "=========================================="

# 检查 Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装，请先安装 Docker"
    exit 1
fi

# 检查 Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose 未安装，请先安装 Docker Compose"
    exit 1
fi

echo "✅ Docker 环境检查通过"

# 编译项目
echo ""
echo "📦 开始编译项目..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ 项目编译失败"
    exit 1
fi

echo "✅ 项目编译成功"

# 启动服务
echo ""
echo "🚀 启动 Docker 服务..."
docker-compose up -d

if [ $? -ne 0 ]; then
    echo "❌ Docker 服务启动失败"
    exit 1
fi

echo "✅ Docker 服务启动成功"

# 等待服务就绪
echo ""
echo "⏳ 等待服务启动..."
sleep 10

# 下载 Ollama 模型
echo ""
echo "📥 下载 Ollama 模型..."
docker exec -it spring-ai-ollama ollama pull qwen2.5:7b

echo ""
echo "=========================================="
echo "  🎉 部署完成！"
echo "=========================================="
echo ""
echo "访问地址："
echo "  - 应用首页: http://localhost"
echo "  - Swagger UI: http://localhost/swagger-ui"
echo "  - API Docs: http://localhost/v3/api-docs"
echo ""
echo "查看日志："
echo "  docker-compose logs -f spring-ai-dev-kit"
echo ""
echo "停止服务："
echo "  docker-compose down"
echo ""

