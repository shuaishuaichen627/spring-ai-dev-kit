package com.springai.rag.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * 文档解析服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentParserService {

    /**
     * 解析 PDF 文档
     */
    public List<Document> parsePdf(String filePath) {
        try {
            PagePdfDocumentReader reader = new PagePdfDocumentReader(
                    new FileSystemResource(new File(filePath)));
            List<Document> documents = reader.get();
            log.info("PDF 文档解析成功，共 {} 页", documents.size());
            return documents;
        } catch (Exception e) {
            log.error("PDF 文档解析失败", e);
            throw new RuntimeException("PDF 文档解析失败：" + e.getMessage());
        }
    }

    /**
     * 解析 Markdown 文档
     */
    public List<Document> parseMarkdown(String filePath) {
        try {
            TikaDocumentReader reader = new TikaDocumentReader(
                    new FileSystemResource(new File(filePath)));
            List<Document> documents = reader.get();
            log.info("Markdown 文档解析成功");
            return documents;
        } catch (Exception e) {
            log.error("Markdown 文档解析失败", e);
            throw new RuntimeException("Markdown 文档解析失败：" + e.getMessage());
        }
    }
}

