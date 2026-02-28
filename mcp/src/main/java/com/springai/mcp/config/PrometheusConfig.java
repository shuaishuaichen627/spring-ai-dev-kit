package com.springai.mcp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Base64;

/**
 * Prometheus 配置
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "mcp.tools.prometheus", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PrometheusConfig {

    private final PrometheusProperties prometheusProperties;

    @Bean
    public WebClient prometheusWebClient() {
        // 配置 HttpClient
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(prometheusProperties.getReadTimeout()));
        
        // 创建 WebClient Builder
        WebClient.Builder builder = WebClient.builder()
                .baseUrl(prometheusProperties.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                        .build());
        
        // 配置认证
        if (prometheusProperties.getUsername() != null && !prometheusProperties.getUsername().isEmpty()) {
            String auth = prometheusProperties.getUsername() + ":" + prometheusProperties.getPassword();
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            builder.defaultHeader("Authorization", "Basic " + encodedAuth);
        }
        
        WebClient webClient = builder.build();
        
        log.info("Prometheus WebClient 初始化成功：{}", prometheusProperties.getBaseUrl());
        
        return webClient;
    }
}

