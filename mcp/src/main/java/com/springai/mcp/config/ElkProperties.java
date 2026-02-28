package com.springai.mcp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ELK 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "mcp.tools.elk")
public class ElkProperties {
    
    /**
     * 是否启用
     */
    private Boolean enabled = true;
    
    /**
     * Elasticsearch 地址
     */
    private String baseUrl = "http://localhost:9200";
    
    /**
     * 用户名
     */
    private String username = "elastic";
    
    /**
     * 密码
     */
    private String password = "changeme";
    
    /**
     * 索引模式
     */
    private String indexPattern = "logs-*";
    
    /**
     * 连接超时（毫秒）
     */
    private Integer connectTimeout = 5000;
    
    /**
     * 读取超时（毫秒）
     */
    private Integer readTimeout = 30000;
}

