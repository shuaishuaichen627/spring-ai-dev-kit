# Spring AI Dev Kit - 系统对接说明

## 🔌 如何对接到任何系统

本系统设计为通用的智能研发助手，可以无缝对接到任何企业的研发和运营系统。

## 1. ELK 日志系统对接

### 支持的 ELK 版本
- Elasticsearch 7.x
- Elasticsearch 8.x
- OpenSearch（兼容）

### 配置步骤

#### 步骤 1：配置连接信息

编辑 `.env` 文件：

```bash
# ELK 配置
MCP_ELK_ENABLED=true
ELK_BASE_URL=http://your-elasticsearch:9200
ELK_USERNAME=your-username
ELK_PASSWORD=your-password
ELK_INDEX_PATTERN=logs-*
```

#### 步骤 2：索引模式说明

`ELK_INDEX_PATTERN` 支持通配符，可以匹配多个索引：

```bash
# 匹配所有以 logs- 开头的索引
ELK_INDEX_PATTERN=logs-*

# 匹配特定应用的日志
ELK_INDEX_PATTERN=app-logs-*

# 匹配多个索引
ELK_INDEX_PATTERN=logs-*,app-*,service-*

# 匹配特定日期的索引
ELK_INDEX_PATTERN=logs-2024-*
```

#### 步骤 3：日志字段映射

系统会自动识别以下常见字段：

| 字段名 | 说明 | 示例 |
|--------|------|------|
| `@timestamp` | 时间戳 | 2024-01-01T10:00:00Z |
| `level` | 日志级别 | ERROR, WARN, INFO |
| `message` | 日志消息 | NullPointerException |
| `log` | 日志内容（备选） | Error occurred |
| `content` | 日志内容（备选） | System error |
| `service` | 服务名称 | user-service |
| `service.name` | 服务名称（备选） | order-service |

**如果你的日志字段不同，可以修改 `ElkLogTool.java` 中的字段映射。**

#### 步骤 4：测试连接

```bash
# 测试 Elasticsearch 连接
curl http://your-elasticsearch:9200/_cluster/health

# 测试查询
curl -N "http://localhost:8080/api/agent/chat?message=查询最近的ERROR日志"
```

### 常见场景

#### 场景 1：查询特定服务的日志
```bash
curl -N "http://localhost:8080/api/agent/chat?message=查询user-service的ERROR日志"
```

#### 场景 2：查询特定时间范围的日志
```bash
curl -N "http://localhost:8080/api/agent/chat?message=查询2024-01-01 10:00:00到11:00:00之间的ERROR日志"
```

#### 场景 3：查询包含特定关键词的日志
```bash
curl -N "http://localhost:8080/api/agent/chat?message=查询包含NullPointerException的日志"
```

## 2. Prometheus 监控系统对接

### 支持的 Prometheus 版本
- Prometheus 2.x
- VictoriaMetrics（兼容）
- Thanos（兼容）

### 配置步骤

#### 步骤 1：配置连接信息

编辑 `.env` 文件：

```bash
# Prometheus 配置
MCP_PROMETHEUS_ENABLED=true
PROMETHEUS_BASE_URL=http://your-prometheus:9090
PROMETHEUS_USERNAME=  # 如果需要认证
PROMETHEUS_PASSWORD=  # 如果需要认证
```

#### 步骤 2：常用指标说明

系统支持查询任何 Prometheus 指标，常见指标包括：

| 指标类型 | 指标名称 | 说明 |
|---------|---------|------|
| CPU | `node_cpu_seconds_total` | CPU 使用时间 |
| CPU | `process_cpu_seconds_total` | 进程 CPU 使用 |
| 内存 | `node_memory_MemAvailable_bytes` | 可用内存 |
| 内存 | `process_resident_memory_bytes` | 进程内存 |
| 网络 | `node_network_receive_bytes_total` | 网络接收字节 |
| 网络 | `node_network_transmit_bytes_total` | 网络发送字节 |
| HTTP | `http_requests_total` | HTTP 请求总数 |
| HTTP | `http_request_duration_seconds` | HTTP 请求耗时 |
| JVM | `jvm_memory_used_bytes` | JVM 内存使用 |
| JVM | `jvm_gc_pause_seconds` | GC 暂停时间 |

#### 步骤 3：实例标识说明

查询时需要指定实例标识（instance），格式通常为：

```
hostname:port
```

示例：
- `localhost:9100`
- `192.168.1.100:9100`
- `node-exporter:9100`

#### 步骤 4：测试连接

```bash
# 测试 Prometheus 连接
curl http://your-prometheus:9090/api/v1/status/config

# 测试查询
curl -N "http://localhost:8080/api/agent/chat?message=查询up指标"
```

### 常见场景

#### 场景 1：查询服务器 CPU 使用率
```bash
curl -N "http://localhost:8080/api/agent/chat?message=查询192.168.1.100的CPU使用率"
```

#### 场景 2：查询内存使用情况
```bash
curl -N "http://localhost:8080/api/agent/chat?message=查询node_memory_MemAvailable_bytes指标"
```

#### 场景 3：查询服务健康状态
```bash
curl -N "http://localhost:8080/api/agent/chat?message=查询up指标，实例是localhost:9100"
```

## 3. 自定义日志字段映射

如果你的日志系统使用了自定义字段，可以修改 `ElkLogTool.java`：

```java
// 在 formatSearchResult 方法中添加自定义字段
if (source.has("your_custom_field")) {
    result.append(source.get("your_custom_field").asText());
}
```

## 4. 自定义 Prometheus 指标

系统支持查询任何 Prometheus 指标，只需在查询时指定指标名称即可。

如果需要自定义指标格式化，可以修改 `PrometheusTool.java` 中的 `formatValue` 方法。

## 5. 多环境配置

### 开发环境

```bash
# .env.dev
ELK_BASE_URL=http://dev-elk:9200
PROMETHEUS_BASE_URL=http://dev-prometheus:9090
```

### 测试环境

```bash
# .env.test
ELK_BASE_URL=http://test-elk:9200
PROMETHEUS_BASE_URL=http://test-prometheus:9090
```

### 生产环境

```bash
# .env.prod
ELK_BASE_URL=http://prod-elk:9200
PROMETHEUS_BASE_URL=http://prod-prometheus:9090
```

使用时指定环境：

```bash
# 加载不同环境的配置
export $(cat .env.prod | xargs)
java -jar boot/target/boot-1.0.0.jar
```

## 6. 安全配置

### ELK 认证

支持以下认证方式：

1. **Basic 认证**（已实现）
```bash
ELK_USERNAME=elastic
ELK_PASSWORD=your-password
```

2. **API Key 认证**（需要修改代码）
```java
// 在 ElkConfig.java 中添加
builder.setDefaultHeaders(new Header[]{
    new BasicHeader("Authorization", "ApiKey " + apiKey)
});
```

### Prometheus 认证

支持以下认证方式：

1. **Basic 认证**（已实现）
```bash
PROMETHEUS_USERNAME=admin
PROMETHEUS_PASSWORD=your-password
```

2. **Bearer Token 认证**（需要修改代码）
```java
// 在 PrometheusConfig.java 中添加
builder.defaultHeader("Authorization", "Bearer " + token);
```

## 7. 性能优化

### ELK 查询优化

```yaml
# application.yml
mcp:
  tools:
    elk:
      connect-timeout: 5000  # 连接超时
      read-timeout: 30000    # 读取超时
      max-results: 100       # 最大返回结果数
```

### Prometheus 查询优化

```yaml
# application.yml
mcp:
  tools:
    prometheus:
      connect-timeout: 5000
      read-timeout: 30000
      step: 15s              # 查询步长
```

## 8. 故障排查

### ELK 连接失败

1. 检查网络连通性
```bash
curl http://your-elk:9200
```

2. 检查认证信息
```bash
curl -u username:password http://your-elk:9200
```

3. 检查索引是否存在
```bash
curl http://your-elk:9200/_cat/indices
```

### Prometheus 连接失败

1. 检查网络连通性
```bash
curl http://your-prometheus:9090/api/v1/status/config
```

2. 检查指标是否存在
```bash
curl 'http://your-prometheus:9090/api/v1/label/__name__/values'
```

## 9. 扩展其他系统

如果需要对接其他系统（如 Grafana、Kibana、Jaeger 等），可以参考 `ElkLogTool.java` 和 `PrometheusTool.java` 的实现方式：

1. 创建配置类（Properties + Config）
2. 创建工具类（使用 @Tool 注解）
3. 实现 HTTP 客户端调用
4. 格式化返回结果

## 10. 最佳实践

1. **使用环境变量**：不要在代码中硬编码配置
2. **启用工具开关**：通过 `enabled` 配置控制工具是否启用
3. **配置超时时间**：避免长时间等待
4. **限制返回结果**：避免返回过多数据
5. **添加错误处理**：确保工具调用失败不影响主流程
6. **记录详细日志**：便于问题排查

---

**现在你可以将本系统对接到任何企业的 ELK 和 Prometheus 系统，只需修改配置文件即可！** 🎉

