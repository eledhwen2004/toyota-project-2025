package com.main.kafka.event;

import com.main.Mapper.RateMapper;
import com.main.entity.RateEntity;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

/**
 * Kafka consumer component for receiving and processing rate events from the "rates" topic.
 * <p>
 * This class listens to Kafka messages, deserializes them, and maps them into {@link RateEntity} objects.
 */
public class RateEventConsumer {

    /**
     * Kafka consumer instance configured to consume string messages.
     */
    private final Consumer<String, String> consumer;

    /**
     * The Kafka topic this consumer listens to.
     */
    private final String topic;

    /**
     * Constructs a {@code RateEventConsumer} with predefined Kafka consumer settings.
     * <ul>
     *     <li>Bootstrap server: kafka:9092</li>
     *     <li>Key/Value deserializer: String</li>
     *     <li>Group ID: kafka-java-getting-started</li>
     *     <li>Offset reset: earliest</li>
     * </ul>
     */
    public RateEventConsumer() {
        final Properties props = new Properties() {{
            put(BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
            put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
            put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
            put(AUTO_OFFSET_RESET_CONFIG, "earliest"); // Read all messages from beginning
            put(GROUP_ID_CONFIG, "kafka-java-getting-started");
        }};
        consumer = new KafkaConsumer<>(props);
        this.topic = "rates";
    }

    /**
     * Consumes rate events from the Kafka topic and maps them to a list of {@link RateEntity} instances.
     *
     * @return list of {@link RateEntity} parsed from Kafka message values
     */
    public List<RateEntity> consumeRateEvent() {
        List<RateEntity> rateEntityList = new ArrayList<>();
        synchronized (consumer) {
            consumer.subscribe(Arrays.asList(topic));
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                rateEntityList.add(RateMapper.stringToRateEntity(record.value()));
            }
        }
        return rateEntityList;
    }
}
