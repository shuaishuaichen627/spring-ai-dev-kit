# 编译错误修复 - VectorStoreService 方法名不匹配

## 问题
```
cannot find symbol: method similaritySearch(java.lang.String,int)
location: variable vectorStoreService of type com.springai.rag.service.VectorStoreService
```

## 原因
- `RagController` 调用 `vectorStoreService.similaritySearch(query, topK)`
- 但 `VectorStoreService` 中方法名是 `searchSimilar`

## 解决方案
在 `VectorStoreService` 中添加 `similaritySearch` 方法，同时保留 `searchSimilar` 作为别名（兼容测试代码）

## 修改内容
```java
// 主方法
public List<Document> similaritySearch(String query, int topK)
public List<Document> similaritySearch(String query, int topK, double threshold)

// 别名方法（兼容测试）
public List<Document> searchSimilar(String query, int topK)
public List<Document> searchSimilar(String query, int topK, double threshold)
```

## 提交
```bash
git add rag/src/main/java/com/springai/rag/service/VectorStoreService.java
git commit -m "fix: add similaritySearch method to VectorStoreService for RagController compatibility"
git push
```

✅ 修复完成！

