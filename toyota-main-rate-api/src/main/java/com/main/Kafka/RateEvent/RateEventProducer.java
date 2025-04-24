package com.main.Kafka.RateEvent;

import com.main.Dto.RateDto;
import com.main.Kafka.Serdis.RateEventSerializer;
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

public class RateEventProducer {

    private final Logger logger = LogManager.getLogger("KafkaLogger");
    private Producer<String, RateDto> producer;
    private final String topic;

    public RateEventProducer() {
        final Properties props = new Properties() {{
            put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            put(KEY_SERIALIZER_CLASS_CONFIG,   StringSerializer.class.getCanonicalName());
            put(VALUE_SERIALIZER_CLASS_CONFIG, RateEventSerializer.class.getCanonicalName());
            put(AUTO_OFFSET_RESET_CONFIG, "earliest"); // ✅ Read all messages from beginning
        }};
        this.producer = new KafkaProducer<>(props);
        this.topic = "rates";
    }

    public void produceRateEvent(RateDto rateDto) {
        producer.send(new ProducerRecord<>(topic, rateDto.getRateName(), rateDto),
                (event, ex) -> handleCallback(event, ex, topic, rateDto.getRateName(), rateDto));

    }

    private void handleCallback(Object event, Exception ex, String topic, String rateName, RateDto rate) {
        if (ex != null) {
            ex.printStackTrace();
        } else {
            System.out.printf("Produced event to topic %s: key = %-10s value = %s%n", topic, rateName, rate.toString());
        }
    }

}
