package com.springai.mcp.tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.Tool;
import org.springframework.stereotype.Component;

/**
 * 代码评审工具
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CodeReviewTool {

    @Tool(description = """
            对代码进行静态分析和评审。
            用于代码质量检查、规范检查和性能优化建议。
            支持多种编程语言和不同的评审重点。
            """)
    public String reviewCode(
            String code,
            String language,
            String focus) {
        
        log.info("执行代码评审：language={}, focus={}", language, focus);
        
        // TODO: 实现代码评审逻辑
        // 这里可以集成 SonarQube、Checkstyle 等工具
        
        String reviewFocus = focus != null ? focus : "综合评审";
        
        return String.format("""
                ===== 代码评审结果 =====
                编程语言：%s
                评审重点：%s
                代码质量评分：85/100
                
                发现的问题：
                1. 第 15 行：变量命名不符合驼峰命名规范
                2. 第 23 行：存在潜在的空指针异常
                3. 第 45 行：建议使用 StringBuilder 代替字符串拼接
                
                优化建议：
                1. 建议添加输入参数校验
                2. 可以使用 Stream API 简化集合操作
                3. 建议添加单元测试覆盖核心逻辑
                
                总结：代码整体质量良好，发现 3 个需要改进的地方
                """, language, reviewFocus);
    }
}
