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

/**
 * Configuration class for setting up an OpenSearch {@link RestHighLevelClient}.
 * <p>
 * This client is configured with basic authentication, a custom SSL context that trusts all certificates,
 * and disables hostname verification for HTTPS connections.
 */
@Configuration
public class OpenSearchConfig {

    /**
     * Creates and configures a {@link RestHighLevelClient} bean for connecting to an OpenSearch cluster.
     * <p>
     * The client uses:
     * <ul>
     *     <li>HTTPS protocol</li>
     *     <li>Basic authentication with username 'admin' and password 'Aloha.32bit'</li>
     *     <li>A custom SSL context that trusts all certificates</li>
     *     <li>No-op hostname verification (use with caution in production)</li>
     * </ul>
     *
     * @return an instance of {@link RestHighLevelClient} configured to communicate with OpenSearch
     * @throws RuntimeException if the SSL context cannot be created
     */
    @Bean
    public RestHighLevelClient openSearchClient() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("admin", "Aloha.32bit"));

        RestClientBuilder builder = RestClient.builder(new HttpHost("opensearch", 9200, "https"))
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
