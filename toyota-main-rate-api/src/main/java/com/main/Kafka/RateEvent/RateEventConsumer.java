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

public class RateEventConsumer {
    private final Consumer<String, String> consumer;
    private final String topic;

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

