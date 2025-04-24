package com.main.Kafka.Serdis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.main.Dto.RateDto;
import com.main.Mapper.RateMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Arrays;

public class RateEventDeserializer implements Deserializer<RateDto> {
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public RateDto deserialize(String s, byte[] bytes) {
        if(bytes == null) return null;
        String formatted = Arrays.toString(bytes);
        return RateMapper.stringToRateDto(formatted);
    }
}
