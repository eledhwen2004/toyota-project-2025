package com.main.Mapper;

import com.main.Dto.RateDto;
import com.main.RateCache.RawRate;

import java.time.Instant;

public class RateMapper {

    RawRate PF1ResponseToRawRate(String response) {
        String [] fields = response.split("\\|");
        String rateName = fields[0];
        double ask = Double.parseDouble(fields[1]);
        double bid = Double.parseDouble(fields[2]);
        Instant timestamp = Instant.ofEpochSecond(Long.parseLong(fields[3]));
        return new RawRate(new RateDto(rateName,ask,bid,timestamp));
    }

    RawRate PF2ResponseToRawRate(RateDto response){
        return new RawRate(response);
    }
}
