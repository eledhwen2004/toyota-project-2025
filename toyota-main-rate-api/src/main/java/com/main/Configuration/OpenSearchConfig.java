package com.main.Configuration;

import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.http.HttpHost;

/**
 * The OpenSearchConfig class is a Spring configuration class responsible for
 * configuring a client for connecting to an OpenSearch instance.
 * <p>
 * This class provides a {@link RestHighLevelClient} bean that can be injected
 * into Spring components for interacting with OpenSearch services. It is
 * configured to connect to an OpenSearch instance running on localhost at
 * the default port (9200) using HTTP.
 * </p>
 */
@Configuration
public class OpenSearchConfig {

    /**
     * Configures and returns a {@link RestHighLevelClient} instance for interacting
     * with OpenSearch. The client is configured to connect to an OpenSearch node
     * running on localhost at port 9200 using HTTP.
     * <p>
     * This bean can be injected into other Spring components to interact with
     * OpenSearch services.
     * </p>
     *
     * @return A configured {@link RestHighLevelClient} instance for OpenSearch.
     */
    @Bean
    public RestHighLevelClient openSearchClient() {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );
    }
}
