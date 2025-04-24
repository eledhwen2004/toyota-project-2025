package com.main.OpenSearch;

import com.main.Configuration.OpenSearchConfig;
import com.main.Dto.RateDto;
import com.main.Entity.RateEntity;
import com.main.Kafka.RateEvent.RateEventConsumer;
import com.main.Mapper.RateMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.IndexNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OpenSearchService {

    private final RestHighLevelClient client;
    private final Logger logger = LogManager.getLogger("OpenSearchLogger");

    @Autowired
    public OpenSearchService(OpenSearchConfig openSearchConfig) {
        this.client = openSearchConfig.openSearchClient();
    }

    public void updateRates(List <RateDto> rateDtoList) {
        logger.info("Rate Event Consumed: " + rateDtoList.size());
        // Process and index each rate event into OpenSearch
        for (RateDto rateDto : rateDtoList) {
            try {
                this.indexRateEvent(rateDto);
            } catch (Exception e) {
                System.err.println("Error indexing rate event: " + e.getMessage());
            }
        }
        logger.info("OpenSearch updated for rates");
    }

    private void indexRateEvent(RateDto rateDto) throws IOException {
        IndexRequest request = new IndexRequest("rates") // Assuming "rates" is your index name
                .id(rateDto.getRateName())  // Using the ID of the RateDto as the document ID in OpenSearch
                .source("rate", rateDto.getRateName(),
                        "bid", rateDto.getBid(),
                        "ask",rateDto.getAsk(),
                        "timestamp", rateDto.getTimestamp()); // Map your fields here

        try {
            client.index(request, RequestOptions.DEFAULT);
            System.out.println("RateDto indexed successfully.");
        } catch (IndexNotFoundException e) {
            System.out.println("Index not found, creating a new one.");
        }
    }

    public void close() throws IOException {
        client.close();
    }
}
