# MCP 工具测试示例

## 1. SQL 报表导出工具测试

### 测试准备

```sql
-- 创建测试表
CREATE TABLE test_users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入测试数据
INSERT INTO test_users (name, email) VALUES
('张三', 'zhangsan@example.com'),
('李四', 'lisi@example.com'),
('王五', 'wangwu@example.com');
```

### 测试用例

#### 测试 1：导出 CSV 格式

**用户输入：**
```
导出用户数据为CSV格式，SQL是 SELECT * FROM test_users，报表名称是 user_list
```

**预期结果：**
```
===== SQL 报表导出成功 =====
SQL 语句：SELECT * FROM test_users
导出格式：csv
报表名称：user_list

文件名：user_list_20240101_120000.csv
文件路径：C:\Users\chenyi\Desktop\spring-ai-dev-kit\reports\user_list_20240101_120000.csv
数据行数：3 行
列数：4 列
文件大小：0.25 KB

列名：id, name, email, created_at

报表导出成功！
```

**生成的 CSV 文件内容：**
```csv
id,name,email,created_at
1,张三,zhangsan@example.com,2024-01-01 10:00:00
2,李四,lisi@example.com,2024-01-01 10:00:01
3,王五,wangwu@example.com,2024-01-01 10:00:02
```

#### 测试 2：导出 Excel 格式

**用户输入：**
```
导出用户统计报表为Excel格式，SQL是 SELECT COUNT(*) as total, DATE(created_at) as date FROM test_users GROUP BY DATE(created_at)，报表名称是 user_stats
```

**预期结果：**
- 生成 Excel 文件
- 包含表头样式（灰色背景、粗体）
- 自动调整列宽

#### 测试 3：安全检查（拒绝非 SELECT 语句）

**用户输入：**
```
执行 DELETE FROM test_users
```

**预期结果：**
```
错误：出于安全考虑，只允许执行 SELECT 查询语句
```

#### 测试 4：大数据量测试

**用户输入：**
```
导出所有订单数据，SQL是 SELECT * FROM orders
```

**预期结果：**
- 如果超过 10000 行，只导出前 10000 行
- 提示已达到最大行数限制

---

## 2. 代码评审工具测试

### 测试用例

#### 测试 1：Java 代码 - 性能问题

**用户输入：**
```java
帮我评审这段Java代码：

public class StringTest {
    public String concatenate(List<String> items) {
        String result = "";
        for (String item : items) {
            result += item;
        }
        return result;
    }
}
```

**预期结果：**
```
===== 代码评审结果 =====
编程语言：java
评审重点：all
代码质量评分：67/100

发现的问题（3 个）：
1. 方法 'concatenate' 中在循环内使用字符串拼接，建议使用 StringBuilder
2. 变量名 'result' 在循环中频繁修改，影响性能
3. 方法 'concatenate' 较复杂但没有异常处理，建议添加 try-catch

优化建议（1 个）：
1. 建议为复杂逻辑添加注释

总结：代码质量良好，建议处理上述问题以进一步提升
```

#### 测试 2：Java 代码 - 命名规范问题

**用户输入：**
```java
评审这段代码：

public class user_service {
    private String user_name;
    
    public void SetUserName(String name) {
        this.user_name = name;
    }
}
```

**预期结果：**
```
发现的问题：
1. 类名 'user_service' 应该以大写字母开头
2. 类名 'user_service' 不应包含下划线，建议使用驼峰命名
3. 方法名 'SetUserName' 应该以小写字母开头
4. 变量名 'user_name' 应使用驼峰命名，而不是下划线
```

#### 测试 3：Java 代码 - 方法复杂度

**用户输入：**
```java
评审这段代码的复杂度：

public void processOrder(Order order) {
    if (order != null) {
        if (order.getStatus() == Status.PENDING) {
            if (order.getAmount() > 0) {
                if (order.getCustomer() != null) {
                    if (order.getItems().size() > 0) {
                        for (Item item : order.getItems()) {
                            if (item.getStock() > 0) {
                                // process
                            }
                        }
                    }
                }
            }
        }
    }
}
```

**预期结果：**
```
发现的问题：
1. 方法 'processOrder' 的圈复杂度为 12，建议拆分为多个小方法（建议 < 10）
2. 方法 'processOrder' 有 15 行代码，建议考虑拆分

优化建议：
1. 建议使用卫语句（guard clauses）减少嵌套
2. 建议提取子方法处理订单项
```

#### 测试 4：Python 代码评审

**用户输入：**
```python
评审这段Python代码：

def process_data(data):
    try:
        result = []
        for item in data:
            result.append(item * 2)
        return result
    except:
        return []
```

**预期结果：**
```
发现的问题：
1. 发现裸 except 语句，建议指定具体的异常类型

优化建议：
1. 建议使用列表推导式简化代码
```

#### 测试 5：JavaScript 代码评审

**用户输入：**
```javascript
评审这段JavaScript代码：

function compare(a, b) {
    if (a == b) {
        return true;
    }
    var result = false;
    return result;
}
```

**预期结果：**
```
发现的问题：
1. 建议使用 let 或 const 代替 var

优化建议：
1. 建议使用 === 代替 ==
2. 可以简化逻辑，直接返回比较结果
```

#### 测试 6：优秀代码评审

**用户输入：**
```java
评审这段代码：

/**
 * 用户服务
 */
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * 根据ID查询用户
     */
    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return userRepository.findById(id);
    }
}
```

**预期结果：**
```
===== 代码评审结果 =====
编程语言：java
评审重点：all
代码质量评分：97/100

✅ 代码质量优秀，未发现明显问题！

优化建议（1 个）：
1. 代码包含注释，这是好的实践

总结：代码质量优秀，继续保持！
```

---

## 3. 集成测试

### 场景 1：AI 自动识别并调用工具

**用户输入：**
```
帮我导出最近一周的订单数据，并评审一下我的订单处理代码
```

**预期行为：**
1. AI 识别需要导出数据
2. 自动调用 `SqlReportTool`
3. AI 识别需要评审代码
4. 自动调用 `CodeReviewTool`
5. 返回综合结果

### 场景 2：错误处理

**用户输入：**
```
导出数据，SQL是 SELECT * FROM non_exist_table
```

**预期结果：**
```
===== SQL 报表导出失败 =====
错误类型：SQL 执行错误
错误信息：relation "non_exist_table" does not exist
SQL 状态：42P01
错误代码：0

请检查：
1. SQL 语法是否正确
2. 表名和字段名是否存在
3. 是否有查询权限
```

---

## 4. 性能测试

### SQL 报表工具

```bash
# 测试大数据量导出
time curl -N "http://localhost:8080/api/agent/chat?message=导出10000条数据"

# 预期：
# - 查询时间 < 5秒
# - 文件生成时间 < 2秒
# - 总时间 < 10秒
```

### 代码评审工具

```bash
# 测试大文件评审
time curl -N "http://localhost:8080/api/agent/chat?message=评审这个1000行的Java类"

# 预期：
# - 解析时间 < 3秒
# - 分析时间 < 2秒
# - 总时间 < 5秒
```

---

## 5. 验证清单

### SQL 报表工具

- [ ] 可以执行 SELECT 查询
- [ ] 可以导出 CSV 格式
- [ ] 可以导出 Excel 格式
- [ ] 拒绝非 SELECT 语句
- [ ] 查询超时保护
- [ ] 行数限制保护
- [ ] 文件正确生成
- [ ] 文件内容正确
- [ ] 错误提示清晰

### 代码评审工具

- [ ] 可以评审 Java 代码
- [ ] 可以评审 Python 代码
- [ ] 可以评审 JavaScript 代码
- [ ] 检测命名规范问题
- [ ] 检测性能问题
- [ ] 检测安全问题
- [ ] 计算圈复杂度
- [ ] 评分合理
- [ ] 建议有价值

---

## 6. 故障排查

### SQL 报表工具常见问题

**问题 1：数据源连接失败**
```
错误：未配置数据源，请检查配置
```
**解决方案：**
- 检查 `application.yml` 中的数据源配置
- 确保数据库服务正在运行

**问题 2：导出目录无权限**
```
错误：导出目录没有写入权限
```
**解决方案：**
```bash
chmod 755 reports/
```

**问题 3：查询超时**
```
错误：查询超时
```
**解决方案：**
- 增加 `query-timeout` 配置
- 优化 SQL 查询
- 添加索引

### 代码评审工具常见问题

**问题 1：代码解析失败**
```
错误：代码解析失败，可能存在语法错误
```
**解决方案：**
- 确保代码可以正常编译
- 检查代码格式是否正确

**问题 2：不支持的语言**
```
提示：该语言仅支持基础检查
```
**说明：**
- Java：完整支持
- Python、JavaScript：基础支持
- 其他语言：通用检查

---

**测试完成后，两个工具都应该能够正常工作！** ✅

