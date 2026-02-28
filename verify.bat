@echo off
REM Spring AI Dev Kit - 项目验证脚本 (Windows)
REM 用于验证所有改进是否正确实施

echo ==========================================
echo Spring AI Dev Kit - 项目验证
echo ==========================================
echo.

set PASS=0
set FAIL=0

echo 1. 检查示例代码...
echo -------------------
call :check_dir "sample" "sample 目录"
call :check_file "sample\README.md" "示例说明文档"
call :check_file "sample\pom.xml" "示例模块 POM"
call :check_file "sample\src\main\java\com\springai\sample\agent\AgentSimpleDemo.java" "Agent 示例"
call :check_file "sample\src\main\java\com\springai\sample\mcp\McpSimpleDemo.java" "MCP 示例"
call :check_file "sample\src\main\java\com\springai\sample\rag\RagSimpleDemo.java" "RAG 示例"
echo.

echo 2. 检查配置管理...
echo -------------------
call :check_file "boot\src\main\resources\application.yml" "主配置文件"
call :check_file "boot\src\main\resources\application-dev.yml" "开发环境配置"
call :check_file "boot\src\main\resources\application-test.yml" "测试环境配置"
call :check_file "boot\src\main\resources\application-prod.yml" "生产环境配置"
echo.

echo 3. 检查 Docker 配置...
echo -------------------
call :check_file "Dockerfile" "Dockerfile"
call :check_file "docker-compose.yml" "Docker Compose 配置"
echo.

echo 4. 检查 CI/CD 配置...
echo -------------------
call :check_dir ".github\workflows" "GitHub Actions 目录"
call :check_file ".github\workflows\ci-cd.yml" "CI/CD 工作流"
call :check_file ".github\workflows\code-review.yml" "代码审查工作流"
call :check_file ".github\workflows\release.yml" "发布工作流"
echo.

echo 5. 检查测试体系...
echo -------------------
call :check_file "mcp\src\test\java\com\springai\mcp\tools\CodeReviewToolTest.java" "CodeReviewTool 测试"
call :check_file "mcp\src\test\java\com\springai\mcp\tools\SqlReportToolTest.java" "SqlReportTool 测试"
call :check_file "agent\src\test\java\com\springai\agent\service\AgentServiceTest.java" "AgentService 测试"
call :check_file "rag\src\test\java\com\springai\rag\service\VectorStoreServiceTest.java" "VectorStoreService 测试"
call :check_file "boot\src\test\java\com\springai\boot\integration\AgentIntegrationTest.java" "Agent 集成测试"
call :check_file "boot\src\test\resources\application-test.yml" "测试配置文件"
call :check_file "TESTING_GUIDE.md" "测试指南文档"
echo.

echo 6. 检查文档...
echo -------------------
call :check_file "README.md" "项目说明"
call :check_file "IMPROVEMENTS.md" "改进总结"
call :check_file "TESTING_GUIDE.md" "测试指南"
call :check_file "QUICKSTART.md" "快速开始"
call :check_file "CONFIG.md" "配置文档"
call :check_file "TOOLS.md" "工具文档"
echo.

echo ==========================================
echo 验证结果
echo ==========================================
echo 通过: %PASS%
echo 失败: %FAIL%
echo.

if %FAIL% EQU 0 (
    echo 🎉 所有检查通过！项目改进完成！
    exit /b 0
) else (
    echo ⚠️  有 %FAIL% 项检查失败，请检查上述错误
    exit /b 1
)

:check_file
if exist "%~1" (
    echo [✓] %~2
    set /a PASS+=1
) else (
    echo [✗] %~2 ^(文件不存在: %~1^)
    set /a FAIL+=1
)
goto :eof

:check_dir
if exist "%~1\" (
    echo [✓] %~2
    set /a PASS+=1
) else (
    echo [✗] %~2 ^(目录不存在: %~1^)
    set /a FAIL+=1
)
goto :eof

