package com.springai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 配置
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring AI Dev Kit API")
                        .version("1.0.0")
                        .description("基于 Spring AI 的智能研发助手，整合 RAG、MCP 和 AI Agent 能力")
                        .contact(new Contact()
                                .name("Spring AI Dev Kit")
                                .url("https://github.com/your-repo/spring-ai-dev-kit")));
    }
}

