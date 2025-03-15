package com.main.Kafka.Serdis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.main.Dto.RateDto;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class RateEventDeserializer implements Deserializer<RateDto> {
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public RateDto deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes,RateDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
