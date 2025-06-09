package com.main.Kafka;

import com.main.Dto.RateDto;
import com.main.Kafka.RateEvent.RateEventProducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Kafka {

    private final RateEventProducer producer;
    private final Logger logger = LogManager.getLogger("KafkaProducerLogger");


    public Kafka() {
        logger.info("Initializing Kafka producer");
        this.producer = new RateEventProducer();
        logger.info("Kafka producer initialized successfully");
    }

    public void produceRateEvent(RateDto rateDto){
        if (rateDto == null) {
            logger.warn("Attempted to produce a null RateDto event");
            return;
        }
        logger.info("Producing rate event for: {}", rateDto.getRateName());
        try {
            producer.produceRateEvent(rateDto);
            logger.info("Rate event successfully produced for: {}", rateDto.getRateName());
        } catch (Exception e) {
            logger.error("Failed to produce rate event for: {} -- error: {}", rateDto.getRateName(), e.getMessage(), e);
        }
    }

}
