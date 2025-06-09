package com.main.Kafka;

import com.main.Dto.RateDto;
import com.main.Kafka.RateEvent.RateEventProducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Wrapper class for Kafka event publishing.
 * <p>
 * This class encapsulates a {@link RateEventProducer} and provides
 * a safe method to produce {@link RateDto} events to the Kafka topic.
 */
public class Kafka {

    /**
     * Underlying Kafka producer for rate events.
     */
    private final RateEventProducer producer;

    /**
     * Logger for Kafka operations.
     */
    private final Logger logger = LogManager.getLogger("KafkaProducerLogger");

    /**
     * Initializes the Kafka event producer and logs the process.
     */
    public Kafka() {
        logger.info("Initializing Kafka producer");
        this.producer = new RateEventProducer();
        logger.info("Kafka producer initialized successfully");
    }

    /**
     * Produces a rate event to Kafka if the input is valid.
     *
     * @param rateDto the {@link RateDto} object to be sent to Kafka
     */
    public void produceRateEvent(RateDto rateDto) {
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
