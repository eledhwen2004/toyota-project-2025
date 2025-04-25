package com.main.Kafka.Serdis;

import com.main.Dto.RateDto;
import com.main.Mapper.RateMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Arrays;

/**
 * The `RateEventDeserializer` class is a Kafka deserializer used for converting
 * byte arrays representing serialized rate events into `RateDto` objects.
 * This class implements the `Deserializer` interface and is designed to handle
 * the deserialization of Kafka messages into `RateDto` objects.
 */
public class RateEventDeserializer implements Deserializer<RateDto> {

    /**
     * Deserializes the given byte array into a `RateDto` object. This method converts
     * the serialized byte array into a string and then maps it to a `RateDto` object
     * using the `RateMapper.stringToRateDto()` method.
     *
     * @param s     The Kafka topic associated with the data being deserialized. This value
     *              is not used in this implementation.
     * @param bytes The serialized byte array to be deserialized into a `RateDto`. If the
     *              byte array is null, the method will return null.
     * @return      A `RateDto` object representing the deserialized data, or null if the
     *              input byte array is null.
     */
    @Override
    public RateDto deserialize(String s, byte[] bytes) {
        if (bytes == null) return null;
        String formatted = Arrays.toString(bytes);
        return RateMapper.stringToRateDto(formatted);
    }
}
