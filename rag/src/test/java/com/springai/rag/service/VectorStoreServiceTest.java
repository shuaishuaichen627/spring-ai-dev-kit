package com.springai.rag.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * VectorStoreService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class VectorStoreServiceTest {

    @Mock
    private VectorStore vectorStore;

    private VectorStoreService vectorStoreService;

    @BeforeEach
    void setUp() {
        vectorStoreService = new VectorStoreService(vectorStore);
    }

    @Test
    void testAddDocuments_Success() {
        // Given
        List<Document> documents = Arrays.asList(
                new Document("Document 1 content"),
                new Document("Document 2 content")
        );

        // When
        vectorStoreService.addDocuments(documents);

        // Then
        verify(vectorStore, times(1)).add(documents);
    }

    @Test
    void testAddDocuments_EmptyList() {
        // Given
        List<Document> emptyList = Arrays.asList();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            vectorStoreService.addDocuments(emptyList);
        });
    }

    @Test
    void testAddDocuments_NullList() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            vectorStoreService.addDocuments(null);
        });
    }

    @Test
    void testSearchSimilar_Success() {
        // Given
        String query = "test query";
        int topK = 5;
        
        List<Document> expectedResults = Arrays.asList(
                new Document("Result 1"),
                new Document("Result 2")
        );
        
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(expectedResults);

        // When
        List<Document> results = vectorStoreService.searchSimilar(query, topK);

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(vectorStore, times(1)).similaritySearch(any(SearchRequest.class));
    }

    @Test
    void testSearchSimilar_EmptyQuery() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            vectorStoreService.searchSimilar("", 5);
        });
    }

    @Test
    void testSearchSimilar_InvalidTopK() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            vectorStoreService.searchSimilar("query", 0);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            vectorStoreService.searchSimilar("query", -1);
        });
    }

    @Test
    void testSearchSimilar_WithThreshold() {
        // Given
        String query = "test query";
        int topK = 3;
        double threshold = 0.8;
        
        List<Document> expectedResults = Arrays.asList(
                new Document("Highly relevant result")
        );
        
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(expectedResults);

        // When
        List<Document> results = vectorStoreService.searchSimilar(query, topK, threshold);

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(vectorStore, times(1)).similaritySearch(any(SearchRequest.class));
    }

    @Test
    void testDeleteDocuments_Success() {
        // Given
        List<String> documentIds = Arrays.asList("doc1", "doc2", "doc3");

        // When
        vectorStoreService.deleteDocuments(documentIds);

        // Then
        verify(vectorStore, times(1)).delete(documentIds);
    }

    @Test
    void testDeleteDocuments_EmptyList() {
        // Given
        List<String> emptyList = Arrays.asList();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            vectorStoreService.deleteDocuments(emptyList);
        });
    }
}

