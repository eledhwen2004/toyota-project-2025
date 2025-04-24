package com.main.Kafka.Serdis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.main.Dto.RateDto;
import com.main.Mapper.RateMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;

public class RateEventSerializer implements Serializer<RateDto> {
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public byte[] serialize(String topic, RateDto rateDto) {
        if (rateDto == null) return null;
        String formatted = RateMapper.rateDtoToString(rateDto);
        return formatted.getBytes(StandardCharsets.UTF_8);
    }
}
