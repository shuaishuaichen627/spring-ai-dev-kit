package com.springai.agent.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AgentService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class AgentServiceTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequest.CallPromptResponseSpec callSpec;

    @Mock
    private ChatClient.ChatClientRequest.StreamPromptResponseSpec streamSpec;

    private AgentService agentService;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.build()).thenReturn(chatClient);
        agentService = new AgentService(chatClientBuilder);
    }

    @Test
    void testChat_Success() {
        // Given
        String userMessage = "Hello";
        String expectedResponse = "Hi there!";
        
        when(chatClient.prompt()).thenReturn(mock(ChatClient.ChatClientRequest.class));
        when(chatClient.prompt().user(userMessage)).thenReturn(mock(ChatClient.ChatClientRequest.class));
        when(chatClient.prompt().user(userMessage).call()).thenReturn(callSpec);
        when(callSpec.content()).thenReturn(expectedResponse);

        // When
        String response = agentService.chat(userMessage);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(chatClient, times(1)).prompt();
    }

    @Test
    void testChat_EmptyMessage() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            agentService.chat("");
        });
    }

    @Test
    void testChat_NullMessage() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            agentService.chat(null);
        });
    }

    @Test
    void testStreamChat_Success() {
        // Given
        String userMessage = "Tell me a story";
        ChatResponse response1 = createMockChatResponse("Once ");
        ChatResponse response2 = createMockChatResponse("upon ");
        ChatResponse response3 = createMockChatResponse("a time");
        
        Flux<ChatResponse> responseFlux = Flux.just(response1, response2, response3);
        
        when(chatClient.prompt()).thenReturn(mock(ChatClient.ChatClientRequest.class));
        when(chatClient.prompt().user(userMessage)).thenReturn(mock(ChatClient.ChatClientRequest.class));
        when(chatClient.prompt().user(userMessage).stream()).thenReturn(streamSpec);
        when(streamSpec.chatResponse()).thenReturn(responseFlux);

        // When
        Flux<String> result = agentService.streamChat(userMessage);

        // Then
        StepVerifier.create(result)
                .expectNext("Once ")
                .expectNext("upon ")
                .expectNext("a time")
                .verifyComplete();
    }

    @Test
    void testStreamChat_EmptyMessage() {
        // When
        Flux<String> result = agentService.streamChat("");

        // Then
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void testChatWithTools_Success() {
        // Given
        String userMessage = "What's the weather?";
        String expectedResponse = "The weather is sunny, 25°C";
        
        when(chatClient.prompt()).thenReturn(mock(ChatClient.ChatClientRequest.class));
        when(chatClient.prompt().user(userMessage)).thenReturn(mock(ChatClient.ChatClientRequest.class));
        when(chatClient.prompt().user(userMessage).options(any(ChatOptions.class))).thenReturn(mock(ChatClient.ChatClientRequest.class));
        when(chatClient.prompt().user(userMessage).options(any(ChatOptions.class)).call()).thenReturn(callSpec);
        when(callSpec.content()).thenReturn(expectedResponse);

        // When
        String response = agentService.chatWithTools(userMessage);

        // Then
        assertNotNull(response);
        assertTrue(response.contains("sunny") || response.contains("25"));
    }

    private ChatResponse createMockChatResponse(String content) {
        ChatResponse response = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        when(response.getResult()).thenReturn(generation);
        when(generation.getOutput()).thenReturn(mock(org.springframework.ai.chat.messages.AssistantMessage.class));
        when(generation.getOutput().getContent()).thenReturn(content);
        return response;
    }
}

