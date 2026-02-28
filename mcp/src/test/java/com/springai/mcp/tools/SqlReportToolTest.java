package com.springai.mcp.tools;

import com.springai.mcp.config.SqlReportProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * SqlReportTool 单元测试
 */
@ExtendWith(MockitoExtension.class)
class SqlReportToolTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement statement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSetMetaData metaData;

    @Mock
    private SqlReportProperties properties;

    @TempDir
    Path tempDir;

    private SqlReportTool sqlReportTool;

    @BeforeEach
    void setUp() throws Exception {
        when(properties.getExportDir()).thenReturn(tempDir.toString());
        when(properties.getMaxRows()).thenReturn(1000);
        when(properties.getQueryTimeout()).thenReturn(30);
        
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(metaData);
        
        sqlReportTool = new SqlReportTool(dataSource, properties);
    }

    @Test
    void testExecuteQuery_Success() throws Exception {
        // Given
        String sql = "SELECT id, name FROM users";
        
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnName(1)).thenReturn("id");
        when(metaData.getColumnName(2)).thenReturn("name");
        
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(1, 2);
        when(resultSet.getObject(2)).thenReturn("Alice", "Bob");

        // When
        String result = sqlReportTool.executeQuery(sql, "json");

        // Then
        assertNotNull(result);
        assertTrue(result.contains("Alice"));
        assertTrue(result.contains("Bob"));
        verify(statement).setQueryTimeout(30);
    }

    @Test
    void testExecuteQuery_SqlInjectionPrevention() {
        // Given
        String maliciousSql = "SELECT * FROM users; DROP TABLE users;";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            sqlReportTool.executeQuery(maliciousSql, "json");
        });
    }

    @Test
    void testExecuteQuery_InvalidFormat() {
        // Given
        String sql = "SELECT * FROM users";
        String invalidFormat = "xml";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            sqlReportTool.executeQuery(sql, invalidFormat);
        });
    }

    @Test
    void testExportToCsv_Success() throws Exception {
        // Given
        String sql = "SELECT id, name FROM users";
        
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnName(1)).thenReturn("id");
        when(metaData.getColumnName(2)).thenReturn("name");
        
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getObject(1)).thenReturn(1);
        when(resultSet.getObject(2)).thenReturn("Alice");

        // When
        String result = sqlReportTool.executeQuery(sql, "csv");

        // Then
        assertNotNull(result);
        assertTrue(result.contains(".csv"));
        
        // Verify file exists
        File exportDir = new File(tempDir.toString());
        File[] files = exportDir.listFiles((dir, name) -> name.endsWith(".csv"));
        assertNotNull(files);
        assertTrue(files.length > 0);
    }

    @Test
    void testExecuteQuery_MaxRowsLimit() throws Exception {
        // Given
        String sql = "SELECT * FROM large_table";
        when(properties.getMaxRows()).thenReturn(10);
        
        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnName(1)).thenReturn("id");
        
        // Simulate 15 rows
        when(resultSet.next()).thenReturn(
            true, true, true, true, true,
            true, true, true, true, true,
            true, true, true, true, true,
            false
        );
        when(resultSet.getObject(1)).thenReturn(1);

        // When
        String result = sqlReportTool.executeQuery(sql, "json");

        // Then
        assertNotNull(result);
        assertTrue(result.contains("已达到最大行数限制"));
    }

    @Test
    void testValidateSql_ValidQueries() {
        // Valid queries should not throw exceptions
        assertDoesNotThrow(() -> sqlReportTool.executeQuery("SELECT * FROM users", "json"));
        assertDoesNotThrow(() -> sqlReportTool.executeQuery("SELECT id, name FROM users WHERE id = 1", "json"));
    }

    @Test
    void testValidateSql_InvalidQueries() {
        // Invalid queries should throw exceptions
        assertThrows(IllegalArgumentException.class, () -> 
            sqlReportTool.executeQuery("DROP TABLE users", "json"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            sqlReportTool.executeQuery("DELETE FROM users", "json"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            sqlReportTool.executeQuery("UPDATE users SET name = 'hacked'", "json"));
    }
}

