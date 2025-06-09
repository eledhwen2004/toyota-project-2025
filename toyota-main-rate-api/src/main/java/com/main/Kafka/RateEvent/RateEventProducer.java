package com.main.Kafka.RateEvent;

import com.main.Dto.RateDto;
import com.main.Mapper.RateMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

/**
 * Kafka producer responsible for publishing rate events to a Kafka topic.
 * <p>
 * Converts {@link RateDto} objects to string format and sends them to the "rates" topic.
 * Uses custom serialization defined in {@link RateMapper}.
 */
public class RateEventProducer {

    private final Logger logger = LogManager.getLogger("KafkaProducerLogger");
    private final Producer<String, String> producer;
    private final String topic;

    /**
     * Initializes a Kafka producer with default configuration to connect to Kafka at "kafka:9092"
     * and publish messages to the "rates" topic using string key-value serialization.
     */
    public RateEventProducer() {
        final Properties props = new Properties() {{
            put(BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
            put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
            put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
            put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        }};
        this.producer = new KafkaProducer<>(props);
        this.topic = "rates";
    }

    /**
     * Publishes a {@link RateDto} as an event to the Kafka "rates" topic.
     *
     * @param rateDto the rate object to be serialized and sent
     */
    public void produceRateEvent(RateDto rateDto) {
        producer.send(
                new ProducerRecord<>(topic, rateDto.getRateName(), RateMapper.rateDtoToString(rateDto)),
                (event, ex) -> handleCallback(event, ex, topic, rateDto.getRateName(), rateDto)
        );
    }

    /**
     * Internal callback handler for Kafka send operation.
     *
     * @param event     metadata or result (unused)
     * @param ex        exception, if an error occurred during send
     * @param topic     Kafka topic name
     * @param rateName  key of the message
     * @param rate      original rate object being sent
     */
    private void handleCallback(Object event, Exception ex, String topic, String rateName, RateDto rate) {
        if (ex != null) {
            ex.printStackTrace();
        } else {
            logger.info("Produced event to topic {}: key = {} value = {}", topic, rateName, rate.toString());
        }
    }
}
