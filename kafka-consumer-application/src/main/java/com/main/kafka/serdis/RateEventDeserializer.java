package com.main.kafka.serdis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.main.Mapper.RateMapper;
import com.main.entity.RateEntity;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Arrays;

public class RateEventDeserializer implements Deserializer<RateEntity> {
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public RateEntity deserialize(String s, byte[] bytes) {
        if(bytes == null) return null;
        String formatted = Arrays.toString(bytes);
        return RateMapper.stringToRateEntity(formatted);
    }
}
