package com.main.kafka;

import com.main.database.PostgresqlDatabase;
import com.main.kafka.event.RateEventConsumer;
import com.main.entity.RateEntity;
import com.main.opensearchService.OpenSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Spring-managed Kafka consumer component responsible for consuming rate events from the "rates" topic.
 * <p>
 * Upon receiving a message, it parses the event into a list of {@link RateEntity} objects,
 * stores them in a PostgreSQL database, and updates the data in OpenSearch.
 */
@Component
public class Consumer {

    /**
     * Service for persisting rate data into PostgreSQL.
     */
    @Autowired
    private PostgresqlDatabase postgresqlDatabase;

    /**
     * Service for updating rate data into OpenSearch.
     */
    @Autowired
    private OpenSearchService openSearchService;

    /**
     * Custom Kafka consumer that retrieves and parses rate events from Kafka.
     */
    private final RateEventConsumer consumer;

    /**
     * Initializes the {@link RateEventConsumer}.
     */
    public Consumer() {
        this.consumer = new RateEventConsumer();
    }

    /**
     * Kafka listener method triggered when a new message arrives on the "rates" topic.
     * <p>
     * Although the Kafka message payload is received as a String, this method ignores it
     * and instead directly pulls records using a custom Kafka consumer implementation.
     *
     * @param message the raw message from Kafka (not used in processing)
     * @throws Exception if any processing or persistence error occurs
     */
    @KafkaListener(topics = "rates", groupId = "rate-group")
    public void consumeRateEvent(String message) throws Exception {
        List<RateEntity> rateEntities = consumer.consumeRateEvent();
        postgresqlDatabase.saveNewRates(rateEntities);
        openSearchService.updateOpensearchRate(rateEntities);
    }
}
