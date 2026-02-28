package com.springai.boot.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Agent 集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AgentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testChatEndpoint_Success() throws Exception {
        mockMvc.perform(get("/api/agent/chat")
                        .param("message", "Hello")
                        .accept(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    void testChatEndpoint_EmptyMessage() throws Exception {
        mockMvc.perform(get("/api/agent/chat")
                        .param("message", "")
                        .accept(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testChatEndpoint_MissingMessage() throws Exception {
        mockMvc.perform(get("/api/agent/chat")
                        .accept(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}

