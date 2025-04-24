package com.main.Kafka;

import com.main.Dto.RateDto;
import com.main.Kafka.RateEvent.RateEventConsumer;
import com.main.Kafka.RateEvent.RateEventProducer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.List;

public class Kafka {

    private final RateEventConsumer consumer;
    private final RateEventProducer producer;

    public Kafka() {
        this.consumer = new RateEventConsumer();
        this.producer = new RateEventProducer();
    }

    public void produceRateEvent(RateDto rateDto){
        producer.produceRateEvent(rateDto);
    }

    public List<RateDto> consumeRateEvent(){
        return consumer.consumeRateEvent();
    }
}
