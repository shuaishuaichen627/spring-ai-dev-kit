#!/bin/bash

# Spring AI Dev Kit - 项目验证脚本
# 用于验证所有改进是否正确实施

echo "=========================================="
echo "Spring AI Dev Kit - 项目验证"
echo "=========================================="
echo ""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查计数
PASS=0
FAIL=0

# 检查函数
check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} $2"
        ((PASS++))
    else
        echo -e "${RED}✗${NC} $2 (文件不存在: $1)"
        ((FAIL++))
    fi
}

check_dir() {
    if [ -d "$1" ]; then
        echo -e "${GREEN}✓${NC} $2"
        ((PASS++))
    else
        echo -e "${RED}✗${NC} $2 (目录不存在: $1)"
        ((FAIL++))
    fi
}

echo "1. 检查示例代码..."
echo "-------------------"
check_dir "sample" "sample 目录"
check_file "sample/README.md" "示例说明文档"
check_file "sample/pom.xml" "示例模块 POM"
check_file "sample/src/main/java/com/springai/sample/agent/AgentSimpleDemo.java" "Agent 示例"
check_file "sample/src/main/java/com/springai/sample/mcp/McpSimpleDemo.java" "MCP 示例"
check_file "sample/src/main/java/com/springai/sample/rag/RagSimpleDemo.java" "RAG 示例"
echo ""

echo "2. 检查配置管理..."
echo "-------------------"
check_file "boot/src/main/resources/application.yml" "主配置文件"
check_file "boot/src/main/resources/application-dev.yml" "开发环境配置"
check_file "boot/src/main/resources/application-test.yml" "测试环境配置"
check_file "boot/src/main/resources/application-prod.yml" "生产环境配置"
echo ""

echo "3. 检查 Docker 配置..."
echo "-------------------"
check_file "Dockerfile" "Dockerfile"
check_file "docker-compose.yml" "Docker Compose 配置"

# 检查 Dockerfile 是否使用固定版本
if grep -q "openjdk:17-jdk-slim-bullseye" Dockerfile; then
    echo -e "${GREEN}✓${NC} Dockerfile 使用固定版本"
    ((PASS++))
else
    echo -e "${RED}✗${NC} Dockerfile 未使用固定版本"
    ((FAIL++))
fi

# 检查 docker-compose.yml 是否使用固定版本
if grep -q "ankane/pgvector:v0.5.1" docker-compose.yml && \
   grep -q "redis:7.2.3-alpine" docker-compose.yml; then
    echo -e "${GREEN}✓${NC} Docker Compose 使用固定版本"
    ((PASS++))
else
    echo -e "${RED}✗${NC} Docker Compose 未使用固定版本"
    ((FAIL++))
fi
echo ""

echo "4. 检查 CI/CD 配置..."
echo "-------------------"
check_dir ".github/workflows" "GitHub Actions 目录"
check_file ".github/workflows/ci-cd.yml" "CI/CD 工作流"
check_file ".github/workflows/code-review.yml" "代码审查工作流"
check_file ".github/workflows/release.yml" "发布工作流"
echo ""

echo "5. 检查测试体系..."
echo "-------------------"
check_file "mcp/src/test/java/com/springai/mcp/tools/CodeReviewToolTest.java" "CodeReviewTool 测试"
check_file "mcp/src/test/java/com/springai/mcp/tools/SqlReportToolTest.java" "SqlReportTool 测试"
check_file "agent/src/test/java/com/springai/agent/service/AgentServiceTest.java" "AgentService 测试"
check_file "rag/src/test/java/com/springai/rag/service/VectorStoreServiceTest.java" "VectorStoreService 测试"
check_file "boot/src/test/java/com/springai/boot/integration/AgentIntegrationTest.java" "Agent 集成测试"
check_file "boot/src/test/resources/application-test.yml" "测试配置文件"
check_file "TESTING_GUIDE.md" "测试指南文档"

# 检查 pom.xml 是否包含测试插件
if grep -q "jacoco-maven-plugin" pom.xml; then
    echo -e "${GREEN}✓${NC} Maven 配置包含 JaCoCo 插件"
    ((PASS++))
else
    echo -e "${RED}✗${NC} Maven 配置缺少 JaCoCo 插件"
    ((FAIL++))
fi
echo ""

echo "6. 检查文档..."
echo "-------------------"
check_file "README.md" "项目说明"
check_file "IMPROVEMENTS.md" "改进总结"
check_file "TESTING_GUIDE.md" "测试指南"
check_file "QUICKSTART.md" "快速开始"
check_file "CONFIG.md" "配置文档"
check_file "TOOLS.md" "工具文档"
echo ""

echo "=========================================="
echo "验证结果"
echo "=========================================="
echo -e "通过: ${GREEN}${PASS}${NC}"
echo -e "失败: ${RED}${FAIL}${NC}"
echo ""

if [ $FAIL -eq 0 ]; then
    echo -e "${GREEN}🎉 所有检查通过！项目改进完成！${NC}"
    exit 0
else
    echo -e "${YELLOW}⚠️  有 ${FAIL} 项检查失败，请检查上述错误${NC}"
    exit 1
fi

