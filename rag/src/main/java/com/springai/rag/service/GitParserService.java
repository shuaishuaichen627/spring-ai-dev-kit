package com.springai.rag.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Git 代码解析服务
 */
@Slf4j
@Service
public class GitParserService {

    /**
     * 克隆 Git 仓库
     */
    public String cloneRepository(String gitUrl, String localPath) {
        try {
            File localDir = new File(localPath);
            if (localDir.exists()) {
                log.info("目录已存在，跳过克隆：{}", localPath);
                return localPath;
            }

            Git.cloneRepository()
                    .setURI(gitUrl)
                    .setDirectory(localDir)
                    .call();
            
            log.info("Git 仓库克隆成功：{}", gitUrl);
            return localPath;
        } catch (GitAPIException e) {
            log.error("Git 仓库克隆失败", e);
            throw new RuntimeException("Git 仓库克隆失败：" + e.getMessage());
        }
    }

    /**
     * 解析代码文件
     */
    public List<String> parseCodeFiles(String repoPath) {
        List<String> codeContents = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Path.of(repoPath))) {
            paths.filter(Files::isRegularFile)
                    .filter(this::isCodeFile)
                    .forEach(path -> {
                        try {
                            String content = Files.readString(path);
                            codeContents.add(content);
                            log.debug("解析代码文件：{}", path);
                        } catch (IOException e) {
                            log.error("读取文件失败：{}", path, e);
                        }
                    });
        } catch (IOException e) {
            log.error("遍历目录失败", e);
        }
        return codeContents;
    }

    /**
     * 判断是否为代码文件
     */
    private boolean isCodeFile(Path path) {
        String fileName = path.getFileName().toString();
        return fileName.endsWith(".java") 
                || fileName.endsWith(".py")
                || fileName.endsWith(".js")
                || fileName.endsWith(".ts")
                || fileName.endsWith(".go")
                || fileName.endsWith(".cpp")
                || fileName.endsWith(".c");
    }
}

