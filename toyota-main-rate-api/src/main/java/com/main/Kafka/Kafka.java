package com.main.Kafka;

import com.main.Dto.RateDto;
import com.main.Kafka.RateEvent.RateEventProducer;

import java.util.List;

public class Kafka {

    private final RateEventProducer producer;

    public Kafka() {
        this.producer = new RateEventProducer();
    }

    public void produceRateEvent(RateDto rateDto){
        producer.produceRateEvent(rateDto);
    }

}
