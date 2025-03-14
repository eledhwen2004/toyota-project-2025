package com.main.Database;

import com.main.Entity.RateEntity;
import com.main.Kafka.RateEvent.RateEventConsumer;
import com.main.Repository.RateRepository;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostgresqlDatabase{

    @Autowired RateRepository rateRepository;
    @Autowired RateEventConsumer rateEventConsumer;

    public void updateDatabase(){
    }

}
