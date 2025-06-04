package com.main.kafka;

import com.main.database.PostgresqlDatabase;
import com.main.kafka.event.RateEventConsumer;
import com.main.entity.RateEntity;
import com.main.opensearchService.OpenSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Consumer{

    @Autowired PostgresqlDatabase postgresqlDatabase;
    @Autowired OpenSearchService openSearchService;
    private final RateEventConsumer consumer;

    public Consumer() {
        this.consumer = new RateEventConsumer();
    }

    @KafkaListener(topics = "rates", groupId = "rate-group")
    public void consumeRateEvent(String message) {
        List<RateEntity> rateEntities = consumer.consumeRateEvent();
        postgresqlDatabase.saveNewRates(rateEntities);
        openSearchService.indexRate(rateEntities);
    }

}
