package com.main.kafka.event;

import com.main.Mapper.RateMapper;
import com.main.entity.RateEntity;
import org.apache.kafka.clients.consumer.Consumer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.*;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

public class RateEventConsumer {
    private final Consumer<String, String> consumer;
    private final String topic;

    public RateEventConsumer() {
        final Properties props = new Properties() {{
            put(BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
            put(KEY_DESERIALIZER_CLASS_CONFIG,   StringDeserializer.class.getCanonicalName());
            put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
            put(AUTO_OFFSET_RESET_CONFIG, "earliest"); // Read all messages from beginning
            put(GROUP_ID_CONFIG, "kafka-java-getting-started");
        }};
        consumer = new KafkaConsumer<>(props);
        this.topic = "rates";
    }

    public List<RateEntity> consumeRateEvent() {
        List<RateEntity> rateDtoList = new ArrayList<>();
        synchronized (consumer) {
            consumer.subscribe(Arrays.asList(topic));
            ConsumerRecords<String, String > records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                rateDtoList.add(RateMapper.stringToRateEntity(record.value()));
            }
        }
        return rateDtoList;
    }
}

