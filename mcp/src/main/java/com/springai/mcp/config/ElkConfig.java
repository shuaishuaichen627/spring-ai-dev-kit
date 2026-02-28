package com.springai.mcp.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch 配置
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "mcp.tools.elk", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ElkConfig {

    private final ElkProperties elkProperties;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        try {
            // 解析 URL
            String baseUrl = elkProperties.getBaseUrl();
            HttpHost host = HttpHost.create(baseUrl);
            
            // 创建 RestClient
            RestClient.Builder builder = RestClient.builder(host);
            
            // 配置认证
            if (elkProperties.getUsername() != null && !elkProperties.getUsername().isEmpty()) {
                BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(
                        AuthScope.ANY,
                        new UsernamePasswordCredentials(
                                elkProperties.getUsername(),
                                elkProperties.getPassword()
                        )
                );
                
                builder.setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                );
            }
            
            // 配置超时
            builder.setRequestConfigCallback(requestConfigBuilder ->
                    requestConfigBuilder
                            .setConnectTimeout(elkProperties.getConnectTimeout())
                            .setSocketTimeout(elkProperties.getReadTimeout())
            );
            
            RestClient restClient = builder.build();
            
            // 创建 Transport
            RestClientTransport transport = new RestClientTransport(
                    restClient,
                    new JacksonJsonpMapper()
            );
            
            ElasticsearchClient client = new ElasticsearchClient(transport);
            
            log.info("Elasticsearch 客户端初始化成功：{}", baseUrl);
            
            return client;
        } catch (Exception e) {
            log.error("Elasticsearch 客户端初始化失败", e);
            throw new RuntimeException("Elasticsearch 客户端初始化失败", e);
        }
    }
}

