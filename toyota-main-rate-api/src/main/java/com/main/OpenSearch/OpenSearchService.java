package com.main.OpenSearch;

import com.main.Configuration.OpenSearchConfig;
import com.main.Dto.RateDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.IndexNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Service for interacting with OpenSearch. This service handles the indexing of rate events into OpenSearch.
 * It is responsible for processing rate data (RateDto objects) and updating the corresponding index in OpenSearch.
 * <p>
 * It uses the `RestHighLevelClient` to perform operations like creating and indexing documents.
 * It also logs the events for tracking and debugging purposes.
 * </p>
 */
@Service
public class OpenSearchService {

    private final RestHighLevelClient client;
    private final Logger openSearchLogger = LogManager.getLogger("OpenSearchLogger");

    /**
     * Constructor to initialize the OpenSearch service.
     *
     * @param openSearchConfig Configuration object to initialize OpenSearch client.
     */
    @Autowired
    public OpenSearchService(OpenSearchConfig openSearchConfig) {
        this.client = openSearchConfig.openSearchClient();
    }

    /**
     * Updates the rates in OpenSearch by indexing each rate event from the provided list.
     * This method processes each `RateDto` and sends it to OpenSearch for indexing.
     *
     * @param rateDtoList A list of `RateDto` objects to be indexed in OpenSearch.
     */
    public void updateRates(List <RateDto> rateDtoList) {
        openSearchLogger.info("Rate Event Consumed: " + rateDtoList.size());
        // Process and index each rate event into OpenSearch
        for (RateDto rateDto : rateDtoList) {
            try {
                this.indexRateEvent(rateDto);
            } catch (Exception e) {
                openSearchLogger.error("Error indexing rate event: " + e.getMessage());
            }
        }
        openSearchLogger.info("OpenSearch updated for rates");
    }

    /**
     * Indexes a single rate event (`RateDto`) into OpenSearch.
     * It creates an `IndexRequest` and uses the OpenSearch client to send the request.
     * If the index doesn't exist, it catches the `IndexNotFoundException`.
     *
     * @param rateDto The `RateDto` object representing the rate event to be indexed.
     * @throws IOException If an error occurs during the index request.
     */
    private void indexRateEvent(RateDto rateDto) throws IOException {
        IndexRequest request = new IndexRequest("rates") // Assuming "rates" is your index name
                .id(rateDto.getRateName())  // Using the ID of the RateDto as the document ID in OpenSearch
                .source("rate", rateDto.getRateName(),
                        "bid", rateDto.getBid(),
                        "ask", rateDto.getAsk(),
                        "timestamp", rateDto.getTimestamp()); // Map your fields here

        try {
            client.index(request, RequestOptions.DEFAULT);
            System.out.println("RateDto indexed successfully.");
        } catch (IndexNotFoundException e) {
            openSearchLogger.error("Index not found");
        }
    }

    /**
     * Closes the OpenSearch client and releases any resources.
     *
     * @throws IOException If an error occurs while closing the client.
     */
    public void close() throws IOException {
        client.close();
    }
}
