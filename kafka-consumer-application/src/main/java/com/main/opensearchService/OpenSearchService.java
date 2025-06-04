package com.main.opensearchService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.main.entity.RateEntity;
import org.apache.http.HttpHost;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class OpenSearchService {

    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public OpenSearchService() {
        this.client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );
    }

    public void indexRate(List<RateEntity> rates) {
        for (RateEntity rateEntity : rates) {
            try {
                Map<String, Object> dataMap = objectMapper.convertValue(rateEntity, Map.class);

                IndexRequest request = new IndexRequest("rates-index")
                        .id(String.valueOf(rateEntity.id)) // Optional: specify ID
                        .source(dataMap);

                client.index(request, RequestOptions.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace(); // Optional: log error
            }
        }
    }

    // Optional: çağrıyı kapatmak istersen
    public void close() throws IOException {
        client.close();
    }
}