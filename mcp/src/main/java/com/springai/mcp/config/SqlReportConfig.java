package com.springai.mcp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.File;

/**
 * SQL 报表配置
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "mcp.tools.sql-report", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SqlReportConfig {

    private final SqlReportProperties sqlReportProperties;

    @Bean(name = "reportDataSource")
    public DataSource reportDataSource() {
        try {
            // 如果没有配置报表数据源，返回 null（使用主数据源）
            if (sqlReportProperties.getDatasourceUrl() == null || 
                sqlReportProperties.getDatasourceUrl().isEmpty()) {
                log.info("未配置报表数据源，将使用主数据源");
                return null;
            }
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(sqlReportProperties.getDatasourceUrl());
            config.setUsername(sqlReportProperties.getDatasourceUsername());
            config.setPassword(sqlReportProperties.getDatasourcePassword());
            config.setMaximumPoolSize(5);
            config.setMinimumIdle(1);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            
            HikariDataSource dataSource = new HikariDataSource(config);
            
            log.info("报表数据源初始化成功：{}", sqlReportProperties.getDatasourceUrl());
            
            return dataSource;
        } catch (Exception e) {
            log.error("报表数据源初始化失败", e);
            return null;
        }
    }
    
    /**
     * 初始化报表导出目录
     */
    @Bean
    public File reportExportDir() {
        File dir = new File(sqlReportProperties.getExportDir());
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                log.info("创建报表导出目录：{}", dir.getAbsolutePath());
            }
        }
        return dir;
    }
}

