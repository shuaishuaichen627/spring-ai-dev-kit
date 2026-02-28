package com.springai.mcp.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.springai.mcp.config.PrometheusProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.Tool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;

/**
 * Prometheus 监控查询工具
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(name = "prometheusWebClient")
public class PrometheusTool {

    private final WebClient prometheusWebClient;
    private final PrometheusProperties prometheusProperties;

    @Tool(description = """
            查询 Prometheus 监控系统中的指标数据。
            用于性能分析、故障排查和容量规划。
            支持 CPU、内存、网络等各类系统指标查询。
            参数说明：
            - metric: 监控指标名称，如 node_cpu_seconds_total、node_memory_MemAvailable_bytes、up 等
            - instance: 实例标识，如 localhost:9100、192.168.1.100:9100
            - timeRange: 查询时间范围（分钟），默认 5 分钟
            """)
    public String queryPrometheusMetrics(
            String metric,
            String instance,
            Integer timeRange) {
        
        log.info("执行 Prometheus 查询：metric={}, instance={}, timeRange={}", 
                metric, instance, timeRange);
        
        try {
            int range = timeRange != null ? timeRange : 5;
            
            // 构建 PromQL 查询
            String query = buildPromQL(metric, instance);
            
            // 查询即时数据
            JsonNode instantResult = queryInstant(query);
            
            // 查询范围数据（用于计算平均值、最大值等）
            JsonNode rangeResult = queryRange(query, range);
            
            // 格式化结果
            return formatPrometheusResult(metric, instance, range, instantResult, rangeResult);
            
        } catch (Exception e) {
            log.error("Prometheus 查询失败", e);
            return String.format("""
                    ===== Prometheus 监控查询失败 =====
                    错误信息：%s
                    
                    请检查：
                    1. Prometheus 服务是否正常运行
                    2. 配置的地址是否正确：%s
                    3. 指标名称是否正确：%s
                    4. 实例是否存在：%s
                    """, e.getMessage(), prometheusProperties.getBaseUrl(), metric, instance);
        }
    }
    
    /**
     * 构建 PromQL 查询语句
     */
    private String buildPromQL(String metric, String instance) {
        if (instance != null && !instance.isEmpty()) {
            return String.format("%s{instance=\"%s\"}", metric, instance);
        }
        return metric;
    }
    
    /**
     * 查询即时数据
     */
    private JsonNode queryInstant(String query) {
        return prometheusWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/query")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
    
    /**
     * 查询范围数据
     */
    private JsonNode queryRange(String query, int rangeMinutes) {
        long end = Instant.now().getEpochSecond();
        long start = end - (rangeMinutes * 60L);
        
        return prometheusWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/query_range")
                        .queryParam("query", query)
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("step", "15s")  // 15秒间隔
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
    
    /**
     * 格式化 Prometheus 查询结果
     */
    private String formatPrometheusResult(String metric, String instance, int range,
                                         JsonNode instantResult, JsonNode rangeResult) {
        StringBuilder result = new StringBuilder();
        result.append("===== Prometheus 监控查询结果 =====\n");
        result.append(String.format("监控指标：%s\n", metric));
        result.append(String.format("实例：%s\n", instance != null ? instance : "全部"));
        result.append(String.format("时间范围：最近 %d 分钟\n", range));
        result.append(String.format("数据源：%s\n\n", prometheusProperties.getBaseUrl()));
        
        // 解析即时数据
        if (instantResult != null && instantResult.has("data")) {
            JsonNode data = instantResult.get("data");
            if (data.has("result") && data.get("result").isArray() && data.get("result").size() > 0) {
                JsonNode firstResult = data.get("result").get(0);
                
                // 当前值
                if (firstResult.has("value") && firstResult.get("value").isArray()) {
                    String currentValue = firstResult.get("value").get(1).asText();
                    result.append(String.format("当前值：%s\n", formatValue(metric, currentValue)));
                }
                
                // 标签信息
                if (firstResult.has("metric")) {
                    JsonNode metricLabels = firstResult.get("metric");
                    result.append("\n标签信息：\n");
                    metricLabels.fields().forEachRemaining(entry -> {
                        result.append(String.format("  %s: %s\n", entry.getKey(), entry.getValue().asText()));
                    });
                }
            } else {
                result.append("当前值：无数据\n");
            }
        }
        
        // 解析范围数据并计算统计信息
        if (rangeResult != null && rangeResult.has("data")) {
            JsonNode data = rangeResult.get("data");
            if (data.has("result") && data.get("result").isArray() && data.get("result").size() > 0) {
                JsonNode firstResult = data.get("result").get(0);
                
                if (firstResult.has("values") && firstResult.get("values").isArray()) {
                    JsonNode values = firstResult.get("values");
                    
                    double sum = 0;
                    double max = Double.MIN_VALUE;
                    double min = Double.MAX_VALUE;
                    int count = 0;
                    
                    for (JsonNode value : values) {
                        if (value.isArray() && value.size() > 1) {
                            double val = value.get(1).asDouble();
                            sum += val;
                            max = Math.max(max, val);
                            min = Math.min(min, val);
                            count++;
                        }
                    }
                    
                    if (count > 0) {
                        double avg = sum / count;
                        result.append("\n统计信息：\n");
                        result.append(String.format("  平均值：%s\n", formatValue(metric, String.valueOf(avg))));
                        result.append(String.format("  最大值：%s\n", formatValue(metric, String.valueOf(max))));
                        result.append(String.format("  最小值：%s\n", formatValue(metric, String.valueOf(min))));
                        result.append(String.format("  数据点数：%d\n", count));
                        
                        // 趋势分析
                        result.append("\n趋势分析：\n");
                        if (max - min < avg * 0.1) {
                            result.append("  指标在观察期内保持稳定，波动较小\n");
                        } else if (max - min > avg * 0.5) {
                            result.append("  指标在观察期内波动较大，建议关注\n");
                        } else {
                            result.append("  指标在观察期内有一定波动，属于正常范围\n");
                        }
                    }
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * 格式化数值（根据指标类型）
     */
    private String formatValue(String metric, String value) {
        try {
            double val = Double.parseDouble(value);
            
            // CPU 相关指标（百分比）
            if (metric.contains("cpu") && !metric.contains("seconds")) {
                return String.format("%.2f%%", val * 100);
            }
            
            // 内存相关指标（字节转换）
            if (metric.contains("memory") || metric.contains("bytes")) {
                return formatBytes(val);
            }
            
            // 时间相关指标（秒）
            if (metric.contains("seconds") || metric.contains("duration")) {
                return String.format("%.2f 秒", val);
            }
            
            // 默认格式
            return String.format("%.2f", val);
        } catch (NumberFormatException e) {
            return value;
        }
    }
    
    /**
     * 格式化字节数
     */
    private String formatBytes(double bytes) {
        if (bytes < 1024) {
            return String.format("%.2f B", bytes);
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024 * 1024 * 1024));
        }
    }
}
