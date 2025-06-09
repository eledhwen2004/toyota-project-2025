package com.main.opensearchService;

import com.main.entity.RateEntity;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.*;
import java.util.*;

import org.opensearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpenSearchService {

    @Autowired
    RestHighLevelClient client;

    public void updateOpensearchRate(List<RateEntity> rateEntities) throws Exception {

        System.out.println("Update OpenSearch Rate");
        for (RateEntity rateEntity : rateEntities) {

            // Create rate doc
            Map<String, Object> doc = new HashMap<>();
            doc.put("name", rateEntity.rateName);
            doc.put("ask", rateEntity.ask);
            doc.put("bid", rateEntity.bid);
            doc.put("update_time", rateEntity.rateUpdateTime);

            // Send to OpenSearch
            IndexRequest request = new IndexRequest("rates").source(doc, XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);

            System.out.printf("Sent %s →  Ask: %.4f Bid: %.4f%n", rateEntity.rateName, rateEntity.ask, rateEntity.bid);
            System.out.println("update_time = " + rateEntity.rateUpdateTime);
        }

    }
}
