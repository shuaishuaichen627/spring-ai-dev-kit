package com.springai.mcp.tools;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.databind.JsonNode;
import com.springai.mcp.config.ElkProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.Tool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ELK 日志查询工具
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(ElasticsearchClient.class)
public class ElkLogTool {

    private final ElasticsearchClient elasticsearchClient;
    private final ElkProperties elkProperties;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Tool(description = """
            查询 ELK 日志系统中的日志记录。
            用于排查系统错误、异常和问题定位。
            支持关键词搜索、时间范围过滤和日志级别筛选。
            参数说明：
            - keyword: 搜索关键词，如 ERROR、Exception、NullPointer 等
            - startTime: 开始时间，格式 yyyy-MM-dd HH:mm:ss
            - endTime: 结束时间，格式 yyyy-MM-dd HH:mm:ss
            - level: 日志级别，如 ERROR、WARN、INFO，可选
            """)
    public String queryElkLogs(
            String keyword,
            String startTime,
            String endTime,
            String level) {
        
        log.info("执行 ELK 日志查询：keyword={}, startTime={}, endTime={}, level={}", 
                keyword, startTime, endTime, level);
        
        try {
            // 构建查询
            SearchRequest.Builder searchBuilder = new SearchRequest.Builder()
                    .index(elkProperties.getIndexPattern())
                    .size(100);  // 最多返回 100 条
            
            // 构建查询条件
            searchBuilder.query(q -> q
                    .bool(b -> {
                        // 关键词匹配
                        if (keyword != null && !keyword.isEmpty()) {
                            b.must(m -> m.multiMatch(mm -> mm
                                    .query(keyword)
                                    .fields("message", "log", "content", "msg")
                            ));
                        }
                        
                        // 日志级别
                        if (level != null && !level.isEmpty()) {
                            b.must(m -> m.match(ma -> ma
                                    .field("level")
                                    .query(level)
                            ));
                        }
                        
                        // 时间范围
                        if (startTime != null && endTime != null) {
                            b.must(m -> m.range(r -> r
                                    .field("@timestamp")
                                    .gte(startTime)
                                    .lte(endTime)
                            ));
                        }
                        
                        return b;
                    })
            );
            
            // 按时间倒序
            searchBuilder.sort(s -> s
                    .field(f -> f
                            .field("@timestamp")
                            .order(co.elastic.clients.elasticsearch._types.SortOrder.Desc)
                    )
            );
            
            // 执行查询
            SearchResponse<JsonNode> response = elasticsearchClient.search(
                    searchBuilder.build(),
                    JsonNode.class
            );
            
            // 格式化结果
            return formatSearchResult(response, keyword, startTime, endTime, level);
            
        } catch (Exception e) {
            log.error("ELK 日志查询失败", e);
            return String.format("""
                    ===== ELK 日志查询失败 =====
                    错误信息：%s
                    
                    请检查：
                    1. Elasticsearch 服务是否正常运行
                    2. 配置的地址是否正确：%s
                    3. 认证信息是否正确
                    4. 索引模式是否存在：%s
                    """, e.getMessage(), elkProperties.getBaseUrl(), elkProperties.getIndexPattern());
        }
    }
    
    /**
     * 格式化查询结果
     */
    private String formatSearchResult(SearchResponse<JsonNode> response, 
                                     String keyword, String startTime, String endTime, String level) {
        StringBuilder result = new StringBuilder();
        result.append("===== ELK 日志查询结果 =====\n");
        result.append(String.format("查询时间范围：%s 至 %s\n", startTime, endTime));
        result.append(String.format("关键词：%s\n", keyword));
        result.append(String.format("日志级别：%s\n", level != null ? level : "全部"));
        result.append(String.format("索引：%s\n", elkProperties.getIndexPattern()));
        result.append(String.format("总命中数：%d 条\n\n", response.hits().total().value()));
        
        if (response.hits().hits().isEmpty()) {
            result.append("未找到匹配的日志记录\n");
        } else {
            result.append("日志详情：\n");
            int count = 1;
            for (Hit<JsonNode> hit : response.hits().hits()) {
                JsonNode source = hit.source();
                if (source != null) {
                    result.append(String.format("\n[%d] ", count++));
                    
                    // 时间戳
                    if (source.has("@timestamp")) {
                        result.append(source.get("@timestamp").asText()).append(" ");
                    }
                    
                    // 日志级别
                    if (source.has("level")) {
                        result.append("[").append(source.get("level").asText()).append("] ");
                    }
                    
                    // 服务名
                    if (source.has("service") || source.has("service.name")) {
                        String serviceName = source.has("service") ? 
                                source.get("service").asText() : 
                                source.get("service.name").asText();
                        result.append("[").append(serviceName).append("] ");
                    }
                    
                    // 日志消息
                    if (source.has("message")) {
                        result.append(source.get("message").asText());
                    } else if (source.has("log")) {
                        result.append(source.get("log").asText());
                    } else if (source.has("content")) {
                        result.append(source.get("content").asText());
                    }
                    
                    result.append("\n");
                    
                    // 限制显示前 20 条
                    if (count > 20) {
                        result.append(String.format("\n... 还有 %d 条日志未显示\n", 
                                response.hits().total().value() - 20));
                        break;
                    }
                }
            }
        }
        
        return result.toString();
    }
}
