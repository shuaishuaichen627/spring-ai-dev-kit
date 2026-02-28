package com.springai.rag.controller;

import com.springai.common.result.Result;
import com.springai.rag.service.DocumentParserService;
import com.springai.rag.service.GitParserService;
import com.springai.rag.service.VectorStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RAG 知识库控制器
 */
@Tag(name = "RAG 知识库", description = "文档解析、向量检索接口")
@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RagController {

    private final GitParserService gitParserService;
    private final DocumentParserService documentParserService;
    private final VectorStoreService vectorStoreService;

    @Operation(summary = "克隆 Git 仓库")
    @PostMapping("/git/clone")
    public Result<String> cloneRepository(
            @RequestParam String gitUrl,
            @RequestParam String localPath) {
        String path = gitParserService.cloneRepository(gitUrl, localPath);
        return Result.success(path);
    }

    @Operation(summary = "解析代码文件")
    @PostMapping("/git/parse")
    public Result<Integer> parseCodeFiles(@RequestParam String repoPath) {
        List<String> contents = gitParserService.parseCodeFiles(repoPath);
        return Result.success(contents.size());
    }

    @Operation(summary = "解析 PDF 文档")
    @PostMapping("/document/pdf")
    public Result<Integer> parsePdf(@RequestParam String filePath) {
        List<Document> documents = documentParserService.parsePdf(filePath);
        vectorStoreService.addDocuments(documents);
        return Result.success(documents.size());
    }

    @Operation(summary = "相似度搜索")
    @GetMapping("/search")
    public Result<List<Document>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int topK) {
        List<Document> results = vectorStoreService.similaritySearch(query, topK);
        return Result.success(results);
    }
}

