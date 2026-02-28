package com.springai.mcp.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CodeReviewTool 单元测试
 */
class CodeReviewToolTest {

    private CodeReviewTool codeReviewTool;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        codeReviewTool = new CodeReviewTool();
    }

    @Test
    void testReviewCode_ValidJavaCode() throws IOException {
        // Given
        String javaCode = """
                public class TestClass {
                    private String name;
                    
                    public String getName() {
                        return name;
                    }
                    
                    public void setName(String name) {
                        this.name = name;
                    }
                }
                """;
        
        Path javaFile = tempDir.resolve("TestClass.java");
        Files.writeString(javaFile, javaCode);

        // When
        String result = codeReviewTool.reviewCode(javaFile.toString());

        // Then
        assertNotNull(result);
        assertTrue(result.contains("代码审查完成"));
    }

    @Test
    void testReviewCode_NamingConventions() throws IOException {
        // Given - Bad naming conventions
        String badCode = """
                public class test_class {
                    private String UserName;
                    
                    public void DO_SOMETHING() {
                        int My_Variable = 10;
                    }
                }
                """;
        
        Path javaFile = tempDir.resolve("test_class.java");
        Files.writeString(javaFile, badCode);

        // When
        String result = codeReviewTool.reviewCode(javaFile.toString());

        // Then
        assertNotNull(result);
        assertTrue(result.contains("命名规范") || result.contains("建议"));
    }

    @Test
    void testReviewCode_PerformanceIssues() throws IOException {
        // Given - Performance issues
        String inefficientCode = """
                public class PerformanceTest {
                    public String concatenate(String[] strings) {
                        String result = "";
                        for (String s : strings) {
                            result = result + s;  // Inefficient string concatenation
                        }
                        return result;
                    }
                }
                """;
        
        Path javaFile = tempDir.resolve("PerformanceTest.java");
        Files.writeString(javaFile, inefficientCode);

        // When
        String result = codeReviewTool.reviewCode(javaFile.toString());

        // Then
        assertNotNull(result);
        assertTrue(result.contains("性能") || result.contains("StringBuilder"));
    }

    @Test
    void testReviewCode_SecurityIssues() throws IOException {
        // Given - Security issues
        String insecureCode = """
                import java.sql.*;
                
                public class SecurityTest {
                    public void query(String userId) throws SQLException {
                        Connection conn = null;
                        Statement stmt = conn.createStatement();
                        String sql = "SELECT * FROM users WHERE id = '" + userId + "'";
                        ResultSet rs = stmt.executeQuery(sql);
                    }
                }
                """;
        
        Path javaFile = tempDir.resolve("SecurityTest.java");
        Files.writeString(javaFile, insecureCode);

        // When
        String result = codeReviewTool.reviewCode(javaFile.toString());

        // Then
        assertNotNull(result);
        assertTrue(result.contains("安全") || result.contains("SQL注入"));
    }

    @Test
    void testReviewCode_EmptyFile() throws IOException {
        // Given
        Path emptyFile = tempDir.resolve("Empty.java");
        Files.writeString(emptyFile, "");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            codeReviewTool.reviewCode(emptyFile.toString());
        });
    }

    @Test
    void testReviewCode_NonJavaFile() throws IOException {
        // Given
        Path txtFile = tempDir.resolve("test.txt");
        Files.writeString(txtFile, "This is not Java code");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            codeReviewTool.reviewCode(txtFile.toString());
        });
    }

    @Test
    void testReviewCode_FileNotFound() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            codeReviewTool.reviewCode("/non/existent/file.java");
        });
    }

    @Test
    void testReviewCode_ComplexClass() throws IOException {
        // Given - Complex class with multiple issues
        String complexCode = """
                public class ComplexTest {
                    private String user_name;  // Bad naming
                    
                    public void processData(String input) {
                        // String concatenation in loop
                        String result = "";
                        for (int i = 0; i < 100; i++) {
                            result = result + input;
                        }
                        
                        // SQL injection vulnerability
                        String sql = "SELECT * FROM users WHERE name = '" + input + "'";
                        
                        // Empty catch block
                        try {
                            // Some operation
                        } catch (Exception e) {
                            // Empty catch
                        }
                    }
                }
                """;
        
        Path javaFile = tempDir.resolve("ComplexTest.java");
        Files.writeString(javaFile, complexCode);

        // When
        String result = codeReviewTool.reviewCode(javaFile.toString());

        // Then
        assertNotNull(result);
        assertTrue(result.contains("问题") || result.contains("建议"));
        // Should detect multiple issues
        assertTrue(result.length() > 100);
    }
}

