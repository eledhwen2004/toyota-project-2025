package com.main.opensearchService;

import com.main.entity.RateEntity;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.*;
import org.opensearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class responsible for sending rate data to OpenSearch.
 * <p>
 * This class indexes each {@link RateEntity} into the "rates" index in OpenSearch.
 */
@Service
public class OpenSearchService {

    /**
     * OpenSearch client used to communicate with the OpenSearch cluster.
     */
    @Autowired
    private RestHighLevelClient client;

    /**
     * Sends a list of rate entities to the OpenSearch index named "rates".
     * <p>
     * For each {@link RateEntity}, a document is constructed and indexed.
     *
     * @param rateEntities the list of {@link RateEntity} objects to be indexed
     * @throws Exception if any error occurs during indexing
     */
    public void updateOpensearchRate(List<RateEntity> rateEntities) throws Exception {
        System.out.println("Update OpenSearch Rate");

        for (RateEntity rateEntity : rateEntities) {
            // Create rate document
            Map<String, Object> doc = new HashMap<>();
            doc.put("name", rateEntity.rateName);
            doc.put("ask", rateEntity.ask);
            doc.put("bid", rateEntity.bid);
            doc.put("update_time", rateEntity.rateUpdateTime);

            // Send to OpenSearch
            IndexRequest request = new IndexRequest("rates").source(doc, XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);

            // Log to console
            System.out.printf("Sent %s →  Ask: %.4f Bid: %.4f%n", rateEntity.rateName, rateEntity.ask, rateEntity.bid);
            System.out.println("update_time = " + rateEntity.rateUpdateTime);
        }
    }
}
