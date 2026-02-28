package com.springai.sample.mcp;

import org.springframework.ai.tool.Tool;
import org.springframework.stereotype.Component;

/**
 * MCP 工具极简示例
 * 演示如何创建和使用 MCP 工具
 */
@Component
public class McpSimpleDemo {

    /**
     * 示例工具 1：计算器
     */
    @Tool(description = """
            一个简单的计算器工具。
            可以执行基本的数学运算：加、减、乘、除。
            参数说明：
            - operation: 运算类型（add、subtract、multiply、divide）
            - a: 第一个数字
            - b: 第二个数字
            """)
    public String calculator(String operation, double a, double b) {
        double result = switch (operation.toLowerCase()) {
            case "add" -> a + b;
            case "subtract" -> a - b;
            case "multiply" -> a * b;
            case "divide" -> b != 0 ? a / b : Double.NaN;
            default -> throw new IllegalArgumentException("不支持的运算：" + operation);
        };
        
        String resultStr = String.format("%.2f %s %.2f = %.2f", 
                a, getOperationSymbol(operation), b, result);
        
        System.out.println("计算结果：" + resultStr);
        return resultStr;
    }

    /**
     * 示例工具 2：天气查询（模拟）
     */
    @Tool(description = """
            查询指定城市的天气信息。
            这是一个模拟工具，返回示例数据。
            参数说明：
            - city: 城市名称
            """)
    public String getWeather(String city) {
        // 模拟天气数据
        String weather = String.format("""
                城市：%s
                天气：晴
                温度：25°C
                湿度：60%%
                风力：3级
                """, city);
        
        System.out.println("查询天气：" + city);
        System.out.println(weather);
        
        return weather;
    }

    /**
     * 示例工具 3：文本分析
     */
    @Tool(description = """
            分析文本的基本统计信息。
            返回字符数、单词数、行数等信息。
            参数说明：
            - text: 要分析的文本
            """)
    public String analyzeText(String text) {
        int charCount = text.length();
        int wordCount = text.split("\\s+").length;
        int lineCount = text.split("\n").length;
        
        String analysis = String.format("""
                文本分析结果：
                - 字符数：%d
                - 单词数：%d
                - 行数：%d
                """, charCount, wordCount, lineCount);
        
        System.out.println(analysis);
        return analysis;
    }

    /**
     * 辅助方法：获取运算符号
     */
    private String getOperationSymbol(String operation) {
        return switch (operation.toLowerCase()) {
            case "add" -> "+";
            case "subtract" -> "-";
            case "multiply" -> "×";
            case "divide" -> "÷";
            default -> "?";
        };
    }

    /**
     * 运行示例
     */
    public void runDemo() {
        System.out.println("========== MCP 工具示例演示 ==========\n");
        
        // 示例 1：计算器
        System.out.println("示例 1：计算器工具");
        calculator("add", 10, 5);
        calculator("multiply", 3, 7);
        System.out.println();
        
        // 示例 2：天气查询
        System.out.println("示例 2：天气查询工具");
        getWeather("北京");
        System.out.println();
        
        // 示例 3：文本分析
        System.out.println("示例 3：文本分析工具");
        analyzeText("Hello World\nThis is a test\nSpring AI is awesome");
        
        System.out.println("\n========== 示例演示完成 ==========");
    }
}

