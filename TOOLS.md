# MCP 工具使用指南

## 工具架构说明

在 Spring AI 中，工具通过 `@Tool` 注解定义，Spring AI 会自动识别并在需要时调用。

### 工具定义方式（使用 @Tool 注解）

```java
@Component
public class MyTool {
    
    @Tool(description = """
            工具的功能描述，AI 会根据这个描述判断何时调用此工具。
            描述要清晰、详细，包含使用场景和功能说明。
            """)
    public String myToolMethod(
            String param1,
            Integer param2) {
        // 实现工具逻辑
        return "执行结果";
    }
}
```

### 工具注册

只需要添加 `@Component` 注解，Spring AI 会自动扫描并注册所有带 `@Tool` 注解的方法：

```java
@Component
@RequiredArgsConstructor
public class MyTool {
    
    @Tool(description = "工具描述")
    public String execute(String param) {
        return "result";
    }
}
```

Spring AI 会自动：
1. 扫描所有 `@Component` 类中的 `@Tool` 方法
2. 根据方法签名生成参数描述
3. 在 AI 对话中自动识别并调用

## 内置工具列表

### 1. ELK 日志查询工具 (ElkLogTool)

**功能**：查询 ELK 日志，支持关键词搜索、时间范围过滤

**使用场景**：
- 排查系统错误和异常
- 分析日志趋势
- 定位问题发生时间

**参数**：
- `keyword`: 搜索关键词（如 ERROR、Exception）
- `startTime`: 开始时间（格式：yyyy-MM-dd HH:mm:ss）
- `endTime`: 结束时间（格式：yyyy-MM-dd HH:mm:ss）
- `level`: 日志级别（ERROR、WARN、INFO）

**示例对话**：
```
用户：查询最近 1 小时的错误日志
AI：好的，我来帮你查询 ELK 日志...
[自动调用 ElkLogTool]
```

### 2. Prometheus 监控工具 (PrometheusTool)

**功能**：查询 Prometheus 监控指标，支持 CPU、内存、网络等系统指标

**使用场景**：
- 性能分析
- 故障排查
- 容量规划

**参数**：
- `metric`: 监控指标名称（cpu_usage、memory_usage、network_traffic）
- `instance`: 实例名称或 IP 地址
- `timeRange`: 时间范围（分钟）

**示例对话**：
```
用户：查看服务器 192.168.1.100 的 CPU 使用率
AI：我来查询该服务器的 CPU 监控数据...
[自动调用 PrometheusTool]
```

### 3. SQL 报表导出工具 (SqlReportTool)

**功能**：执行 SQL 查询并导出报表，支持 CSV、Excel 格式

**使用场景**：
- 数据分析
- 报表生成
- 数据导出

**参数**：
- `sql`: SQL 查询语句
- `format`: 导出格式（csv 或 excel）
- `reportName`: 报表名称

**示例对话**：
```
用户：导出最近一周的用户注册数据
AI：我来生成报表...
[自动调用 SqlReportTool]
```

### 4. 代码评审工具 (CodeReviewTool)

**功能**：对代码进行静态分析和评审

**使用场景**：
- 代码质量检查
- 代码规范检查
- 性能优化建议

**参数**：
- `code`: 要评审的代码
- `language`: 编程语言
- `focus`: 评审重点（style、performance、security）

**示例对话**：
```
用户：帮我评审这段代码：[代码内容]
AI：我来分析这段代码...
[自动调用 CodeReviewTool]
```

## 如何添加新工具

### 步骤 1：创建工具类

在 `mcp/src/main/java/com/springai/mcp/tools/` 目录下创建新的工具类：

```java
package com.springai.mcp.tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.Tool;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class YourTool {
    
    @Tool(description = """
            你的工具功能描述。
            AI 会根据这个描述判断何时调用此工具。
            描述要清晰、详细，包含使用场景。
            """)
    public String yourToolMethod(
            String param1,
            Integer param2) {
        
        log.info("执行工具：param1={}, param2={}", param1, param2);
        
        // 实现你的逻辑
        return "执行结果";
    }
}
```

### 步骤 2：无需额外配置

添加 `@Component` 注解后，Spring AI 会自动扫描并注册工具。无需手动配置。

### 步骤 3：测试工具

启动应用后，AI 会自动识别新工具，并在合适的时候调用。

## 工具调用流程

```
用户输入 → Agent 分析 → 判断是否需要工具
                              ↓
                         调用相应工具
                              ↓
                         获取工具结果
                              ↓
                    结合上下文生成回答
                              ↓
                         返回给用户
```

## 最佳实践

1. **清晰的描述**：使用 `@Description` 提供清晰的工具和参数描述
2. **类型安全**：使用 Record 定义请求和响应结构
3. **错误处理**：在工具中处理异常，返回友好的错误信息
4. **日志记录**：记录工具调用日志，便于调试
5. **幂等性**：确保工具可以安全地重复调用

## 常见问题

### Q: 工具没有被调用？

A: 检查以下几点：
1. 工具是否添加了 `@Component` 注解
2. 工具是否在 `AgentService` 中注册
3. 用户的问题是否明确需要该工具
4. 工具的描述是否清晰

### Q: 如何调试工具调用？

A: 查看日志输出，Spring AI 会记录工具调用的详细信息

### Q: 工具可以调用其他工具吗？

A: 可以，但建议保持工具的独立性，复杂逻辑应该在 Agent 层编排

