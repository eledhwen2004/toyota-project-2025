package com.main.kafka.serdis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.main.Mapper.RateMapper;
import com.main.entity.RateEntity;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Arrays;

/**
 * Kafka deserializer for converting byte arrays into {@link RateEntity} objects.
 * <p>
 * This class is intended to be used by Kafka consumers that read {@link RateEntity} events.
 * It uses a custom mapper utility to handle deserialization.
 */
public class RateEventDeserializer implements Deserializer<RateEntity> {

    /**
     * Jackson ObjectMapper configured to handle Java 8 date/time types.
     * (Currently unused in actual deserialization logic.)
     */
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * Deserializes a byte array into a {@link RateEntity} object.
     *
     * @param topic the topic associated with the data
     * @param bytes the serialized value
     * @return the deserialized {@link RateEntity} object, or {@code null} if input is null
     */
    @Override
    public RateEntity deserialize(String topic, byte[] bytes) {
        if (bytes == null) return null;
        String formatted = Arrays.toString(bytes);
        return RateMapper.stringToRateEntity(formatted);
    }
}
