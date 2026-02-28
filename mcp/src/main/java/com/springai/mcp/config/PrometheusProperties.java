package com.springai.mcp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Prometheus 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "mcp.tools.prometheus")
public class PrometheusProperties {
    
    /**
     * 是否启用
     */
    private Boolean enabled = true;
    
    /**
     * Prometheus 地址
     */
    private String baseUrl = "http://localhost:9090";
    
    /**
     * 用户名（如果需要认证）
     */
    private String username;
    
    /**
     * 密码（如果需要认证）
     */
    private String password;
    
    /**
     * 连接超时（毫秒）
     */
    private Integer connectTimeout = 5000;
    
    /**
     * 读取超时（毫秒）
     */
    private Integer readTimeout = 30000;
}

