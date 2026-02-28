package com.springai.mcp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * SQL 报表配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "mcp.tools.sql-report")
public class SqlReportProperties {
    
    /**
     * 是否启用
     */
    private Boolean enabled = true;
    
    /**
     * 报表数据源 URL
     */
    private String datasourceUrl;
    
    /**
     * 数据源用户名
     */
    private String datasourceUsername;
    
    /**
     * 数据源密码
     */
    private String datasourcePassword;
    
    /**
     * 报表导出目录
     */
    private String exportDir = "./reports";
    
    /**
     * 最大查询行数
     */
    private Integer maxRows = 10000;
    
    /**
     * 查询超时时间（秒）
     */
    private Integer queryTimeout = 60;
}

