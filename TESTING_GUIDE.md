# 测试指南

本文档介绍 Spring AI Dev Kit 的测试体系和最佳实践。

## 📋 目录

- [测试架构](#测试架构)
- [运行测试](#运行测试)
- [单元测试](#单元测试)
- [集成测试](#集成测试)
- [代码覆盖率](#代码覆盖率)
- [测试最佳实践](#测试最佳实践)

## 🏗️ 测试架构

### 测试分层

```
tests/
├── 单元测试 (Unit Tests)
│   ├── Service 层测试
│   ├── Tool 层测试
│   └── Util 层测试
├── 集成测试 (Integration Tests)
│   ├── API 集成测试
│   ├── 数据库集成测试
│   └── 外部服务集成测试
└── 端到端测试 (E2E Tests)
    └── 完整业务流程测试
```

### 测试技术栈

- **JUnit 5** - 测试框架
- **Mockito** - Mock 框架
- **Spring Boot Test** - Spring 测试支持
- **JaCoCo** - 代码覆盖率
- **Testcontainers** - 容器化测试（可选）

## 🚀 运行测试

### 运行所有测试

```bash
# 运行所有单元测试
mvn test

# 运行所有测试（包括集成测试）
mvn verify

# 跳过测试
mvn clean package -DskipTests
```

### 运行特定模块的测试

```bash
# 只测试 MCP 模块
mvn test -pl mcp

# 只测试 Agent 模块
mvn test -pl agent

# 测试多个模块
mvn test -pl mcp,agent
```

### 运行特定测试类

```bash
# 运行单个测试类
mvn test -Dtest=CodeReviewToolTest

# 运行多个测试类
mvn test -Dtest=CodeReviewToolTest,SqlReportToolTest

# 运行特定测试方法
mvn test -Dtest=CodeReviewToolTest#testReviewCode_Success
```

### 运行集成测试

```bash
# 运行集成测试
mvn integration-test

# 运行并验证
mvn verify
```

## 🧪 单元测试

### 测试结构

```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    
    @Mock
    private Dependency dependency;
    
    @InjectMocks
    private ServiceUnderTest service;
    
    @BeforeEach
    void setUp() {
        // 初始化
    }
    
    @Test
    void testMethod_Success() {
        // Given
        when(dependency.method()).thenReturn(value);
        
        // When
        Result result = service.method();
        
        // Then
        assertNotNull(result);
        verify(dependency).method();
    }
}
```

### 现有单元测试

#### 1. CodeReviewTool 测试

```bash
# 位置
mcp/src/test/java/com/springai/mcp/tools/CodeReviewToolTest.java

# 测试覆盖
- ✅ 有效 Java 代码审查
- ✅ 命名规范检查
- ✅ 性能问题检测
- ✅ 安全漏洞检测
- ✅ 空文件处理
- ✅ 非 Java 文件处理
- ✅ 文件不存在处理
- ✅ 复杂类审查
```

#### 2. SqlReportTool 测试

```bash
# 位置
mcp/src/test/java/com/springai/mcp/tools/SqlReportToolTest.java

# 测试覆盖
- ✅ SQL 查询执行
- ✅ SQL 注入防护
- ✅ 无效格式处理
- ✅ CSV 导出
- ✅ 最大行数限制
- ✅ SQL 验证
```

#### 3. AgentService 测试

```bash
# 位置
agent/src/test/java/com/springai/agent/service/AgentServiceTest.java

# 测试覆盖
- ✅ 简单对话
- ✅ 流式对话
- ✅ 工具调用
- ✅ 空消息处理
- ✅ 空值处理
```

#### 4. VectorStoreService 测试

```bash
# 位置
rag/src/test/java/com/springai/rag/service/VectorStoreServiceTest.java

# 测试覆盖
- ✅ 添加文档
- ✅ 相似度搜索
- ✅ 删除文档
- ✅ 参数验证
```

## 🔗 集成测试

### 测试结构

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testEndpoint() throws Exception {
        mockMvc.perform(get("/api/endpoint"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.field").value("value"));
    }
}
```

### 现有集成测试

#### AgentIntegrationTest

```bash
# 位置
boot/src/test/java/com/springai/boot/integration/AgentIntegrationTest.java

# 测试覆盖
- ✅ Chat 端点测试
- ✅ 空消息处理
- ✅ 缺失参数处理
- ✅ 健康检查端点
```

### 使用 Testcontainers（推荐）

```java
@SpringBootTest
@Testcontainers
class DatabaseIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb");
    
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Test
    void testDatabaseOperation() {
        // 测试数据库操作
    }
}
```

## 📊 代码覆盖率

### 生成覆盖率报告

```bash
# 运行测试并生成报告
mvn clean test

# 查看报告
open target/site/jacoco/index.html
```

### 覆盖率要求

- **最低要求**: 60% 行覆盖率
- **推荐目标**: 80% 行覆盖率
- **核心模块**: 90% 行覆盖率

### 覆盖率报告位置

```
target/
└── site/
    └── jacoco/
        ├── index.html          # 总览
        ├── jacoco.xml          # XML 格式（CI/CD 使用）
        └── jacoco.csv          # CSV 格式
```

### CI/CD 集成

覆盖率报告会自动上传到 Codecov：

```yaml
# .github/workflows/ci-cd.yml
- name: Upload coverage to Codecov
  uses: codecov/codecov-action@v3
  with:
    files: ./target/site/jacoco/jacoco.xml
```

## 📝 测试最佳实践

### 1. 测试命名规范

```java
// ✅ 好的命名
@Test
void testMethodName_Scenario_ExpectedBehavior()

// 示例
@Test
void testReviewCode_ValidJavaCode_ReturnsSuccessResult()

@Test
void testExecuteQuery_SqlInjection_ThrowsException()
```

### 2. AAA 模式

```java
@Test
void testExample() {
    // Arrange (Given) - 准备测试数据
    String input = "test";
    when(mock.method()).thenReturn(value);
    
    // Act (When) - 执行被测试方法
    String result = service.process(input);
    
    // Assert (Then) - 验证结果
    assertEquals(expected, result);
    verify(mock).method();
}
```

### 3. 测试独立性

```java
// ✅ 每个测试独立
@BeforeEach
void setUp() {
    // 每个测试前重新初始化
    service = new Service();
}

// ❌ 避免测试间依赖
static int counter = 0;  // 不要这样做
```

### 4. 使用 @TempDir

```java
@Test
void testFileOperation(@TempDir Path tempDir) {
    // 使用临时目录，测试后自动清理
    Path file = tempDir.resolve("test.txt");
    Files.writeString(file, "content");
}
```

### 5. 异常测试

```java
// ✅ 使用 assertThrows
@Test
void testInvalidInput_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> {
        service.process(null);
    });
}

// ✅ 验证异常消息
@Test
void testInvalidInput_ThrowsExceptionWithMessage() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        service.process(null);
    });
    assertTrue(exception.getMessage().contains("不能为空"));
}
```

### 6. 参数化测试

```java
@ParameterizedTest
@ValueSource(strings = {"", "  ", "\t", "\n"})
void testEmptyInput(String input) {
    assertThrows(IllegalArgumentException.class, () -> {
        service.process(input);
    });
}

@ParameterizedTest
@CsvSource({
    "1, 2, 3",
    "5, 5, 10",
    "10, -5, 5"
})
void testAddition(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a, b));
}
```

### 7. Mock 最佳实践

```java
// ✅ 只 Mock 外部依赖
@Mock
private ExternalService externalService;

// ✅ 验证交互
verify(externalService, times(1)).method();
verify(externalService, never()).dangerousMethod();

// ✅ 使用 ArgumentCaptor
ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
verify(service).send(captor.capture());
assertEquals("expected", captor.getValue().getData());
```

### 8. 测试数据构建

```java
// ✅ 使用 Builder 模式
User user = User.builder()
    .name("Test User")
    .email("test@example.com")
    .build();

// ✅ 使用测试工厂
class TestDataFactory {
    static User createTestUser() {
        return new User("Test", "test@example.com");
    }
}
```

## 🔍 代码质量检查

### Checkstyle

```bash
# 运行 Checkstyle
mvn checkstyle:check

# 查看报告
open target/site/checkstyle.html
```

### SpotBugs

```bash
# 运行 SpotBugs
mvn spotbugs:check

# 查看报告
open target/site/spotbugs.html
```

### SonarQube（可选）

```bash
# 本地运行 SonarQube
docker run -d --name sonarqube -p 9000:9000 sonarqube:latest

# 分析项目
mvn sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=your-token
```

## 📈 持续改进

### 测试覆盖率目标

| 模块 | 当前覆盖率 | 目标覆盖率 |
|------|-----------|-----------|
| common | - | 80% |
| rag | 60% | 80% |
| mcp | 70% | 85% |
| agent | 65% | 80% |
| config | - | 70% |

### 待补充测试

- [ ] ElkLogTool 单元测试
- [ ] PrometheusTool 单元测试
- [ ] DocumentParser 单元测试
- [ ] GitCodeParser 单元测试
- [ ] RAG 集成测试
- [ ] MCP 工具集成测试
- [ ] 端到端测试

## 🆘 常见问题

### Q: 测试运行很慢怎么办？

A: 
1. 使用 `@MockBean` 替代真实依赖
2. 使用内存数据库（H2）替代 PostgreSQL
3. 并行运行测试：`mvn test -T 4`

### Q: 如何调试测试？

A:
1. 在 IDE 中右键运行测试
2. 使用 `mvn test -Dmaven.surefire.debug` 远程调试
3. 添加日志输出

### Q: 测试环境配置？

A: 使用 `application-test.yml` 配置测试环境，已包含：
- H2 内存数据库
- Mock 的外部服务
- 调试级别日志

---

**持续改进测试，提升代码质量！** 🚀

