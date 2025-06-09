package com.main.Kafka.Serdis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.main.Dto.RateDto;
import com.main.Mapper.RateMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;

/**
 * Kafka serializer for converting {@link RateDto} objects into byte arrays.
 * <p>
 * The conversion is done by serializing the DTO into a pipe-delimited string format using {@link RateMapper}.
 * This string is then encoded into a UTF-8 byte array to be sent to Kafka.
 */
public class RateEventSerializer implements Serializer<RateDto> {

    /**
     * Jackson {@link ObjectMapper} configured with JavaTime module.
     * (Note: currently unused; could be used for JSON-based serialization instead of RateMapper.)
     */
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * Serializes a {@link RateDto} into a UTF-8 byte array to send via Kafka.
     *
     * @param topic    the Kafka topic name (not used in this implementation)
     * @param rateDto  the {@link RateDto} object to serialize
     * @return a UTF-8 encoded byte array representing the rate, or {@code null} if input is null
     */
    @Override
    public byte[] serialize(String topic, RateDto rateDto) {
        if (rateDto == null) return null;
        String formatted = RateMapper.rateDtoToString(rateDto);
        return formatted.getBytes(StandardCharsets.UTF_8);
    }
}
