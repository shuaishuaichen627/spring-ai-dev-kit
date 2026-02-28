# 快速启动脚本（Windows）
@echo off
echo ========================================
echo   Spring AI Dev Kit 启动脚本
echo ========================================

REM 检查 Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到 Java，请先安装 Java 17+
    pause
    exit /b 1
)

echo [OK] Java 环境检查通过

REM 检查 Maven
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到 Maven，请先安装 Maven
    pause
    exit /b 1
)

echo [OK] Maven 环境检查通过

REM 编译项目
echo.
echo [1/3] 编译项目...
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo [错误] 项目编译失败
    pause
    exit /b 1
)

echo [OK] 项目编译成功

REM 启动应用
echo.
echo [2/3] 启动应用...
echo.
echo ========================================
echo   访问地址：
echo   - Swagger UI: http://localhost:8080/swagger-ui
echo   - API Docs: http://localhost:8080/v3/api-docs
echo ========================================
echo.

cd boot
call mvn spring-boot:run

