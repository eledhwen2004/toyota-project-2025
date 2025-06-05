package com.main.configuration;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContexts;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;

@Configuration
public class OpenSearchConfig {

    @Bean
    public RestHighLevelClient openSearchClient() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("admin", "Aloha.32bit"));

        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "https"))
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    try {
                        SSLContext sslContext = SSLContexts.custom()
                                .loadTrustMaterial(null, (chain, authType) -> true)
                                .build();
                        return httpClientBuilder
                                .setDefaultCredentialsProvider(credentialsProvider)
                                .setSSLContext(sslContext)
                                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                    } catch (Exception e) {
                        throw new RuntimeException("SSL context error", e);
                    }
                });

        return new RestHighLevelClient(builder);
    }
}
