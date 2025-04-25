package com.main.Kafka.Serdis;

import com.main.Dto.RateDto;
import com.main.Mapper.RateMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;

/**
 * The `RateEventSerializer` class is a Kafka serializer used to convert `RateDto` objects into byte arrays
 * for transmission over Kafka. This class implements the `Serializer` interface and defines how to serialize
 * the `RateDto` objects before sending them through Kafka.
 */
public class RateEventSerializer implements Serializer<RateDto> {

    /**
     * Serializes a `RateDto` object into a byte array. The `RateDto` is first converted into a string using
     * `RateMapper.rateDtoToString()`, and then the string is converted into a byte array using UTF-8 encoding.
     *
     * @param topic  The Kafka topic associated with the data being serialized. This parameter is provided by
     *               Kafka but is not used in this implementation.
     * @param rateDto The `RateDto` object to be serialized. If the `RateDto` object is null, the method
     *                returns null.
     * @return A byte array representing the serialized `RateDto` object, or null if the input is null.
     */
    @Override
    public byte[] serialize(String topic, RateDto rateDto) {
        if (rateDto == null) return null;
        String formatted = RateMapper.rateDtoToString(rateDto);
        return formatted.getBytes(StandardCharsets.UTF_8);
    }
}
