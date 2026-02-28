# 贡献指南

感谢你对 Spring AI Dev Kit 的关注！我们欢迎任何形式的贡献。

## 如何贡献

### 报告 Bug

如果你发现了 Bug，请通过 GitHub Issues 提交，并包含以下信息：

- Bug 的详细描述
- 复现步骤
- 期望行为
- 实际行为
- 环境信息（操作系统、Java 版本等）

### 提交功能建议

如果你有新功能的想法，欢迎通过 GitHub Issues 提交，并说明：

- 功能的使用场景
- 预期效果
- 可能的实现方案

### 提交代码

1. **Fork 项目**

2. **创建分支**
```bash
git checkout -b feature/your-feature-name
```

3. **编写代码**
   - 遵循项目的代码风格
   - 添加必要的注释
   - 编写单元测试

4. **提交代码**
```bash
git commit -m "feat: 添加新功能"
```

提交信息格式：
- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建/工具相关

5. **推送到 GitHub**
```bash
git push origin feature/your-feature-name
```

6. **创建 Pull Request**

## 代码规范

### Java 代码风格

- 使用 4 空格缩进
- 类名使用大驼峰命名
- 方法名和变量名使用小驼峰命名
- 常量使用全大写下划线分隔
- 每个类都要有 Javadoc 注释

### 示例

```java
/**
 * 用户服务
 */
@Service
public class UserService {
    
    private static final int MAX_RETRY_COUNT = 3;
    
    /**
     * 获取用户信息
     */
    public User getUserById(Long userId) {
        // 实现逻辑
    }
}
```

## 开发流程

1. 在本地开发环境测试
2. 运行单元测试确保通过
3. 更新相关文档
4. 提交 Pull Request
5. 等待 Code Review

## 联系方式

如有任何问题，欢迎通过以下方式联系：

- GitHub Issues
- Email: support@example.com

再次感谢你的贡献！

