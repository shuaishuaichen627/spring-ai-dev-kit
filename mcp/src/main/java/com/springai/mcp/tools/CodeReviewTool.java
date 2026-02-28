package com.springai.mcp.tools;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 代码评审工具
 * 支持真实的代码静态分析
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CodeReviewTool {

    private final JavaParser javaParser = new JavaParser();

    @Tool(description = """
            对代码进行静态分析和评审。
            用于代码质量检查、规范检查和性能优化建议。
            目前支持 Java 语言的深度分析，其他语言提供基础检查。
            参数说明：
            - code: 要评审的代码
            - language: 编程语言（java、python、javascript 等）
            - focus: 评审重点（style-代码风格、performance-性能、security-安全、all-全部）
            """)
    public String reviewCode(
            String code,
            String language,
            String focus) {
        
        log.info("执行代码评审：language={}, focus={}, codeLength={}", 
                language, focus, code != null ? code.length() : 0);
        
        try {
            // 参数校验
            if (code == null || code.trim().isEmpty()) {
                return "错误：代码内容不能为空";
            }
            
            String lang = language != null ? language.toLowerCase() : "java";
            String reviewFocus = focus != null ? focus.toLowerCase() : "all";
            
            // 根据语言选择分析方法
            ReviewResult result;
            if ("java".equals(lang)) {
                result = reviewJavaCode(code, reviewFocus);
            } else {
                result = reviewGenericCode(code, lang, reviewFocus);
            }
            
            // 格式化输出
            return formatReviewResult(result, lang, reviewFocus);
            
        } catch (Exception e) {
            log.error("代码评审失败", e);
            return String.format("""
                    ===== 代码评审失败 =====
                    错误信息：%s
                    
                    可能的原因：
                    1. 代码语法错误
                    2. 代码格式不正确
                    3. 不支持的语言特性
                    
                    建议：请检查代码是否可以正常编译
                    """, e.getMessage());
        }
    }
    
    /**
     * 评审 Java 代码（深度分析）
     */
    private ReviewResult reviewJavaCode(String code, String focus) {
        ReviewResult result = new ReviewResult();
        
        try {
            // 解析代码
            ParseResult<CompilationUnit> parseResult = javaParser.parse(code);
            
            if (!parseResult.isSuccessful()) {
                result.issues.add("代码解析失败，可能存在语法错误");
                result.score = 0;
                return result;
            }
            
            CompilationUnit cu = parseResult.getResult().orElse(null);
            if (cu == null) {
                result.issues.add("无法解析代码结构");
                result.score = 0;
                return result;
            }
            
            // 代码风格检查
            if ("style".equals(focus) || "all".equals(focus)) {
                checkJavaStyle(cu, result);
            }
            
            // 性能检查
            if ("performance".equals(focus) || "all".equals(focus)) {
                checkJavaPerformance(cu, result);
            }
            
            // 安全检查
            if ("security".equals(focus) || "all".equals(focus)) {
                checkJavaSecurity(cu, result);
            }
            
            // 通用检查
            checkJavaGeneral(cu, result);
            
            // 计算评分
            result.score = calculateScore(result);
            
        } catch (Exception e) {
            log.error("Java 代码分析失败", e);
            result.issues.add("代码分析过程中出现异常：" + e.getMessage());
            result.score = 0;
        }
        
        return result;
    }
    
    /**
     * Java 代码风格检查
     */
    private void checkJavaStyle(CompilationUnit cu, ReviewResult result) {
        // 检查类名命名规范
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cls -> {
            String className = cls.getNameAsString();
            if (!Character.isUpperCase(className.charAt(0))) {
                result.issues.add(String.format("类名 '%s' 应该以大写字母开头", className));
            }
            if (className.contains("_")) {
                result.issues.add(String.format("类名 '%s' 不应包含下划线，建议使用驼峰命名", className));
            }
        });
        
        // 检查方法命名规范
        cu.findAll(MethodDeclaration.class).forEach(method -> {
            String methodName = method.getNameAsString();
            if (Character.isUpperCase(methodName.charAt(0))) {
                result.issues.add(String.format("方法名 '%s' 应该以小写字母开头", methodName));
            }
        });
        
        // 检查变量命名规范
        cu.findAll(VariableDeclarator.class).forEach(var -> {
            String varName = var.getNameAsString();
            if (varName.length() == 1 && !varName.equals("i") && !varName.equals("j")) {
                result.suggestions.add(String.format("变量名 '%s' 过短，建议使用更有意义的名称", varName));
            }
            if (varName.contains("_") && !varName.equals(varName.toUpperCase())) {
                result.issues.add(String.format("变量名 '%s' 应使用驼峰命名，而不是下划线", varName));
            }
        });
    }
    
    /**
     * Java 性能检查
     */
    private void checkJavaPerformance(CompilationUnit cu, ReviewResult result) {
        // 检查字符串拼接
        cu.findAll(MethodDeclaration.class).forEach(method -> {
            method.findAll(MethodCallExpr.class).forEach(call -> {
                // 检查循环中的字符串拼接
                if (isInLoop(call) && isStringConcatenation(call)) {
                    result.issues.add(String.format(
                            "方法 '%s' 中在循环内使用字符串拼接，建议使用 StringBuilder",
                            method.getNameAsString()));
                }
            });
        });
        
        // 检查方法复杂度
        cu.findAll(MethodDeclaration.class).forEach(method -> {
            int complexity = calculateCyclomaticComplexity(method);
            if (complexity > 10) {
                result.issues.add(String.format(
                        "方法 '%s' 的圈复杂度为 %d，建议拆分为多个小方法（建议 < 10）",
                        method.getNameAsString(), complexity));
            } else if (complexity > 7) {
                result.suggestions.add(String.format(
                        "方法 '%s' 的圈复杂度为 %d，建议考虑简化逻辑",
                        method.getNameAsString(), complexity));
            }
        });
    }
    
    /**
     * Java 安全检查
     */
    private void checkJavaSecurity(CompilationUnit cu, ReviewResult result) {
        // 检查空指针风险
        cu.findAll(MethodDeclaration.class).forEach(method -> {
            method.findAll(MethodCallExpr.class).forEach(call -> {
                if (call.getNameAsString().equals("get") && 
                    !hasNullCheck(call, method)) {
                    result.suggestions.add(String.format(
                            "方法 '%s' 中调用 get() 方法，建议添加空值检查",
                            method.getNameAsString()));
                }
            });
        });
        
        // 检查异常处理
        cu.findAll(MethodDeclaration.class).forEach(method -> {
            if (method.getThrownExceptions().isEmpty() && 
                !method.getBody().isPresent()) {
                // 方法体为空，跳过
                return;
            }
            
            if (method.getBody().isPresent()) {
                BlockStmt body = method.getBody().get();
                if (body.getStatements().size() > 5 && 
                    !body.toString().contains("try")) {
                    result.suggestions.add(String.format(
                            "方法 '%s' 较复杂但没有异常处理，建议添加 try-catch",
                            method.getNameAsString()));
                }
            }
        });
    }
    
    /**
     * Java 通用检查
     */
    private void checkJavaGeneral(CompilationUnit cu, ReviewResult result) {
        // 检查方法长度
        cu.findAll(MethodDeclaration.class).forEach(method -> {
            if (method.getBody().isPresent()) {
                int lines = method.getBody().get().toString().split("\n").length;
                if (lines > 50) {
                    result.issues.add(String.format(
                            "方法 '%s' 有 %d 行代码，建议拆分（建议 < 50 行）",
                            method.getNameAsString(), lines));
                } else if (lines > 30) {
                    result.suggestions.add(String.format(
                            "方法 '%s' 有 %d 行代码，建议考虑拆分",
                            method.getNameAsString(), lines));
                }
            }
        });
        
        // 检查是否有注释
        if (!cu.getAllComments().isEmpty()) {
            result.suggestions.add("代码包含注释，这是好的实践");
        } else {
            result.suggestions.add("建议为复杂逻辑添加注释");
        }
    }
    
    /**
     * 评审通用代码（基础检查）
     */
    private ReviewResult reviewGenericCode(String code, String language, String focus) {
        ReviewResult result = new ReviewResult();
        
        String[] lines = code.split("\n");
        
        // 基础检查
        if (lines.length > 100) {
            result.issues.add(String.format("代码文件有 %d 行，建议拆分为多个文件", lines.length));
        }
        
        // 检查命名规范（通用）
        Pattern varPattern = Pattern.compile("\\b[a-z][a-zA-Z0-9]*\\b");
        Pattern constPattern = Pattern.compile("\\b[A-Z][A-Z0-9_]*\\b");
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            
            // 检查行长度
            if (line.length() > 120) {
                result.suggestions.add(String.format("第 %d 行过长（%d 字符），建议换行", 
                        i + 1, line.length()));
            }
            
            // 检查 TODO 和 FIXME
            if (line.contains("TODO") || line.contains("FIXME")) {
                result.suggestions.add(String.format("第 %d 行包含 TODO/FIXME 标记，建议处理", i + 1));
            }
        }
        
        // 语言特定检查
        if ("python".equals(language)) {
            checkPythonSpecific(code, result);
        } else if ("javascript".equals(language) || "typescript".equals(language)) {
            checkJavaScriptSpecific(code, result);
        }
        
        result.score = calculateScore(result);
        
        return result;
    }
    
    /**
     * Python 特定检查
     */
    private void checkPythonSpecific(String code, ReviewResult result) {
        if (!code.contains("def ")) {
            result.suggestions.add("建议将代码组织为函数");
        }
        if (code.contains("except:") && !code.contains("except Exception")) {
            result.issues.add("发现裸 except 语句，建议指定具体的异常类型");
        }
    }
    
    /**
     * JavaScript 特定检查
     */
    private void checkJavaScriptSpecific(String code, ReviewResult result) {
        if (code.contains("var ")) {
            result.issues.add("建议使用 let 或 const 代替 var");
        }
        if (code.contains("==") && !code.contains("===")) {
            result.suggestions.add("建议使用 === 代替 ==");
        }
    }
    
    /**
     * 辅助方法：检查是否在循环中
     */
    private boolean isInLoop(MethodCallExpr call) {
        return call.findAncestor(com.github.javaparser.ast.stmt.ForStmt.class).isPresent() ||
               call.findAncestor(com.github.javaparser.ast.stmt.WhileStmt.class).isPresent() ||
               call.findAncestor(com.github.javaparser.ast.stmt.ForEachStmt.class).isPresent();
    }
    
    /**
     * 辅助方法：检查是否是字符串拼接
     */
    private boolean isStringConcatenation(MethodCallExpr call) {
        return call.toString().contains("+") && 
               (call.toString().contains("\"") || call.toString().contains("String"));
    }
    
    /**
     * 辅助方法：检查是否有空值检查
     */
    private boolean hasNullCheck(MethodCallExpr call, MethodDeclaration method) {
        String methodBody = method.toString();
        return methodBody.contains("!= null") || 
               methodBody.contains("Objects.requireNonNull") ||
               methodBody.contains("Optional");
    }
    
    /**
     * 计算圈复杂度
     */
    private int calculateCyclomaticComplexity(MethodDeclaration method) {
        int complexity = 1; // 基础复杂度
        
        String methodStr = method.toString();
        complexity += countOccurrences(methodStr, "if ");
        complexity += countOccurrences(methodStr, "else if");
        complexity += countOccurrences(methodStr, "for ");
        complexity += countOccurrences(methodStr, "while ");
        complexity += countOccurrences(methodStr, "case ");
        complexity += countOccurrences(methodStr, "catch ");
        complexity += countOccurrences(methodStr, "&&");
        complexity += countOccurrences(methodStr, "||");
        
        return complexity;
    }
    
    /**
     * 计算字符串中子串出现次数
     */
    private int countOccurrences(String str, String substr) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(substr, index)) != -1) {
            count++;
            index += substr.length();
        }
        return count;
    }
    
    /**
     * 计算评分
     */
    private int calculateScore(ReviewResult result) {
        int score = 100;
        score -= result.issues.size() * 10;  // 每个问题扣 10 分
        score -= result.suggestions.size() * 3;  // 每个建议扣 3 分
        return Math.max(0, Math.min(100, score));
    }
    
    /**
     * 格式化评审结果
     */
    private String formatReviewResult(ReviewResult result, String language, String focus) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== 代码评审结果 =====\n");
        sb.append(String.format("编程语言：%s\n", language));
        sb.append(String.format("评审重点：%s\n", focus));
        sb.append(String.format("代码质量评分：%d/100\n\n", result.score));
        
        if (result.issues.isEmpty() && result.suggestions.isEmpty()) {
            sb.append("✅ 代码质量优秀，未发现明显问题！\n");
        } else {
            if (!result.issues.isEmpty()) {
                sb.append(String.format("发现的问题（%d 个）：\n", result.issues.size()));
                for (int i = 0; i < result.issues.size(); i++) {
                    sb.append(String.format("%d. %s\n", i + 1, result.issues.get(i)));
                }
                sb.append("\n");
            }
            
            if (!result.suggestions.isEmpty()) {
                sb.append(String.format("优化建议（%d 个）：\n", result.suggestions.size()));
                for (int i = 0; i < result.suggestions.size(); i++) {
                    sb.append(String.format("%d. %s\n", i + 1, result.suggestions.get(i)));
                }
                sb.append("\n");
            }
        }
        
        // 总结
        if (result.score >= 90) {
            sb.append("总结：代码质量优秀，继续保持！\n");
        } else if (result.score >= 70) {
            sb.append("总结：代码质量良好，建议处理上述问题以进一步提升\n");
        } else if (result.score >= 50) {
            sb.append("总结：代码质量一般，建议重点关注上述问题\n");
        } else {
            sb.append("总结：代码质量需要改进，建议重构\n");
        }
        
        return sb.toString();
    }
    
    /**
     * 评审结果
     */
    private static class ReviewResult {
        List<String> issues = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        int score = 100;
    }
}
