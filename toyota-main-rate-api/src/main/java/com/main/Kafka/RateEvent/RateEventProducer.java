package com.main.Kafka.RateEvent;

import com.main.Dto.RateDto;
import com.main.Kafka.Serdis.RateEventSerializer;
import com.main.Mapper.RateMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

/**
 * The `RateEventProducer` class is responsible for producing rate events to a Kafka topic.
 * It serializes `RateDto` objects and sends them to the "rates" topic in Kafka for consumption by consumers.
 */
public class RateEventProducer {

    private final Logger logger = LogManager.getLogger("KafkaLogger");
    /**
     * Kafka producer instance used to send messages to Kafka brokers.
     */
    private Producer<String, String> producer;

    /**
     * The Kafka topic to which events are sent.
     */
    private final String topic;

    /**
     * Default constructor that initializes the Kafka producer with the necessary configurations.
     * The producer is configured to send events to a Kafka cluster running on localhost:9092.
     */
    public RateEventProducer() {
        final Properties props = new Properties() {{
            put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
            put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
            put(AUTO_OFFSET_RESET_CONFIG, "earliest"); // Read all messages from the beginning
        }};
        this.producer = new KafkaProducer<>(props);
        this.topic = "rates";
    }

    /**
     * Produces a rate event by serializing the provided `RateDto` object and sending it to the Kafka "rates" topic.
     *
     * @param rateDto The `RateDto` object representing the rate event to be produced.
     */
    public void produceRateEvent(RateDto rateDto) {
        producer.send(new ProducerRecord<>(topic, rateDto.getRateName(), RateMapper.rateDtoToString(rateDto)),
                (event, ex) -> handleCallback(event, ex, topic, rateDto.getRateName(), rateDto));
    }

    /**
     * Handles the callback after attempting to send a message to Kafka. Logs success or failure based on the outcome.
     *
     * @param event   The event that was produced.
     * @param ex      The exception encountered during the production (if any).
     * @param topic   The Kafka topic to which the event was sent.
     * @param rateName The name of the rate that was sent.
     * @param rate    The rate event that was sent.
     */
    private void handleCallback(Object event, Exception ex, String topic, String rateName, RateDto rate) {
        if (ex != null) {
            logger.warn(ex.getMessage());
        } else {
            logger.info("Produced event to topic %s: key = %-10s value = %s%n", topic, rateName, rate.toString());
        }
    }
}
