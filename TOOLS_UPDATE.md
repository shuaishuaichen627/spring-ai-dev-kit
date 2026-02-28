# MCP 工具功能更新说明

## 🎉 工具功能已完善

### 1. SQL 报表导出工具（SqlReportTool）

#### ✅ 真实功能
- **真实 SQL 查询**：连接数据库执行真实的 SQL 查询
- **CSV 导出**：使用 OpenCSV 导出为 CSV 格式
- **Excel 导出**：使用 Apache POI 导出为 Excel 格式（.xlsx）
- **安全检查**：只允许 SELECT 查询，防止数据被修改
- **数据源支持**：支持独立的报表数据源或使用主数据源
- **超时控制**：可配置查询超时时间
- **行数限制**：可配置最大查询行数，防止内存溢出

#### 配置说明

```yaml
mcp:
  tools:
    sql-report:
      enabled: true
      # 报表数据源（留空则使用主数据源）
      datasource-url: jdbc:postgresql://localhost:5432/your_db
      datasource-username: your_username
      datasource-password: your_password
      # 报表导出目录
      export-dir: ./reports
      # 最大查询行数
      max-rows: 10000
      # 查询超时时间（秒）
      query-timeout: 60
```

#### 使用示例

```bash
# 导出 CSV 报表
curl -N "http://localhost:8080/api/agent/chat?message=导出用户表的数据为CSV格式，SQL是SELECT * FROM users LIMIT 100"

# 导出 Excel 报表
curl -N "http://localhost:8080/api/agent/chat?message=导出订单统计报表为Excel格式，SQL是SELECT date, count(*) as count FROM orders GROUP BY date"
```

#### 功能特性

1. **自动格式化**
   - CSV：自动添加列名
   - Excel：自动添加表头样式、自动调整列宽

2. **文件命名**
   - 格式：`报表名称_时间戳.格式`
   - 示例：`user_report_20240101_120000.csv`

3. **错误处理**
   - SQL 语法错误提示
   - 连接失败提示
   - 权限不足提示

4. **安全特性**
   - 只允许 SELECT 查询
   - 防止 SQL 注入
   - 查询超时保护
   - 行数限制保护

---

### 2. 代码评审工具（CodeReviewTool）

#### ✅ 真实功能
- **Java 深度分析**：使用 JavaParser 进行 AST 分析
- **代码风格检查**：命名规范、代码格式
- **性能检查**：循环优化、字符串拼接、方法复杂度
- **安全检查**：空指针风险、异常处理
- **通用检查**：方法长度、注释覆盖
- **多语言支持**：Java（深度分析）、Python、JavaScript（基础检查）

#### 使用示例

```bash
# Java 代码评审
curl -N "http://localhost:8080/api/agent/chat?message=帮我评审这段Java代码：public class Test { public void test() { String s = ''; for(int i=0; i<100; i++) { s += i; } } }"

# Python 代码评审
curl -N "http://localhost:8080/api/agent/chat?message=评审这段Python代码，语言是python，代码是：def test(): pass"

# 指定评审重点
curl -N "http://localhost:8080/api/agent/chat?message=评审这段代码的性能，focus是performance"
```

#### 检查项目

**Java 代码（深度分析）：**

1. **代码风格**
   - 类名命名规范（大驼峰）
   - 方法名命名规范（小驼峰）
   - 变量名命名规范
   - 常量命名规范

2. **性能问题**
   - 循环中的字符串拼接
   - 方法圈复杂度（建议 < 10）
   - 不必要的对象创建

3. **安全问题**
   - 空指针风险
   - 异常处理缺失
   - 资源未关闭

4. **通用问题**
   - 方法过长（建议 < 50 行）
   - 缺少注释
   - 代码重复

**其他语言（基础检查）：**

1. **通用检查**
   - 文件长度
   - 行长度
   - TODO/FIXME 标记

2. **Python 特定**
   - 裸 except 语句
   - 函数组织

3. **JavaScript 特定**
   - var vs let/const
   - == vs ===

#### 评分规则

- 基础分：100 分
- 每个问题：-10 分
- 每个建议：-3 分
- 最终分数：0-100 分

#### 输出示例

```
===== 代码评审结果 =====
编程语言：java
评审重点：all
代码质量评分：73/100

发现的问题（3 个）：
1. 方法 'test' 中在循环内使用字符串拼接，建议使用 StringBuilder
2. 变量名 's' 过短，建议使用更有意义的名称
3. 方法 'test' 较复杂但没有异常处理，建议添加 try-catch

优化建议（2 个）：
1. 建议为复杂逻辑添加注释
2. 方法 'test' 的圈复杂度为 8，建议考虑简化逻辑

总结：代码质量良好，建议处理上述问题以进一步提升
```

---

## 📦 新增依赖

### MCP 模块 pom.xml

```xml
<!-- JDBC 支持（用于 SQL 报表） -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>

<!-- Apache POI（用于 Excel 导出） -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>

<!-- OpenCSV（用于 CSV 导出） -->
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.9</version>
</dependency>

<!-- JavaParser（用于代码分析） -->
<dependency>
    <groupId>com.github.javaparser</groupId>
    <artifactId>javaparser-symbol-solver-core</artifactId>
    <version>3.25.8</version>
</dependency>
```

---

## 🚀 使用指南

### SQL 报表导出

1. **配置数据源**（可选，不配置则使用主数据源）
```bash
REPORT_DB_URL=jdbc:postgresql://localhost:5432/your_db
REPORT_DB_USERNAME=your_username
REPORT_DB_PASSWORD=your_password
```

2. **创建导出目录**
```bash
mkdir reports
```

3. **使用 AI 导出报表**
```bash
curl -N "http://localhost:8080/api/agent/chat?message=导出用户数据，SQL是SELECT * FROM users，格式是excel，报表名称是user_report"
```

4. **查看导出的文件**
```bash
ls reports/
# 输出：user_report_20240101_120000.xlsx
```

### 代码评审

1. **直接通过 AI 对话**
```bash
curl -N "http://localhost:8080/api/agent/chat?message=帮我评审这段代码：[你的代码]"
```

2. **指定语言和重点**
```bash
curl -N "http://localhost:8080/api/agent/chat?message=评审这段Java代码的性能问题：[你的代码]"
```

---

## 🎯 测试场景

### 场景 1：导出数据库报表

```bash
# 用户问：导出最近一周的订单数据
# AI 会自动调用 SqlReportTool
# 生成 CSV 或 Excel 文件
```

### 场景 2：代码质量检查

```bash
# 用户问：帮我看看这段代码有什么问题
# AI 会自动调用 CodeReviewTool
# 返回详细的代码评审报告
```

---

## ⚠️ 注意事项

### SQL 报表工具

1. **安全性**
   - 只允许 SELECT 查询
   - 不允许 INSERT、UPDATE、DELETE 等修改操作
   - 建议使用只读账号

2. **性能**
   - 默认最大查询 10000 行
   - 查询超时时间 60 秒
   - 大数据量建议分批导出

3. **权限**
   - 确保导出目录有写入权限
   - 确保数据库账号有查询权限

### 代码评审工具

1. **语言支持**
   - Java：完整的 AST 分析
   - Python、JavaScript：基础检查
   - 其他语言：通用检查

2. **代码格式**
   - Java 代码需要能够正常编译
   - 建议提供完整的类或方法

3. **评审重点**
   - `style`：代码风格
   - `performance`：性能
   - `security`：安全
   - `all`：全部（默认）

---

## 📊 功能对比

| 功能 | 之前 | 现在 |
|------|------|------|
| SQL 报表 | 返回固定结果 | ✅ 真实查询和导出 |
| 代码评审 | 返回固定结果 | ✅ 真实的 AST 分析 |
| 文件导出 | ❌ 不支持 | ✅ CSV/Excel 导出 |
| 多语言支持 | ❌ 不支持 | ✅ Java/Python/JS |
| 安全检查 | ❌ 无 | ✅ SQL 注入防护 |
| 性能优化 | ❌ 无 | ✅ 超时和行数限制 |

---

**现在两个工具都已经完全可用，可以处理真实的业务场景！** 🎉

