package com.main.Kafka.Serdis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.Dto.RateDto;
import com.main.Entity.RateEntity;
import org.apache.kafka.common.serialization.Serializer;

public class RateEventSerializer implements Serializer<RateDto> {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, RateDto data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
