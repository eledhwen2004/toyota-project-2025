package com.main.Kafka.RateEvent;

import com.main.Dto.RateDto;
import com.main.Kafka.Serdis.RateEventDeserializer;
import com.main.Mapper.RateMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

/**
 * The `RateEventConsumer` class is responsible for consuming rate events from a Kafka topic.
 * It uses a Kafka consumer to read messages from the "rates" topic, deserializes the message content,
 * and maps it to a list of `RateDto` objects for further processing.
 */
public class RateEventConsumer {
    /**
     * The Kafka consumer used to consume messages from the Kafka broker.
     */
    private final Consumer<String, String> consumer;
    /**
     * The Kafka topic from which the consumer reads messages.
     */
    private final String topic;

    /**
     * Default constructor that initializes the Kafka consumer with necessary configurations.
     * The consumer connects to a Kafka cluster running on localhost:9092 and subscribes to the "rates" topic.
     * The consumer is configured to start reading messages from the beginning of the topic if no offset is found.
     */
    public RateEventConsumer() {
        final Properties props = new Properties() {{
            put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            put(KEY_DESERIALIZER_CLASS_CONFIG,   StringDeserializer.class.getCanonicalName());
            put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
            put(AUTO_OFFSET_RESET_CONFIG, "earliest"); // Read all messages from beginning
            put(GROUP_ID_CONFIG, "kafka-java-getting-started");
        }};
        consumer = new KafkaConsumer<>(props);
        this.topic = "rates";
    }

    /**
     * Consumes messages from the "rates" Kafka topic, deserializes them into `RateDto` objects,
     * and returns a list of `RateDto` objects.
     *
     * @return A list of `RateDto` objects that represent the consumed rate events.
     */
    public List<RateDto> consumeRateEvent() {
        List<RateDto> rateDtoList = new ArrayList<>();
        synchronized (consumer) {
            consumer.subscribe(Arrays.asList(topic));
            ConsumerRecords<String, String > records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                rateDtoList.add(RateMapper.stringToRateDto(record.value()));
            }
        }
        return rateDtoList;
    }
}

