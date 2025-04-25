package com.main.Kafka;

import com.main.Dto.RateDto;
import com.main.Kafka.RateEvent.RateEventConsumer;
import com.main.Kafka.RateEvent.RateEventProducer;

import java.util.List;

/**
 * The `Kafka` class acts as a wrapper for Kafka event production and consumption related to rate events.
 * It utilizes `RateEventConsumer` to consume events from Kafka and `RateEventProducer` to produce rate events
 * to Kafka. This class provides a simple interface for working with Kafka's message-based architecture
 * for rate-related data.
 */
public class Kafka {

    private final RateEventConsumer consumer;
    private final RateEventProducer producer;

    /**
     * Constructs a new `Kafka` object, initializing the consumer and producer for handling rate events.
     * The consumer listens for incoming rate events from Kafka, and the producer sends rate events to Kafka.
     */
    public Kafka() {
        this.consumer = new RateEventConsumer();
        this.producer = new RateEventProducer();
    }

    /**
     * Produces a rate event to Kafka. The `RateDto` object is sent to the configured Kafka topic.
     *
     * @param rateDto The `RateDto` object that contains the rate event to be sent to Kafka.
     *                It is serialized and published to the Kafka topic.
     */
    public void produceRateEvent(RateDto rateDto) {
        producer.produceRateEvent(rateDto);
    }

    /**
     * Consumes rate events from Kafka. This method polls the Kafka topic for incoming rate events,
     * deserializes them into `RateDto` objects, and returns a list of those rate events.
     *
     * @return A list of `RateDto` objects representing the rate events consumed from Kafka.
     *         If no events are available, the list will be empty.
     */
    public List<RateDto> consumeRateEvent() {
        return consumer.consumeRateEvent();
    }
}
