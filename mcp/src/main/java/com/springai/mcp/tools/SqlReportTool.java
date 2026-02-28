package com.springai.mcp.tools;

import com.opencsv.CSVWriter;
import com.springai.mcp.config.SqlReportProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.ai.tool.Tool;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL 报表导出工具
 * 支持真实的 SQL 查询和报表导出（CSV、Excel）
 */
@Slf4j
@Component
public class SqlReportTool {

    private final DataSource reportDataSource;
    private final DataSource mainDataSource;
    private final SqlReportProperties sqlReportProperties;
    private final File reportExportDir;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public SqlReportTool(
            @Qualifier("reportDataSource") DataSource reportDataSource,
            DataSource mainDataSource,
            SqlReportProperties sqlReportProperties,
            File reportExportDir) {
        this.reportDataSource = reportDataSource;
        this.mainDataSource = mainDataSource;
        this.sqlReportProperties = sqlReportProperties;
        this.reportExportDir = reportExportDir;
    }

    @Tool(description = """
            执行 SQL 查询并导出报表文件。
            用于数据分析、报表生成和数据导出。
            支持 CSV 和 Excel 两种导出格式。
            参数说明：
            - sql: SQL 查询语句（SELECT 语句）
            - format: 导出格式，csv 或 excel，默认 csv
            - reportName: 报表名称，用于生成文件名
            """)
    public String exportSqlReport(
            String sql,
            String format,
            String reportName) {
        
        log.info("执行 SQL 报表导出：sql={}, format={}, reportName={}", 
                sql, format, reportName);
        
        try {
            // 参数校验
            if (sql == null || sql.trim().isEmpty()) {
                return "错误：SQL 语句不能为空";
            }
            
            // 安全检查：只允许 SELECT 语句
            String trimmedSql = sql.trim().toUpperCase();
            if (!trimmedSql.startsWith("SELECT")) {
                return "错误：出于安全考虑，只允许执行 SELECT 查询语句";
            }
            
            // 选择数据源
            DataSource dataSource = reportDataSource != null ? reportDataSource : mainDataSource;
            if (dataSource == null) {
                return "错误：未配置数据源，请检查配置";
            }
            
            // 执行查询
            QueryResult queryResult = executeQuery(dataSource, sql);
            
            // 导出报表
            String exportFormat = format != null && !format.isEmpty() ? format.toLowerCase() : "csv";
            String fileName = generateFileName(reportName, exportFormat);
            File reportFile = new File(reportExportDir, fileName);
            
            int exportedRows;
            if ("excel".equals(exportFormat) || "xlsx".equals(exportFormat)) {
                exportedRows = exportToExcel(queryResult, reportFile);
            } else {
                exportedRows = exportToCsv(queryResult, reportFile);
            }
            
            // 返回结果
            return String.format("""
                    ===== SQL 报表导出成功 =====
                    SQL 语句：%s
                    导出格式：%s
                    报表名称：%s
                    
                    文件名：%s
                    文件路径：%s
                    数据行数：%d 行
                    列数：%d 列
                    文件大小：%.2f KB
                    
                    列名：%s
                    
                    报表导出成功！
                    """, 
                    sql.length() > 100 ? sql.substring(0, 100) + "..." : sql,
                    exportFormat,
                    reportName,
                    fileName,
                    reportFile.getAbsolutePath(),
                    exportedRows,
                    queryResult.columnNames.size(),
                    reportFile.length() / 1024.0,
                    String.join(", ", queryResult.columnNames));
            
        } catch (SQLException e) {
            log.error("SQL 执行失败", e);
            return String.format("""
                    ===== SQL 报表导出失败 =====
                    错误类型：SQL 执行错误
                    错误信息：%s
                    SQL 状态：%s
                    错误代码：%d
                    
                    请检查：
                    1. SQL 语法是否正确
                    2. 表名和字段名是否存在
                    3. 是否有查询权限
                    """, e.getMessage(), e.getSQLState(), e.getErrorCode());
        } catch (Exception e) {
            log.error("报表导出失败", e);
            return String.format("""
                    ===== SQL 报表导出失败 =====
                    错误信息：%s
                    
                    请检查：
                    1. 数据源配置是否正确
                    2. 导出目录是否有写入权限：%s
                    3. 磁盘空间是否充足
                    """, e.getMessage(), reportExportDir.getAbsolutePath());
        }
    }
    
    /**
     * 执行 SQL 查询
     */
    private QueryResult executeQuery(DataSource dataSource, String sql) throws SQLException {
        QueryResult result = new QueryResult();
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 设置查询超时
            stmt.setQueryTimeout(sqlReportProperties.getQueryTimeout());
            
            // 设置最大行数
            stmt.setMaxRows(sqlReportProperties.getMaxRows());
            
            // 执行查询
            try (ResultSet rs = stmt.executeQuery(sql)) {
                // 获取列信息
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                for (int i = 1; i <= columnCount; i++) {
                    result.columnNames.add(metaData.getColumnLabel(i));
                }
                
                // 获取数据
                while (rs.next()) {
                    List<String> row = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        Object value = rs.getObject(i);
                        row.add(value != null ? value.toString() : "");
                    }
                    result.rows.add(row);
                }
            }
        }
        
        log.info("SQL 查询完成，返回 {} 行数据", result.rows.size());
        return result;
    }
    
    /**
     * 导出为 CSV
     */
    private int exportToCsv(QueryResult queryResult, File file) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            // 写入列名
            writer.writeNext(queryResult.columnNames.toArray(new String[0]));
            
            // 写入数据
            for (List<String> row : queryResult.rows) {
                writer.writeNext(row.toArray(new String[0]));
            }
        }
        
        log.info("CSV 文件导出成功：{}", file.getAbsolutePath());
        return queryResult.rows.size();
    }
    
    /**
     * 导出为 Excel
     */
    private int exportToExcel(QueryResult queryResult, File file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Report");
            
            // 创建标题样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // 写入列名
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < queryResult.columnNames.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(queryResult.columnNames.get(i));
                cell.setCellStyle(headerStyle);
            }
            
            // 写入数据
            for (int i = 0; i < queryResult.rows.size(); i++) {
                Row row = sheet.createRow(i + 1);
                List<String> rowData = queryResult.rows.get(i);
                for (int j = 0; j < rowData.size(); j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(rowData.get(j));
                }
            }
            
            // 自动调整列宽
            for (int i = 0; i < queryResult.columnNames.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 写入文件
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
        
        log.info("Excel 文件导出成功：{}", file.getAbsolutePath());
        return queryResult.rows.size();
    }
    
    /**
     * 生成文件名
     */
    private String generateFileName(String reportName, String format) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String safeName = reportName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        return String.format("%s_%s.%s", safeName, timestamp, format);
    }
    
    /**
     * 查询结果
     */
    private static class QueryResult {
        List<String> columnNames = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();
    }
}
